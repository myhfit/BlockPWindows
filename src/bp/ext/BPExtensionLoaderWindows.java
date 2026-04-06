package bp.ext;

import bp.nativehelper.BPNativeHelperFFM;
import bp.nativehelper.BPNativeHelperJNA;
import bp.nativehelper.BPNativeHelpers;
import bp.nativehelper.windows.DwmapiHelper;
import bp.nativehelper.windows.DwmapiHelperFFM;
import bp.nativehelper.windows.DwmapiHelperJNA;
import bp.nativehelper.windows.Kernel32Helper;
import bp.nativehelper.windows.Kernel32HelperFFM;
import bp.nativehelper.windows.Kernel32HelperJNA;
import bp.os.BPOSHandlers;
import bp.os.BPOSHandlers_Win;
import bp.os.monitor.BPOSMonitor_CPU_Win;
import bp.os.monitor.BPOSMonitor_Memory_Win;
import bp.os.monitor.BPOSMonitors;
import bp.os.process.BPProcessHandler_Win;
import bp.os.process.BPProcessHandlers;
import bp.service.BPServiceWindowsUtil;
import bp.util.ClassUtil;
import bp.util.OSInfoHandlersWindows;
import bp.util.SystemUtil;
import bp.util.SystemUtil.SystemOS;

public class BPExtensionLoaderWindows implements BPExtensionLoader
{
	public String getName()
	{
		return "Windows";
	}

	public boolean isUI()
	{
		return false;
	}

	public String getUIType()
	{
		return null;
	}

	public String[] getParentExts()
	{
		return new String[] { "OS Management" };
	}

	public String[] getDependencies()
	{
		return null;
	}

	public boolean checkSystem()
	{
		return SystemUtil.getOS() == SystemOS.Windows;
	}

	public void preload()
	{
		BPNativeHelperFFM helperffm = BPNativeHelpers.getHelper(BPNativeHelperFFM.HELPER_FFM);
		if (ClassUtil.getTClass("com.sun.jna.Native", ClassUtil.getExtensionClassLoader()) != null)
		{
			SystemUtil.addSystemInfoHandler("OS_Windows", OSInfoHandlersWindows::getOSInfoWindows);
			SystemUtil.addSystemInfoHandler("OS_Windows_User", OSInfoHandlersWindows::getOSInfoWindowsUser);
			BPOSHandlers.S_SIMPLERUN = BPOSHandlers_Win::runSimple;
			BPOSHandlers.S_SIMPLESTART = BPOSHandlers_Win::startSimple;
			BPOSMonitors.registerFactory(BPOSMonitors.MONITOR_KEY_CPU, BPOSMonitor_CPU_Win::new);
			BPOSMonitors.registerFactory(BPOSMonitors.MONITOR_KEY_MEMORY, BPOSMonitor_Memory_Win::new);
			BPProcessHandlers.S_LISTPROCESSES = BPProcessHandler_Win::getProcessInfos;
			BPProcessHandlers.S_FINDPROCESS = BPProcessHandler_Win::getProcessInfo;
			BPProcessHandlers.S_FINDPROCESSES = BPProcessHandler_Win::getProcessInfos;
			BPNativeHelperJNA helperjna = BPNativeHelpers.getHelper(BPNativeHelperJNA.HELPER_JNA);
			if (helperjna != null)
			{
				if (helperffm == null)
				{
					helperjna.register(DwmapiHelper.HELPER_NAME_DWMAPI, new DwmapiHelperJNA());
					helperjna.register(Kernel32Helper.HELPER_NAME_K32, new Kernel32HelperJNA());
				}
			}
			(new BPServiceWindowsUtil()).register();
		}
		if (helperffm != null)
		{
			helperffm.register(DwmapiHelper.HELPER_NAME_DWMAPI, new DwmapiHelperFFM());
			helperffm.register(Kernel32Helper.HELPER_NAME_K32, new Kernel32HelperFFM());
		}
	}
}
