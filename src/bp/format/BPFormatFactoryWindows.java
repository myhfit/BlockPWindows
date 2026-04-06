package bp.format;

import java.util.function.Consumer;

public class BPFormatFactoryWindows implements BPFormatFactory
{
	public void register(Consumer<BPFormat> regfunc)
	{
		regfunc.accept(new BPFormatEXE());
		regfunc.accept(new BPFormatDLL());
	}
}