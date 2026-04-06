package bp.util.windows;

import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.win32.StdCallLibrary;

public interface Dwmapi extends StdCallLibrary
{
	int DwmSetWindowAttribute(NativeLong hwnd, int dwAttribute, Pointer pvAttribute, int cbAttribute);
}
