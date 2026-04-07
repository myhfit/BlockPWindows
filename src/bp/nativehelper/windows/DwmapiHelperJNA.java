package bp.nativehelper.windows;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.win32.StdCallLibrary;

public class DwmapiHelperJNA implements DwmapiHelper
{
	private final static Dwmapi INST = (Dwmapi) Native.loadLibrary("dwmapi", Dwmapi.class);

	public int dwmSetWindowAttribute(long hwnd, int dwAttribute, byte[] pvAttribute, int cbAttribute)
	{
		int l = pvAttribute.length;
		Memory m = new Memory(l);
		try
		{
			m.write(0, pvAttribute, 0, l);
			return INST.DwmSetWindowAttribute(new NativeLong(hwnd), dwAttribute, m, l);
		}
		finally
		{
			m.clear();
		}
	}

	private static interface Dwmapi extends StdCallLibrary
	{
		int DwmSetWindowAttribute(NativeLong hwnd, int dwAttribute, Pointer pvAttribute, int cbAttribute);
	}
}
