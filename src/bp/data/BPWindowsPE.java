package bp.data;

import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import bp.data.reader.BPBytesReader;

public class BPWindowsPE implements BPStructureIO
{
	public IMAGE_DOS_HEADER dosheader;

	@FieldOption(handleReadMethod = "readSignature")
	public byte[] signature;

	public IMAGE_COFF_FILE_HEADER fileheader;

	@FieldOption(handleReadMethod = "readOptionalHeader")
	public IMAGE_OPTIONAL_HEADER optionalheader;

	@FieldOption(handleReadMethod = "readSectionTable")
	public IMAGE_SECTION_HEADER[] sectionheaders;
	
	@FieldOption(handleReadMethod = "getExportTable")
	public IMAGE_EXPORT_DESCRIPTOR  export_table;
	
	@FieldOption(handleReadMethod = "getImportTable")
	public IMAGE_IMPORT_DESCRIPTOR[] import_table;
	
//	public long resource_table;
//	public long exception_table;
//	public long certificate_table;
//	public long base_relocation_table;
//	public long debug;
//	public long architecture;
//	public long global_ptr;
//	public long tls_table;
//	public long load_config_table;
//	public long bound_import;
//	public long iat;
//	public long delay_import_descriptor;
//	public long clr_runtime_header;

	@FieldOption(ignoreRead = true)
	public PE_FILE_SUMMARY summary;

	public void initContext(BPBytesReader reader, BPStructureIOContext context)
	{
		reader.order(ByteOrder.LITTLE_ENDIAN);
	}

	public void readFinished(BPStructureIOContext context)
	{
		summary = new PE_FILE_SUMMARY();
		boolean isok = true;
		{
			byte[] bs = dosheader.e_magic;
			isok = isok & (bs != null && bs.length == 2 && bs[0] == 'M' && bs[1] == 'Z');
			if (isok)
			{
				bs = signature;
				isok = (bs != null && bs.length == 4 && bs[0] == 'P' && bs[1] == 'E' && bs[2] == 0 && bs[3] == 0);
			}
		}
		{
			int machine = fileheader.machine;
			for (IMAGE_FILE_MACHINE m : IMAGE_FILE_MACHINE.values())
			{
				if (m.getCode() == machine)
				{
					summary.machine = m;
					break;
				}
			}
			if (summary.machine == null)
				summary.machine = IMAGE_FILE_MACHINE.UNKNOWN;
		}
		{
			short ch = fileheader.characteristics;
			List<IMAGE_FILE_CHARACTERISTICS> chs = new ArrayList<IMAGE_FILE_CHARACTERISTICS>();
			for (IMAGE_FILE_CHARACTERISTICS c : IMAGE_FILE_CHARACTERISTICS.values())
			{
				if ((ch & c.getCode()) == c.getCode())
				{
					chs.add(c);
				}
			}
			summary.characteristics = chs;
		}
		{
			if (fileheader.size_of_optional_header != 0)
			{
				for (PE_FORMAT pf : PE_FORMAT.values())
				{
					if (pf.getCode() == optionalheader.magic)
					{
						summary.pe_format = pf;
						break;
					}
				}
				for (WINDOWS_SUBSYSTEM ws : WINDOWS_SUBSYSTEM.values())
				{
					if (ws.getCode() == optionalheader.subsystem)
					{
						summary.subsystem = ws;
						break;
					}
				}
				{
					short ch = optionalheader.dll_characteristics;
					List<DLL_CHARACTERISTICS> chs = new ArrayList<DLL_CHARACTERISTICS>();
					for (DLL_CHARACTERISTICS c : DLL_CHARACTERISTICS.values())
					{
						if ((ch & c.getCode()) == c.getCode())
						{
							chs.add(c);
						}
					}
					summary.dllcharacteristics = chs;
				}
			}
		}

		summary.isok = isok;
	}

	public byte[] readSignature(BPBytesReader reader, BPStructureIOContext context) throws Exception
	{
		reader.position(dosheader.e_lfanew);
		byte[] bs = new byte[4];
		reader.get(bs);
		return bs;
	}

	public IMAGE_OPTIONAL_HEADER readOptionalHeader(BPBytesReader reader, BPStructureIOContext context) throws Exception
	{
		if (fileheader.size_of_optional_header == 0)
			return null;
		return (new IMAGE_OPTIONAL_HEADER()).read(reader, context);
	}

	public IMAGE_SECTION_HEADER[] readSectionTable(BPBytesReader reader, BPStructureIOContext context) throws Exception
	{
		int pos = dosheader.e_lfanew + 24 + fileheader.size_of_optional_header;
		reader.position(pos);
		IMAGE_SECTION_HEADER[] rc = new IMAGE_SECTION_HEADER[fileheader.number_of_sections];
		for (int i = 0; i < rc.length; i++)
		{
			rc[i] = new IMAGE_SECTION_HEADER();
			rc[i].read(reader, context);
		}
		return rc;
	}

	public IMAGE_EXPORT_DESCRIPTOR getExportTable(BPBytesReader reader, BPStructureIOContext context) throws Exception
	{
		int rva = optionalheader.data_directory[0].virtual_address;
		IMAGE_EXPORT_DESCRIPTOR rc = null;
		if (rva > 0)
		{
			int pos = (int) rvaToFileOffset(rva);
			reader.position((int) pos);
			rc = new IMAGE_EXPORT_DESCRIPTOR();
			rc.read(reader, context);

			List<String> strs = new ArrayList<>();

			reader.position((int) rvaToFileOffset(rc.name_pointer_rva));
			int[] nameaddrs = new int[rc.number_of_name_pointers];
			for (int i = 0; i < rc.number_of_name_pointers; i++)
			{
				nameaddrs[i] = reader.getInt();
			}
			for (int i = 0; i < rc.number_of_name_pointers; i++)
			{
				reader.position(nameaddrs[i]);
				strs.add(readCStr(reader));
			}
			rc.namestrs = strs;
		}
		return rc;
	}

	public IMAGE_IMPORT_DESCRIPTOR[] getImportTable(BPBytesReader reader, BPStructureIOContext context) throws Exception
	{
		int rva = optionalheader.data_directory[1].virtual_address;
		int size = optionalheader.data_directory[1].size;
		IMAGE_IMPORT_DESCRIPTOR[] rc = null;
		if (rva != 0)
		{
			int pos = (int) rvaToFileOffset(rva);
			reader.position((int) pos);
			int count = size / 20;
			{
				List<IMAGE_IMPORT_DESCRIPTOR> descs = new ArrayList<IMAGE_IMPORT_DESCRIPTOR>();
				for (int i = 0; i < count; i++)
				{
					IMAGE_IMPORT_DESCRIPTOR desc = new IMAGE_IMPORT_DESCRIPTOR();
					desc.read(reader, context);
					if (desc.namerva == 0)
						break;
					descs.add(desc);
				}
				count = descs.size();
				rc = descs.toArray(new IMAGE_IMPORT_DESCRIPTOR[descs.size()]);
			}
			for (int i = 0; i < count; i++)
			{
				if (rc[i].namerva != 0)
				{
					int namerva = rc[i].namerva;
					pos = (int) rvaToFileOffset(namerva);
					reader.position(pos);
					rc[i].namestr = readCStr(reader);
				}
			}
		}
		return rc;
	}

	public long rvaToFileOffset(long rva)
	{
		for (int i = 0; i < fileheader.number_of_sections; i++)
		{
			IMAGE_SECTION_HEADER section = sectionheaders[i];
			if (rva >= section.virtual_address && rva < section.virtual_address + section.virtual_size)
			{
				return rva - section.virtual_address + section.pointer_to_raw_data;
			}
		}
		return 0;
	}

	public IMAGE_SECTION_HEADER getRDataSection()
	{
		for (IMAGE_SECTION_HEADER h : sectionheaders)
		{
			if (".rdata".equals(h.name))
				return h;
		}
		return null;
	}

	public static class IMAGE_DOS_HEADER implements BPStructureIO
	{
		@FieldOption(arrLength = 2)
		public byte[] e_magic;
		public short e_cblp;
		public short e_cp;
		public short e_crlc;
		public short e_cparhdr;
		public short e_minalloc;
		public short e_maxalloc;
		public short e_ss;
		public short e_sp;
		public short e_csum;
		public short e_ip;
		public short e_cs;
		public short e_lfarlc;
		public short e_ovno;
		@FieldOption(arrLength = 4)
		public short e_res[];
		public short e_oemid;
		public short e_oeminfo;
		@FieldOption(arrLength = 10)
		public short e_res2[];
		public int e_lfanew;
	}

	public static class IMAGE_COFF_FILE_HEADER implements BPStructureIO
	{
		public short machine;
		public short number_of_sections;
		public int time_date_stamp;
		public int pointer_to_symbol_table;
		public int number_of_symbols;
		public short size_of_optional_header;
		public short characteristics;
	}

	public static class IMAGE_OPTIONAL_HEADER implements BPStructureIO
	{
		public short magic;

		public byte major_linker_version;
		public byte minor_linker_version;
		public int size_of_code;
		public int size_of_initialized_data;
		public int size_of_uninitialized_data;
		public int address_of_entry_point;
		public int base_of_code;
		@FieldOption(handleReadMethod = "readBaseOfData")
		public int base_of_data;

		@FieldOption(handleReadValueMethod = "readPE32Long")
		public long image_base;
		public int section_alignment;
		public int file_alignment;
		public short major_operating_system_version;
		public short minor_operating_system_version;
		public short major_image_version;
		public short minor_image_version;
		public short major_subsystem_version;
		public short minor_subsystem_version;
		public int win32_version_value;
		public int size_of_image;
		public int size_of_headers;
		public int check_sum;
		public short subsystem;
		public short dll_characteristics;
		@FieldOption(handleReadValueMethod = "readPE32Long")
		public long size_of_stack_reserve;
		@FieldOption(handleReadValueMethod = "readPE32Long")
		public long size_of_stack_commit;
		@FieldOption(handleReadValueMethod = "readPE32Long")
		public long size_of_heap_reserve;
		@FieldOption(handleReadValueMethod = "readPE32Long")
		public long size_of_Heap_commit;
		public int loader_flags;
		public int number_of_rva_and_sizes;

		@FieldOption(handleReadValueMethod = "readDir", arrLengthField = "number_of_rva_and_sizes")
		public IMAGE_DATA_DIRECTORY[] data_directory;

		public int readBaseOfData(BPBytesReader reader, BPStructureIOContext context) throws Exception
		{
			if (magic == 0x20B)
				return 0;
			else
				return reader.getInt();
		}

		public long readPE32Long(BPBytesReader reader, BPStructureIOContext context) throws Exception
		{
			if (magic == 0x20B)
				return reader.getLong();
			else
				return reader.getInt();
		}

		public IMAGE_DATA_DIRECTORY readDir(BPBytesReader reader, BPStructureIOContext context) throws Exception
		{
			IMAGE_DATA_DIRECTORY rc = new IMAGE_DATA_DIRECTORY();
			rc.read(reader, context);
			return rc;
		}

		public IMAGE_DATA_DIRECTORY getDir(HEADER_TABLE t)
		{
			int idx = t.ordinal() - HEADER_TABLE.EXPORT_TABLE.ordinal();
			if (data_directory != null && data_directory.length > idx)
				return data_directory[idx];
			return null;
		}
	}

	public static enum HEADER_TABLE
	{
		EXPORT_TABLE, IMPORT_TABLE, RESOURCE_TABLE, EXCEPTION_TABLE, CERTIFICATE_TABLE, BASE_RELOCATION_TABLE, DEBUG, ARCHITECTURE, GLOBAL_PTR, TLS_TABLE, 
		LOAD_CONFIG_TABLE, BOUND_IMPORT, IAT, DELAY_IMPORT_DECRIPTOR, CLR_RUNTIME_HEADER;
	}

	public static class IMAGE_SECTION_HEADER implements BPStructureIO
	{
		@FieldOption(handleReadValueMethod = "readName")
		public String name;
		public int virtual_size;
		public int virtual_address;
		public int size_of_raw_data;
		public int pointer_to_raw_data;
		public int pointer_to_relocations;
		public int pointer_to_linenumbers;
		public short number_of_relocations;
		public short number_of_linenumbers;
		public int characteristics;

		public String readName(BPBytesReader reader, BPStructureIOContext context) throws Exception
		{
			byte[] bs = new byte[8];
			reader.get(bs);
			return new String(bs, "utf-8");
		}
	}

	public static class IMAGE_DATA_DIRECTORY implements BPStructureIO
	{
		public int virtual_address;
		public int size;
	}

	public static class IMAGE_EXPORT_DESCRIPTOR implements BPStructureIO
	{
		public int export_flags;
		public int time_date_stamp;
		public short major_version;
		public short minor_version;
		public int namerva;
		public int ordinal_base;
		public int address_table_entries;
		public int number_of_name_pointers;
		public int export_address_table_rva;
		public int name_pointer_rva;
		public int ordinal_table_rva;
		
		@FieldOption(ignoreRead = true)
		public List<String> namestrs;
	}

	public static class IMAGE_IMPORT_DESCRIPTOR implements BPStructureIO
	{
		public int import_lookup_table_rva;
		public int time_date_stamp;
		public int forwarder_chain;
		public int namerva;
		public int import_address_table_rva;
		
		@FieldOption(ignoreRead = true)
		public String namestr;
	}

	public static class PE_FILE_SUMMARY
	{
		public boolean isok;
		public IMAGE_FILE_MACHINE machine;
		public List<IMAGE_FILE_CHARACTERISTICS> characteristics;
		public PE_FORMAT pe_format;
		public WINDOWS_SUBSYSTEM subsystem;
		public List<DLL_CHARACTERISTICS> dllcharacteristics;
	}

	public static enum IMAGE_FILE_MACHINE
	{
		UNKNOWN(0), ALPHA(0x184), ALPHA64(0x284), AM33(0x1d3), AMD64(0x8664), ARM(0x1c0), ARM64(0xaa64), ARMNT(0x1c4), AXP64(0x284), EBC(0xebc), I386(0x14c), IA64(0x200), 
		LOONGARCH32(0x6232), LOONGARCH64(0x6264), M32R(0x9041), MIPS16(0x266), MIPSFPU(0x366), MIPSFPU16(0x466), POWERPC(0x1f0), POWERPCFP(0x1f1), R3000BE(0x160), 
		R3000(0x162), R4000(0x166), R10000(0x168), RISCV32(0x5032), RISCV64(0x5064), RISCV128(0x5128), SH3(0x1a2), SH3DSP(0x1a3), SH4(0x1a6), SH5(0x1a8), THUMB(0x1c2), WCEMIPSV2(0x169);

		private short m_code;

		private IMAGE_FILE_MACHINE(int code)
		{
			m_code = (short) code;
		}

		public short getCode()
		{
			return m_code;
		}
	}

	public static enum IMAGE_FILE_CHARACTERISTICS
	{
		RELOCS_STRIPPED(0x0001), EXECUTABLE_IMAGE(0x0002), LINE_NUMS_STRIPPED(0x0004), LOCAL_SYMS_STRIPPED(0x0008), AGGRESSIVE_WS_TRIM(0x0010), 
		LARGE_ADDRESS_AWARE(0x0020), REVERSED(0x0040), BYTES_REVERSED_LO(0x0080), MACHINE_32BIT(0x0100), DEBUG_STRIPPED(0x0200), REMOVABLE_RUN_FROM_SWAP(0x0400), 
		NET_RUN_FROM_SWAP(0x0800), SYSTEM(0x1000), DLL(0x2000), UP_SYSTEM_ONLY(0x4000), BYTES_REVERSED_HI(0x8000);

		private short m_code;

		private IMAGE_FILE_CHARACTERISTICS(int code)
		{
			m_code = (short) code;
		}

		public short getCode()
		{
			return m_code;
		}
	}

	public static enum PE_FORMAT
	{
		PE32(0x10B), PE32PLUS(0x20B);

		private short m_code;

		private PE_FORMAT(int code)
		{
			m_code = (short) code;
		}

		public short getCode()
		{
			return m_code;
		}
	}

	public static enum WINDOWS_SUBSYSTEM
	{
		UNKNOWN(0), NATIVE(1), WINDOWS_GUI(2), WINDOWS_CUI(3), OS2_CUI(5), POSIX_CUI(7), NATIVE_WINDOWS(8), WINDOWS_CE_GUI(9), EFI_APPLICATION(10), 
		EFI_BOOT_SERVICE_DRIVER(11), EFI_RUNTIME_DRIVER(12), EFI_ROM(13), XBOX(14), WINDOWS_BOOT_APPLICATION(
				16);

		private short m_code;

		private WINDOWS_SUBSYSTEM(int code)
		{
			m_code = (short) code;
		}

		public short getCode()
		{
			return m_code;
		}
	}

	public static enum DLL_CHARACTERISTICS
	{
		HIGH_ENTROPY_VA(0x0020), DYNAMIC_BASE(0x0040), FORCE_INTEGRITY(0x0080), NX_COMPAT(0x0100), NO_ISOLATION(0x0200), NO_SEH(0x0400), NO_BIND(0x0800), 
		APPCONTAINER(0x1000), WDM_DRIVER(0x2000), GUARD_CF(0x4000), TERMINAL_SERVER_AWARE(0x8000);

		private short m_code;

		private DLL_CHARACTERISTICS(int code)
		{
			m_code = (short) code;
		}

		public short getCode()
		{
			return m_code;
		}
	}
}
