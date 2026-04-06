package bp.os;

import com.sun.jna.WString;

import bp.util.Std;
import bp.util.TextUtil;
import bp.util.windows.Kernel32;
import bp.util.windows.Kernel32.StartupInfoW;
import bp.util.windows.LIB_INSTS_WIN;
import bp.util.windows.Psapi.ProcessInformation;

public class BPOSHandlers_Win
{
	public final static int runSimple(String cmd, String workdir, String[] args)
	{
		return run(null, cmd, workdir, args, null, true);
	}

	public final static int startSimple(String cmd, String workdir, String[] args)
	{
		return run(null, cmd, workdir, args, null, false);
	}

	public final static int run(String applicationname, String cmdline, String workdir, String[] args, String title, boolean issub)
	{
		Kernel32 k32 = LIB_INSTS_WIN.getKernel32();
		String cmdlinestr = cmdline + (args == null ? "" : (" " + String.join(" ", args)));

		ProcessInformation processInformation = new ProcessInformation();
		StartupInfoW startupInfo = new StartupInfoW();
		startupInfo.lpTitle = title == null ? null : new WString(title);
		startupInfo.cb = startupInfo.size();

		boolean r = k32.CreateProcessW(TextUtil.toCStyleChar(applicationname), TextUtil.toCStyleChar(cmdlinestr), null, null, false, issub ? 0x00000010 : 0x00000210, null, TextUtil.toCStyleChar(workdir), startupInfo, processInformation);
		if (r)
		{
			return processInformation.hProcess.intValue();
		}
		else
		{
			int err = k32.GetLastError();
			Std.err("lastdllerror:" + err);
			return -1;
		}
	}
}
