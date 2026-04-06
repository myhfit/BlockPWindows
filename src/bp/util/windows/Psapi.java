package bp.util.windows;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.win32.StdCallLibrary;

public interface Psapi extends StdCallLibrary
{
	boolean EnumProcesses(Pointer lpidProcess, int cb, Pointer lpcbNeeded);

	boolean EnumProcessModules(NativeLong hProcess, Pointer lphModule, int cb, Pointer lpcbNeeded);

	int GetModuleBaseNameW(NativeLong hProcess, NativeLong hModule, char[] lpBaseName, int nSize);

	int GetModuleFileNameExW(NativeLong hProcess, NativeLong hModule, char[] lpFilename, int nSize);

	boolean GetProcessMemoryInfo(NativeLong Process, Pointer ppsmemCounters, int cb);

	public static class PROCESS_MEMORY_COUNTERS extends Structure
	{
		public int cb;
		public int PageFaultCount;
		public Pointer PeakWorkingSetSize;
		public Pointer WorkingSetSize;
		public Pointer QuotaPeakPagedPoolUsage;
		public Pointer QuotaPagedPoolUsage;
		public Pointer QuotaPeakNonPagedPoolUsage;
		public Pointer QuotaNonPagedPoolUsage;
		public Pointer PagefileUsage;
		public Pointer PeakPagefileUsage;

		public PROCESS_MEMORY_COUNTERS(Pointer memory)
		{
			super(memory);
			read();
		}

		protected List<String> getFieldOrder()
		{
			return Arrays.asList("cb", "PageFaultCount", "PeakWorkingSetSize", "WorkingSetSize", "QuotaPeakPagedPoolUsage", "QuotaPagedPoolUsage", "QuotaPeakNonPagedPoolUsage", "QuotaNonPagedPoolUsage", "PagefileUsage", "PeakPagefileUsage");
		}
	};

	public static class PROCESS_MEMORY_COUNTERS_EX2 extends Structure
	{
		public int cb;
		public int PageFaultCount;
		public Pointer PeakWorkingSetSize;
		public Pointer WorkingSetSize;
		public Pointer QuotaPeakPagedPoolUsage;
		public Pointer QuotaPagedPoolUsage;
		public Pointer QuotaPeakNonPagedPoolUsage;
		public Pointer QuotaNonPagedPoolUsage;
		public Pointer PagefileUsage;
		public Pointer PeakPagefileUsage;
		public Pointer PrivateUsage;
		public Pointer PrivateWorkingSetSize;
		public long SharedCommitUsage;

		public PROCESS_MEMORY_COUNTERS_EX2()
		{
			super();
		}

		public PROCESS_MEMORY_COUNTERS_EX2(Pointer memory)
		{
			super(memory);
			read();
		}

		protected List<String> getFieldOrder()
		{
			return Arrays.asList("cb", "PageFaultCount", "PeakWorkingSetSize", "WorkingSetSize", "QuotaPeakPagedPoolUsage", "QuotaPagedPoolUsage", "QuotaPeakNonPagedPoolUsage", "QuotaNonPagedPoolUsage", "PagefileUsage", "PeakPagefileUsage",
					"PrivateUsage", "PrivateWorkingSetSize", "SharedCommitUsage");
		}
	}

	public static class ProcessInformation extends Structure
	{
		public NativeLong hProcess;
		public NativeLong hThread;
		public int dwProcessId;
		public int dwThreadId;

		protected List<String> getFieldOrder()
		{
			return Arrays.asList(new String[] { "hProcess", "hThread", "dwProcessId", "dwThreadId" });
		}
	}
}
