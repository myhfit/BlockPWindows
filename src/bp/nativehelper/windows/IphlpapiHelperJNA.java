package bp.nativehelper.windows;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Union;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;

public class IphlpapiHelperJNA implements IphlpapiHelper
{
	private final static Iphlpapi INST = (Iphlpapi) Native.loadLibrary("iphlpapi", Iphlpapi.class);

	public int getIpForwardTable2(short family, List<IPForwardRecord> table)
	{
		PointerByReference nl = new PointerByReference();
		int r = INST.GetIpForwardTable2((short) 0, nl);
		IPForwardTable_JNA nl2 = new IPForwardTable_JNA(nl.getValue());
		nl2.read();
		nl2.Table = new IPForwardRecord_JNA[nl2.NumEntries];
		nl2.read();
		for (IPForwardRecord_JNA t : nl2.Table)
			table.add(t.toLogic());
		INST.FreeMibTable(nl.getValue());
		return r;
	}

	private static interface Iphlpapi extends StdCallLibrary
	{
		int GetIpForwardTable2(short family, PointerByReference table);

		void FreeMibTable(Pointer ptr);
	}

	public static class IPForwardTable_JNA extends Structure
	{
		public IPForwardTable_JNA(Pointer pointer)
		{
			super(pointer);
		}

		public int NumEntries;
		public IPForwardRecord_JNA[] Table = new IPForwardRecord_JNA[1];

		protected List<String> getFieldOrder()
		{
			return Arrays.asList("NumEntries", "Table");
		}
	}

	public static class IPForwardRecord_JNA extends Structure
	{
		public Net_LUID_JNA InterfaceLuid;
		public int InterfaceIndex;
		public IP_Address_Prefix_JNA DestinationPrefix;
		public SockAddr_INet_JNA NextHop;

		public byte SitePrefixLength;
		public int ValidLifetime;
		public int PreferredLifetime;
		public int Metric;
		public int Protocol;

		public byte Loopback;
		public byte AutoconfigureAddress;
		public byte Publish;
		public byte Immortal;

		public int Age;
		public int Origin;// NlroManual,NlroWellKnown,NlroDHCP,NlroRouterAdvertisement,Nlro6to4,

		protected List<String> getFieldOrder()
		{
			return Arrays.asList("InterfaceLuid", "InterfaceIndex", "DestinationPrefix", "NextHop", "SitePrefixLength", "ValidLifetime", "PreferredLifetime", "Metric", "Protocol", "Loopback", "AutoconfigureAddress", "Publish", "Immortal", "Age",
					"Origin");
		}

		public IPForwardRecord toLogic()
		{
			IPForwardRecord r = new IPForwardRecord();
			r.Age = Age;
			r.AutoconfigureAddress = AutoconfigureAddress;
			r.DestinationPrefix = DestinationPrefix.toLogic();
			r.Immortal = Immortal;
			r.InterfaceIndex = InterfaceIndex;
			r.InterfaceLuid = InterfaceLuid.toLogic();
			r.Loopback = Loopback;
			r.Metric = Metric;
			r.NextHop = NextHop.toLogic();
			r.Origin = Origin;
			r.PreferredLifetime = PreferredLifetime;
			r.Protocol = Protocol;
			r.Publish = Publish;
			r.SitePrefixLength = SitePrefixLength;
			r.ValidLifetime = ValidLifetime;
			return r;
		}
	}

	public static class Net_LUID_JNA extends Structure
	{
		public long Value;

		protected List<String> getFieldOrder()
		{
			return Arrays.asList("Value");
		}

		public Net_LUID toLogic()
		{
			Net_LUID rc = new Net_LUID();
			rc.Value = Value;
			rc.Reserved = Value & 0xFFF;
			rc.IfType = Value & 0xFFF;
			rc.NetLuidIndex = Value & 0xFF;
			return rc;
		}
	}

	public static class IP_Address_Prefix_JNA extends Structure
	{
		public SockAddr_INet_JNA Prefix;
		public byte PrefixLength;

		protected List<String> getFieldOrder()
		{
			return Arrays.asList("Prefix", "PrefixLength");
		}

		public IP_Address_Prefix toLogic()
		{
			IP_Address_Prefix rc = new IP_Address_Prefix();
			rc.PrefixLength = PrefixLength;
			rc.Prefix = Prefix.toLogic();
			return rc;
		}
	}

	public static class SockAddr_INet_JNA extends Union
	{
		public SockAddr_In_JNA Ipv4;
		public SockAddr_In6_JNA Ipv6;
		public short si_family;

		protected List<String> getFieldOrder()
		{
			return Arrays.asList("Ipv4", "Ipv6", "si_family");
		}

		public SockAddr_INet toLogic()
		{
			SockAddr_INet rc = new SockAddr_INet();
			Ipv4 = (SockAddr_In_JNA) readField("Ipv4");
			Ipv6 = (SockAddr_In6_JNA) readField("Ipv6");
			rc.si_family = si_family;
			rc.Ipv4 = Ipv4.toLogic();
			rc.Ipv6 = Ipv6.toLogic();
			return rc;
		}
	}

	public static class SockAddr_In_JNA extends Structure
	{
		public short sin_family;
		public short sin_port;
		public int sin_addr;
		public byte[] sin_zero = new byte[8];

		protected List<String> getFieldOrder()
		{
			return Arrays.asList("sin_family", "sin_port", "sin_addr", "sin_zero");
		}

		public SockAddr_In toLogic()
		{
			SockAddr_In rc = new SockAddr_In();
			rc.sin_family = sin_family;
			rc.sin_port = sin_port;
			rc.sin_addr = sin_addr;
			rc.sin_zero = sin_zero;
			return rc;
		}
	}

	public static class SockAddr_In6_JNA extends Structure
	{
		public short sin6_family;
		public short sin6_port;
		public int sin6_flowinfo;
		public byte[] sin6_addr = new byte[16];
		public int sin6_scope_id;

		protected List<String> getFieldOrder()
		{
			return Arrays.asList("sin6_family", "sin6_port", "sin6_flowinfo", "sin6_addr", "sin6_scope_id");
		}

		public SockAddr_In6 toLogic()
		{
			SockAddr_In6 rc = new SockAddr_In6();
			rc.sin6_family = sin6_family;
			rc.sin6_port = sin6_port;
			rc.sin6_flowinfo = sin6_flowinfo;
			rc.sin6_addr = sin6_addr;
			rc.sin6_scope_id = sin6_scope_id;
			return rc;
		}
	}
}
