package bp.util;

import java.util.Map;
import java.util.TreeMap;

import com.sun.jna.Memory;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

import bp.util.windows.Advapi32;
import bp.util.windows.Advapi32.SID_AND_ATTRIBUTES;
import bp.util.windows.Advapi32.TOKEN_GROUPS;
import bp.util.windows.Advapi32.TOKEN_USER;
import bp.util.windows.Kernel32;
import bp.util.windows.LIB_INSTS_WIN;

public class OSInfoHandlersWindows extends OSInfoHandlers
{
	public final static Map<String, Object> getOSInfoWindows()
	{
		Map<String, Object> rc = new TreeMap<String, Object>();

		Kernel32.OSVERSIONINFOEX osvi = new Kernel32.OSVERSIONINFOEX();
		osvi.dwOSVersionInfoSize = osvi.size();
		boolean r = LIB_INSTS_WIN.getKernel32().GetVersionExW(osvi);
		if (r)
		{
			osvi.dwOSVersionInfoSize = osvi.size();
			rc.put("Major_Version", osvi.dwMajorVersion);
			rc.put("Minor_Version", osvi.dwMinorVersion);
			rc.put("Build_Number", osvi.dwBuildNumber);
			rc.put("Platform_Id", osvi.dwPlatformId);
			rc.put("Product_Type", osvi.wProductType);
		}
		return rc;
	}

	public final static String getOSInfoWindowsUser()
	{
		StringBuilder sb = new StringBuilder();
		Advapi32 aa = LIB_INSTS_WIN.getAdvapi32();
		Kernel32 k32 = LIB_INSTS_WIN.getKernel32();

		{
			Memory m = new Memory(1028);
			Memory m2 = new Memory(4);
			try
			{
				m2.setInt(0, 256);
				if (!aa.GetUserNameW(m, m2))
					throw new RuntimeException("err:" + k32.GetLastError());
				sb.append("Username:" + m.getWideString(0));
			}
			finally
			{
				m.clear();
				m2.clear();
			}
		}
		{
			Memory m = new Memory(8);
			int TOKEN_QUERY = 0x008;
			NativeLong hprocess = k32.GetCurrentProcess();
			NativeLong htoken = null;
			try
			{
				aa.OpenProcessToken(k32.GetCurrentProcess(), TOKEN_QUERY, m);
				htoken = m.getNativeLong(0);
				{
					IntByReference dwsize = new IntByReference(0);
					aa.GetTokenInformation(htoken, 1, Pointer.NULL, 0, dwsize);
					int msize = dwsize.getValue();
					Memory puser = new Memory(msize);
					if (aa.GetTokenInformation(htoken, 1, puser, msize, dwsize))
					{
						TOKEN_USER tuser = new TOKEN_USER(puser);

						{
							PointerByReference pbr = new PointerByReference();
							aa.ConvertSidToStringSidW(tuser.User.Sid, pbr);

							Pointer ptr = pbr.getValue();
							try
							{
								sb.append("\nSID:" + ptr.getWideString(0));
							}
							finally
							{
								k32.LocalFree(ptr);
							}
						}
					}
				}

				{
					IntByReference dwsize = new IntByReference(0);
					aa.GetTokenInformation(htoken, 2, Pointer.NULL, 0, dwsize);
					int msize = dwsize.getValue();
					Memory pgroups = new Memory(msize);
					if (aa.GetTokenInformation(htoken, 2, pgroups, msize, dwsize))
					{
						TOKEN_GROUPS tgroups = new TOKEN_GROUPS(pgroups);
						SID_AND_ATTRIBUTES[] gps = (SID_AND_ATTRIBUTES[]) tgroups.Group0.toArray(tgroups.GroupCount);
						for (int i = 0; i < gps.length; i++)
						{
							PointerByReference pbr = new PointerByReference();
							aa.ConvertSidToStringSidW(gps[i].Sid, pbr);
							Pointer ptr = pbr.getValue();
							try
							{
								String sid = ptr.getWideString(0);
								{
									IntByReference cchName = new IntByReference();
									IntByReference cchDomainName = new IntByReference();
									PointerByReference peUse = new PointerByReference();
									if (!aa.LookupAccountSidW(null, gps[i].Sid, null, cchName, null, cchDomainName, peUse))
									{
										char[] name = new char[cchName.getValue()];
										char[] name2 = new char[cchDomainName.getValue()];
										aa.LookupAccountSidW(null, gps[i].Sid, name, cchName, name2, cchDomainName, peUse);
										try
										{
											sb.append("\nGroup" + i + ":" + new String(name, 0, cchName.getValue()) + "(" + sid + ")");
										}
										finally
										{
										}
									}
								}
							}
							finally
							{
								k32.LocalFree(ptr);
							}

						}
					}
				}
			}
			finally
			{
				if (htoken != null)
				{
					k32.CloseHandle(htoken);
				}
				k32.CloseHandle(hprocess);
			}
		}
		return sb.toString();
	}
}
