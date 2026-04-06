package bp.tool;

import bp.locale.BPLocaleHelpers;
import bp.nativehelper.BPNativeHelpers;
import bp.nativehelper.windows.Kernel32Helper;

public class BPToolPreventSleepWin implements BPTool
{
	public String getName()
	{
		return BPLocaleHelpers.translateByClass(BPTool.class, "Prevent Sleep") + "(Windows)";
	}

	public void run()
	{
		Kernel32Helper helper = BPNativeHelpers.getInterface(Kernel32Helper.HELPER_NAME_K32);
		helper.setThreadExecutionState(Kernel32Helper.ES_CONTINUOUS | Kernel32Helper.ES_SYSTEM_REQUIRED);
	}
}
