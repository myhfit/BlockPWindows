package bp.os.window;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sun.jna.Memory;
import com.sun.jna.NativeLong;

import bp.os.process.BPProcessHandler.ProcessInfo;
import bp.os.process.BPProcessHandler_Win;
import bp.util.windows.LIB_INSTS_WIN;
import bp.util.windows.User32;

public class BPWindowHandler_Win
{
	public final static List<WindowInfo> getWindowInfos()
	{
		List<Long> hwnds = new ArrayList<Long>();
		List<WindowInfo> rc = new ArrayList<WindowInfo>();
		User32 user32 = LIB_INSTS_WIN.getUser32();
		NativeLong t = user32.GetDesktopWindow();
		boolean flag = true;
		while (true)
		{
			NativeLong r = LIB_INSTS_WIN.getUser32().GetWindow(t, flag ? 5 : 2);
			if (flag)
				flag = false;
			long hwnd = r.longValue();
			if (hwnd > 0)
			{
				hwnds.add(hwnd);
				t.setValue(hwnd);
			}
			else
				break;
		}
		for (long hwnd : hwnds)
		{
			t.setValue(hwnd);
			char[] ws = new char[256];
			int r = user32.GetClassNameW(t, ws, 256);
			if (r > -1)
			{
				WindowInfo w = new WindowInfo();
				w.hwnd = hwnd;
				{
					String str = new String(ws, 0, r);
					w.classname = (str == null ? "" : str);
					ws = new char[256];
					r = user32.GetWindowTextW(t, ws, 256);
					str = new String(ws, 0, r);
					w.title = (str == null ? "" : str);
				}
				{
					Memory p = new Memory(4);
					try
					{
						if (user32.GetWindowThreadProcessId(new NativeLong(hwnd), p) != 0)
							w.pid = p.getInt(0);
						else
							w.pid = -1;
					}
					finally
					{
						p.clear();
					}
				}
				rc.add(w);
			}
		}
		{
			Set<Integer> pids = new HashSet<Integer>();
			for (WindowInfo w : rc)
			{
				int pid = w.pid;
				if (pid != -1)
					if (!pids.contains(pid))
						pids.add(pid);
			}
			List<ProcessInfo> pinfos = BPProcessHandler_Win.getProcessInfos(new ArrayList<Integer>(pids));
			if (pinfos != null)
			{
				Map<Integer, ProcessInfo> pinfomap = new HashMap<Integer, ProcessInfo>();
				for (ProcessInfo pinfo : pinfos)
					pinfomap.put(pinfo.pid, pinfo);
				for (WindowInfo w : rc)
				{
					int pid = w.pid;
					if (pid != -1)
					{
						ProcessInfo pinfo = pinfomap.get(pid);
						if (pinfo != null)
						{
							w.pinfo = pinfo;
						}
					}
				}
			}
		}
		return rc;
	}

	public final static boolean closeWindow(long hwnd)
	{
		User32 user32 = LIB_INSTS_WIN.getUser32();
		boolean rc = user32.PostMessageW(new NativeLong(hwnd), 0x112, new NativeLong(0xF060), new NativeLong(0));
		return rc;
	}

	public static class WindowInfo
	{
		public long hwnd;
		public String title;
		public String classname;
		public int pid;
		public ProcessInfo pinfo;
	}
}
