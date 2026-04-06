package bp.util.windows;

import com.sun.jna.Native;

public class LIB_INSTS_WIN
{
	private static volatile Kernel32 S_INST_KERNEL32 = null;
	private static volatile User32 S_INST_USER32 = null;
	private static volatile Psapi S_INST_PSAPI = null;
	private static volatile Advapi32 S_INST_ADVAPI32 = null;
	private static volatile Dwmapi S_INST_DWMAPI = null;

	public final static Kernel32 getKernel32()
	{
		if (S_INST_KERNEL32 != null)
			return S_INST_KERNEL32;
		synchronized (Kernel32.class)
		{
			if (S_INST_KERNEL32 == null)
				S_INST_KERNEL32 = (Kernel32) Native.loadLibrary("kernel32", Kernel32.class);
		}
		return S_INST_KERNEL32;
	}

	public final static Psapi getPsapi()
	{
		if (S_INST_PSAPI != null)
			return S_INST_PSAPI;
		synchronized (Psapi.class)
		{
			if (S_INST_PSAPI == null)
				S_INST_PSAPI = (Psapi) Native.loadLibrary("psapi", Psapi.class);
		}
		return S_INST_PSAPI;
	}

	public final static User32 getUser32()
	{
		if (S_INST_USER32 != null)
			return S_INST_USER32;
		synchronized (User32.class)
		{
			if (S_INST_USER32 == null)
				S_INST_USER32 = (User32) Native.loadLibrary("user32", User32.class);
		}
		return S_INST_USER32;
	}

	public final static Advapi32 getAdvapi32()
	{
		if (S_INST_ADVAPI32 != null)
			return S_INST_ADVAPI32;
		synchronized (Advapi32.class)
		{
			if (S_INST_ADVAPI32 == null)
				S_INST_ADVAPI32 = (Advapi32) Native.loadLibrary("advapi32", Advapi32.class);
		}
		return S_INST_ADVAPI32;
	}

	public final static Dwmapi getDwmapi()
	{
		if (S_INST_DWMAPI != null)
			return S_INST_DWMAPI;
		synchronized (Dwmapi.class)
		{
			if (S_INST_DWMAPI == null)
				S_INST_DWMAPI = (Dwmapi) Native.loadLibrary("dwmapi", Dwmapi.class);
		}
		return S_INST_DWMAPI;
	}
}
