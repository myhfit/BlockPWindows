package bp.format;

public class BPFormatEXE implements BPFormat
{
	public final static String FORMAT_EXE = "EXE";

	public String getName()
	{
		return FORMAT_EXE;
	}

	public String[] getExts()
	{
		return new String[] { ".exe" };
	}

	public boolean checkFeature(BPFormatFeature feature)
	{
		switch (feature)
		{
			case BIN:
			case EXECUTABLE:
			case OBJTREE:
				return true;
			default:
				return false;
		}
	}
}