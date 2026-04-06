package bp.util.windows;

import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.win32.StdCallLibrary;

public interface User32 extends StdCallLibrary
{
	NativeLong GetWindow(NativeLong hwnd, int uCmd);

	NativeLong GetDesktopWindow();

	int GetWindowTextW(NativeLong hwnd, char[] lpString, int nMaxCount);

	int GetClassNameW(NativeLong hWnd, char[] lpClassName, int nMaxCount);

	int GetWindowThreadProcessId(NativeLong hWnd, Pointer lpdwProcessId);

	boolean PostMessageW(NativeLong hWnd, int msg, NativeLong wParam, NativeLong lParam);
}
