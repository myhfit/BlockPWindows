package bp.os.monitor;

import bp.os.monitor.BPOSMonitor.BPOSMonitor_Memory;
import bp.util.windows.Kernel32.MEMORYSTATUSEX;
import bp.util.windows.LIB_INSTS_WIN;

public class BPOSMonitor_Memory_Win implements BPOSMonitor_Memory
{
	protected volatile long[] m_ms = new long[6];

	public void tick()
	{
		MEMORYSTATUSEX ms = new MEMORYSTATUSEX();
		ms.dwLength = ms.size();
		LIB_INSTS_WIN.getKernel32().GlobalMemoryStatusEx(ms);
		m_ms[0] = ms.ullAvailPhys;
		m_ms[1] = ms.ullTotalPhys;
		m_ms[2] = ms.ullAvailPageFile;
		m_ms[3] = ms.ullTotalPageFile;
		m_ms[4] = ms.ullAvailVirtual;
		m_ms[5] = ms.ullTotalVirtual;

		ms.clear();
	}

	public long[] getMemoryStatus()
	{
		long[] ms = m_ms;
		long[] rc = new long[ms.length];
		System.arraycopy(ms, 0, rc, 0, 6);
		return rc;
	}
}
