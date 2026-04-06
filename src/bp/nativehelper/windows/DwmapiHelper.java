package bp.nativehelper.windows;

public interface DwmapiHelper
{
	public final static String HELPER_NAME_DWMAPI = "dwmapi";
	
	int dwmSetWindowAttribute(long hwnd, int dwAttribute, byte[] pvAttribute, int cbAttribute);
}
