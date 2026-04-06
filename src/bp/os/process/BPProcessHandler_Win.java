package bp.os.process;

import java.util.ArrayList;
import java.util.List;

import com.sun.jna.Memory;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;

import bp.util.windows.Kernel32;
import bp.util.windows.LIB_INSTS_WIN;
import bp.util.windows.Psapi;
import bp.util.windows.Psapi.PROCESS_MEMORY_COUNTERS;
import bp.util.windows.Psapi.PROCESS_MEMORY_COUNTERS_EX2;

public class BPProcessHandler_Win implements BPProcessHandler
{
	public final static List<ProcessInfo> getProcessInfos()
	{
		List<ProcessInfo> rc = new ArrayList<ProcessInfo>();
		Psapi psapi = LIB_INSTS_WIN.getPsapi();
		Kernel32 k32 = LIB_INSTS_WIN.getKernel32();
		int cachesize = 4096 << 2;
		Memory ppids = new Memory(cachesize);
		Memory pcb = new Memory(4);
		try
		{
			if (psapi.EnumProcesses(ppids, cachesize, pcb))
			{
				int[] pids = ppids.getIntArray(0, pcb.getInt(0) / 4);
				for (int pid : pids)
				{
					rc.add(getProcessInfoInner(pid, k32, psapi));
				}
			}
		}
		finally
		{
			pcb.clear();
			ppids.clear();
		}
		return rc;
	}

	public final static ProcessInfo getProcessInfo(int pid)
	{
		Psapi psapi = LIB_INSTS_WIN.getPsapi();
		Kernel32 k32 = LIB_INSTS_WIN.getKernel32();
		return getProcessInfoInner(pid, k32, psapi);
	}

	public final static List<ProcessInfo> getProcessInfos(List<Integer> pids)
	{
		Psapi psapi = LIB_INSTS_WIN.getPsapi();
		Kernel32 k32 = LIB_INSTS_WIN.getKernel32();
		List<ProcessInfo> rc = new ArrayList<ProcessInfo>();
		for (int pid : pids)
		{
			rc.add(getProcessInfoInner(pid, k32, psapi));
		}
		return rc;
	}

	protected final static ProcessInfo getProcessInfoInner(int pid, Kernel32 k32, Psapi psapi)
	{
		ProcessInfo p = new ProcessInfo();
		p.pid = pid;
		NativeLong hProcess = k32.OpenProcess(0x0410, false, pid);
		if (hProcess.longValue() != 0)
		{
			Memory hMod = new Memory(NativeLong.SIZE * 20);
			Memory cbNeeded = new Memory(4);
			try
			{
				if (psapi.EnumProcessModules(hProcess, hMod, NativeLong.SIZE * 20, cbNeeded))
				{
					char[] pname = new char[1025];
					int l = psapi.GetModuleFileNameExW(hProcess, new NativeLong(0), pname, 1024);
					p.filename = new String(pname, 0, l);

					PROCESS_MEMORY_COUNTERS_EX2 tmc = new PROCESS_MEMORY_COUNTERS_EX2();
					Memory mmc = new Memory(tmc.size());
					if (psapi.GetProcessMemoryInfo(hProcess, mmc, tmc.size()))
					{
						int cb = mmc.getInt(0);
						if (cb >= tmc.size())
						{
							tmc = new PROCESS_MEMORY_COUNTERS_EX2(mmc);
							p.mem_used = Pointer.nativeValue(tmc.PrivateWorkingSetSize);
						}
						else
						{
							PROCESS_MEMORY_COUNTERS tmc2 = new PROCESS_MEMORY_COUNTERS(mmc);
							p.mem_used = Pointer.nativeValue(tmc2.WorkingSetSize);
						}
					}
				}
			}
			finally
			{
				hMod.clear();
				cbNeeded.clear();
			}
			k32.CloseHandle(hProcess);
		}
		else
		{
		}
		return p;
	}
}
