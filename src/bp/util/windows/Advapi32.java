package bp.util.windows;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Memory;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.ByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;

public interface Advapi32 extends StdCallLibrary
{
	boolean GetUserNameW(Pointer lpBuffer, Pointer pcbBuffer);

	boolean OpenProcessToken(NativeLong ProcessHandle, int DesiredAccess, Pointer TokenHandle);

	public static class TOKEN_USER extends Structure
	{
		public SID_AND_ATTRIBUTES User;

		public TOKEN_USER()
		{
			super();
		}

		public TOKEN_USER(Pointer memory)
		{
			super(memory);
			read();
		}

		public TOKEN_USER(int size)
		{
			super(new Memory(size));
		}

		protected List<String> getFieldOrder()
		{
			return Arrays.asList("User");
		}
	}

	public static class SID_AND_ATTRIBUTES extends Structure
	{
		public PSID.ByReference Sid;

		public int Attributes;

		public SID_AND_ATTRIBUTES()
		{
			super();
		}

		public SID_AND_ATTRIBUTES(Pointer memory)
		{
			super(memory);
		}

		protected List<String> getFieldOrder()
		{
			return Arrays.asList("Sid", "Attributes");
		}
	}

	public static class PSID extends Structure
	{
		public static class ByReference extends PSID implements Structure.ByReference
		{
		}

		public Pointer sid;

		public PSID()
		{
			super();
		}

		public PSID(byte[] data)
		{
			super(new Memory(data.length));
			getPointer().write(0, data, 0, data.length);
			read();
		}

		public PSID(int size)
		{
			super(new Memory(size));
		}

		public PSID(Pointer memory)
		{
			super(memory);
			read();
		}

		public byte[] getBytes()
		{
			int len = LIB_INSTS_WIN.getAdvapi32().GetLengthSid(this);
			return getPointer().getByteArray(0, len);
		}

		protected List<String> getFieldOrder()
		{
			return Arrays.asList("sid");
		}
	}

	public static class PSIDByReference extends ByReference
	{
		public PSIDByReference()
		{
			this(null);
		}

		public PSIDByReference(PSID h)
		{
			super(Pointer.SIZE);
			setValue(h);
		}

		public void setValue(PSID h)
		{
			getPointer().setPointer(0, h != null ? h.getPointer() : null);
		}

		public PSID getValue()
		{
			Pointer p = getPointer().getPointer(0);
			if (p == null)
			{
				return null;
			}
			else
			{
				return new PSID(p);
			}
		}
	}

	public static class TOKEN_GROUPS extends Structure
	{
		public int GroupCount;
		public SID_AND_ATTRIBUTES Group0;

		public TOKEN_GROUPS()
		{
			super();
		}

		public TOKEN_GROUPS(Pointer memory)
		{
			super(memory);
			read();
		}

		public TOKEN_GROUPS(int size)
		{
			super(new Memory(size));
		}

		protected List<String> getFieldOrder()
		{
			return Arrays.asList("GroupCount", "Group0");
		}
	}

	int GetLengthSid(PSID pSid);

	boolean GetTokenInformation(NativeLong TokenHandle, int TokenInformationClass, Pointer TokenInformation, int TokenInformationLength, IntByReference dwSize);

	boolean ConvertSidToStringSidW(PSID Sid, PointerByReference StringSid);

	boolean LookupAccountSidW(String lpSystemName, PSID Sid, char[] lpName, IntByReference cchName, char[] ReferencedDomainName, IntByReference cchReferencedDomainName, PointerByReference peUse);
}
