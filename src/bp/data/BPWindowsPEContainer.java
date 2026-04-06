package bp.data;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import bp.config.BPConfig;
import bp.data.BPTreeData.BPTreeDataObj;
import bp.data.reader.BPBytesReader;
import bp.data.reader.BPBytesReaderBB;
import bp.data.reader.BPBytesReaderDCRA;
import bp.format.BPFormatDLL;
import bp.format.BPFormatEXE;
import bp.res.BPResourceHolder;
import bp.util.ClassUtil;
import bp.util.Std;

public class BPWindowsPEContainer extends BPDataContainerBase implements BPTreeDataContainer
{
	public BPTreeData readTreeData()
	{
		try
		{
			BPBytesReader reader = null;
			if (m_res instanceof BPResourceHolder && ((BPResourceHolder) m_res).isHold(BPDataContainerRandomAccess.class))
			{
				BPDataContainerRandomAccess dcra = ((BPResourceHolder) m_res).getData();
				reader = new BPBytesReaderDCRA(dcra);
			}
			else
				reader = new BPBytesReaderBB(readAll());
			BPWindowsPE pe = new BPWindowsPE();
			pe.read(reader);
			Map<String, Object> t = ClassUtil.getMappedDataReflect(pe);
			BPTreeData rc = new BPTreeDataObj();
			rc.setRoot(t);
			return rc;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Std.err(e);
		}
		return null;
	}

	public CompletionStage<BPTreeData> readTreeDataAsync()
	{
		return CompletableFuture.supplyAsync(this::readTreeData);
	}

	public Boolean writeTreeData(BPTreeData data)
	{
		return false;
	}

	public CompletionStage<Boolean> writeTreeDataAsync(BPTreeData data)
	{
		return null;
	}

	public static class BPWindowsPEEXEContainerFactory implements BPDataContainerFactory
	{
		public boolean canHandle(String format)
		{
			return BPFormatEXE.FORMAT_EXE.equals(format);
		}

		public String getName()
		{
			return "WinPE_EXE";
		}

		@SuppressWarnings("unchecked")
		public <T extends BPDataContainer> T createContainer(BPConfig config)
		{
			BPWindowsPEContainer h = new BPWindowsPEContainer();
			return (T) h;
		}

		public String getFormat()
		{
			return BPFormatEXE.FORMAT_EXE;
		}
	}

	public static class BPWindowsPEDLLContainerFactory implements BPDataContainerFactory
	{
		public boolean canHandle(String format)
		{
			return BPFormatDLL.FORMAT_DLL.equals(format);
		}

		public String getName()
		{
			return "WinPE_DLL";
		}

		@SuppressWarnings("unchecked")
		public <T extends BPDataContainer> T createContainer(BPConfig config)
		{
			BPWindowsPEContainer h = new BPWindowsPEContainer();
			return (T) h;
		}

		public String getFormat()
		{
			return BPFormatDLL.FORMAT_DLL;
		}
	}
}
