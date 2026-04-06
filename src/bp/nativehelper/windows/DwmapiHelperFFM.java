package bp.nativehelper.windows;

import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SymbolLookup;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;

import bp.util.Std;

public class DwmapiHelperFFM implements DwmapiHelper
{
	protected MethodHandle m_stehandle;

	public DwmapiHelperFFM()
	{
		Linker linker = Linker.nativeLinker();
		SymbolLookup lookup = SymbolLookup.libraryLookup("dwmapi", Arena.ofAuto());
		m_stehandle = linker.downcallHandle(lookup.find("DwmSetWindowAttribute").get(), FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_LONG, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT));
	}

	public int dwmSetWindowAttribute(long hwnd, int dwAttribute, byte[] pvAttribute, int cbAttribute)
	{
		Arena arena = Arena.ofConfined();
		MemorySegment mem = arena.allocate(cbAttribute);
		mem.copyFrom(MemorySegment.ofArray(pvAttribute));
		try
		{
			return (int) m_stehandle.invokeExact(hwnd, dwAttribute, mem, cbAttribute);
		}
		catch (Throwable e)
		{
			Std.err(e);
		}
		finally
		{
			arena.close();
		}
		return 0;
	}
}
