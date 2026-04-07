package bp.nativehelper.windows;

import com.sun.jna.Native;
import com.sun.jna.win32.StdCallLibrary;

public class Kernel32HelperJNA implements Kernel32Helper
{
	private static Kernel32 INST = (Kernel32) Native.loadLibrary("kernel32", Kernel32.class);

	public int setThreadExecutionState(int esFlags)
	{
		return INST.SetThreadExecutionState(esFlags);
	}

	private static interface Kernel32 extends StdCallLibrary
	{
		int SetThreadExecutionState(int esFlags);
	}
}
