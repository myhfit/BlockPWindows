package bp.nativehelper.windows;

public interface Kernel32Helper
{
	public final static String HELPER_NAME_K32 = "Kernel32";

	public final static int ES_AWAYMODE_REQUIRED = 0x00000040;
	public final static int ES_CONTINUOUS = 0x80000000;
	public final static int ES_DISPLAY_REQUIRED = 0x00000002;
	public final static int ES_SYSTEM_REQUIRED = 0x00000001;

	int setThreadExecutionState(int esFlags);
}
