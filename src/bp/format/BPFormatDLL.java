package bp.format;

public class BPFormatDLL implements BPFormat
{
	public final static String FORMAT_DLL = "DLL";

	public String getName()
	{
		return FORMAT_DLL;
	}

	public String[] getExts()
	{
		return new String[] { ".dll" };
	}

	public boolean checkFeature(BPFormatFeature feature)
	{
		switch (feature)
		{
			case BIN:
			case OBJTREE:
				return true;
			default:
				return false;
		}
	}
}