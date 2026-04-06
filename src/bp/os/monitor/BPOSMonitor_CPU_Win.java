package bp.os.monitor;

import bp.os.monitor.BPOSMonitor.BPOSMonitor_CPU;
import bp.util.windows.Kernel32.FILETIME;
import bp.util.windows.LIB_INSTS_WIN;

public class BPOSMonitor_CPU_Win implements BPOSMonitor_CPU
{
	protected volatile long idletime;
	protected volatile long kerneltime;
	protected volatile long usertime;

	protected volatile double usage;

	public void start()
	{
		FILETIME ftidle = new FILETIME();
		FILETIME ftkernel = new FILETIME();
		FILETIME ftuser = new FILETIME();

		LIB_INSTS_WIN.getKernel32().GetSystemTimes(ftidle, ftkernel, ftuser);
		idletime = getTimeFromFileTime(ftidle);
		kerneltime = getTimeFromFileTime(ftkernel);
		usertime = getTimeFromFileTime(ftuser);

		ftidle.clear();
		ftkernel.clear();
		ftuser.clear();
	}

	protected long getTimeFromFileTime(FILETIME ft)
	{
		return ((Integer.toUnsignedLong(ft.dwHighDateTime) << 32) | Integer.toUnsignedLong(ft.dwLowDateTime));
	}

	public void stop()
	{
	}

	public double getCPUUsage()
	{
		return usage;
	}

	public void tick()
	{
		FILETIME ftidle = new FILETIME();
		FILETIME ftkernel = new FILETIME();
		FILETIME ftuser = new FILETIME();
		LIB_INSTS_WIN.getKernel32().GetSystemTimes(ftidle, ftkernel, ftuser);
		long i = getTimeFromFileTime(ftidle);
		long k = getTimeFromFileTime(ftkernel);
		long u = getTimeFromFileTime(ftuser);

		long di = i - idletime;
		long dk = k - kerneltime;
		long du = u - usertime;
		double rc = (double) (du + dk - di) / (double) (du + dk);

		idletime = i;
		kerneltime = k;
		usertime = u;

		ftidle.clear();
		ftkernel.clear();
		ftuser.clear();

		usage = rc;
	}
}
