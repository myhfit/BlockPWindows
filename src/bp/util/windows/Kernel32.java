package bp.util.windows;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.WString;
import com.sun.jna.win32.StdCallLibrary;

public interface Kernel32 extends StdCallLibrary
{
	public static class FILETIME extends Structure
	{
		public int dwLowDateTime;
		public int dwHighDateTime;

		protected List<String> getFieldOrder()
		{
			return Arrays.asList("dwLowDateTime", "dwHighDateTime");
		}
	}

	boolean GetSystemTimes(FILETIME lpIdleTime, FILETIME lpKernelTime, FILETIME lpUserTime);

	public static class MEMORYSTATUSEX extends Structure
	{
		public int dwLength;
		public int dwMemoryLoad;
		public long ullTotalPhys;
		public long ullAvailPhys;
		public long ullTotalPageFile;
		public long ullAvailPageFile;
		public long ullTotalVirtual;
		public long ullAvailVirtual;
		public long ullAvailExtendedVirtual;

		protected List<String> getFieldOrder()
		{
			return Arrays.asList("dwLength", "dwMemoryLoad", "ullTotalPhys", "ullAvailPhys", "ullTotalPageFile", "ullAvailPageFile", "ullTotalVirtual", "ullAvailVirtual", "ullAvailExtendedVirtual");
		}
	}

	boolean GlobalMemoryStatusEx(MEMORYSTATUSEX lpBuffer);

	boolean GetVersionExW(OSVERSIONINFOEX info);

	public static class OSVERSIONINFOEX extends Structure
	{
		public int dwOSVersionInfoSize;
		public int dwMajorVersion;
		public int dwMinorVersion;
		public int dwBuildNumber;
		public int dwPlatformId;
		public char[] szCSDVersion = new char[128];
		public short wServicePackMajor;
		public short wServicePackMinor;
		public short wSuiteMask;
		public byte wProductType;
		public byte wReserved;

		protected List<String> getFieldOrder()
		{
			return Arrays.asList("dwOSVersionInfoSize", "dwMajorVersion", "dwMinorVersion", "dwBuildNumber", "dwPlatformId", "szCSDVersion", "wServicePackMajor", "wServicePackMinor", "wSuiteMask", "wProductType", "wReserved");
		}
	}

	public static class StartupInfoW extends Structure
	{
		public int cb;
		public WString lpReserved;
		public WString lpDesktop;
		public WString lpTitle;
		public int dwX;
		public int dwY;
		public int dwXSize;
		public int dwYSize;
		public int dwXCountChars;
		public int dwYCountChars;
		public int dwFillAttribute;
		public int dwFlags;
		public short wShowWindow;
		public short cbReserved2;
		public Pointer lpReserved2;
		public Pointer hStdInput;
		public Pointer hStdOutput;
		public Pointer hStdError;

		protected List<String> getFieldOrder()
		{
			return Arrays.asList(new String[] { "cb", "lpReserved", "lpDesktop", "lpTitle", "dwX", "dwY", "dwXSize", "dwYSize", "dwXCountChars", "dwYCountChars", "dwFillAttribute", "dwFlags", "wShowWindow", "cbReserved2", "lpReserved2", "hStdInput",
					"hStdOutput", "hStdError" });
		}
	}

	boolean CreateProcessW(char[] lpApplicationName, char[] lpCommandLine, Structure lpProcessAttributes, Structure lpThreadAttributes, boolean bInheritHandles, int dwCreationFlags, Structure lpEnvironment, char[] lpCurrentDirectory,
			StartupInfoW lpStartupInfo, Structure lpProcessInformation);

	boolean CloseHandle(NativeLong hObject);

	NativeLong OpenProcess(int dwDesiredAccess, boolean bInheritHandle, int dwProcessId);

	NativeLong GetCurrentProcess();

    Pointer LocalFree(Pointer hMem);
    
	int GetLastError();
}
