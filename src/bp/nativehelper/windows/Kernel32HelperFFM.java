package bp.nativehelper.windows;

import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.SymbolLookup;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;

import bp.util.Std;

public class Kernel32HelperFFM implements Kernel32Helper
{
	protected MethodHandle m_stehandle;

	public Kernel32HelperFFM()
	{
		Linker linker = Linker.nativeLinker();
		SymbolLookup lookup = SymbolLookup.libraryLookup("Kernel32", Arena.ofAuto());
		m_stehandle = linker.downcallHandle(lookup.find("SetThreadExecutionState").get(), FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT));
	}

	public int setThreadExecutionState(int esFlags)
	{
		try
		{
			return (Integer) m_stehandle.invoke(esFlags);
		}
		catch (Throwable e)
		{
			Std.err(e);
		}
		return -1;
	}
}
