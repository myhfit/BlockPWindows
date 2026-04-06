package bp.tool;

import java.util.function.BiConsumer;

import bp.BPCore.BPPlatform;
import bp.locale.BPLocaleHelpers;
import bp.util.SystemUtil;
import bp.util.SystemUtil.SystemOS;

public class BPToolFactoryWindows implements BPToolFactory
{
	public String getName()
	{
		return "Windows";
	}

	public boolean canRunAt(BPPlatform platform)
	{
		return true;
	}

	public void install(BiConsumer<String, BPTool> installfunc, BPPlatform platform)
	{
		if (SystemUtil.getOS() == SystemOS.Windows)
		{
			String packname = BPLocaleHelpers.getValueReflect("bp.ui.actions.BPActionConstOSManagement", "TXT_OSMAN");
			installfunc.accept(packname, new BPToolPreventSleepWin());
		}
	}
}
