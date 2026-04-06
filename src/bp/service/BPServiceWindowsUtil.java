package bp.service;

import java.awt.Component;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import com.sun.jna.Native;

import bp.nativehelper.BPNativeHelpers;
import bp.nativehelper.windows.DwmapiHelper;

public class BPServiceWindowsUtil extends BPServiceFreeCall
{
	protected final AtomicBoolean m_isreg = new AtomicBoolean(false);

	public BPServiceWindowsUtil()
	{
		m_callablemethods = new CopyOnWriteArrayList<String>(new String[] { "getComponentHWND", "setImmersiveDarkMode" });
	}

	public String getName()
	{
		return "WindowsUtil";
	}

	public void stop()
	{
	}

	public void start()
	{
	}

	public void register()
	{
		if (m_isreg.compareAndSet(false, true))
		{
			BPServiceManager.add(this);
		}
	}

	public long getComponentHWND(Component c)
	{
		return Native.getComponentID(c);
	}

	public void setImmersiveDarkMode(long hwnd)
	{
		DwmapiHelper helper = BPNativeHelpers.getInterface(DwmapiHelper.HELPER_NAME_DWMAPI);
		helper.dwmSetWindowAttribute(hwnd, 20, new byte[] { (byte) 1 }, 1);
	}
}
