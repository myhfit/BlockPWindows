package bp.nativehelper.windows;

import java.util.List;

public interface IphlpapiHelper
{
	public final static String HELPER_NAME_IPHLPAPI = "iphlpapi";

	public final static short AF_UNSPEC = 0;
	public final static short AF_INET = 2;
	public final static short AF_INET6 = 23;

	int getIpForwardTable2(short family, List<IPForwardRecord> table);

	public static class IPForwardRecord
	{
		public Net_LUID InterfaceLuid;
		public int InterfaceIndex;
		public IP_Address_Prefix DestinationPrefix;
		public SockAddr_INet NextHop;

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
	}

	public static class Net_LUID
	{
		public long Value;
		public long Reserved;
		public long NetLuidIndex;
		public long IfType;
	}

	public static class IP_Address_Prefix
	{
		public SockAddr_INet Prefix;
		public byte PrefixLength;
	}

	public static class SockAddr_INet
	{
		public SockAddr_In Ipv4;
		public SockAddr_In6 Ipv6;
		public short si_family;
	}

	public static class SockAddr_In
	{
		public short sin_family;
		public short sin_port;
		public int sin_addr;
		public byte[] sin_zero; // 8byte
	}

	public static class SockAddr_In6
	{
		public short sin6_family;
		public short sin6_port;
		public int sin6_flowinfo;
		public byte[] sin6_addr; // 16byte
		public int sin6_scope_id; // union ULONG sin6_scope_id/SCOPE_ID
									// sin6_scope_struct;
	}
}
