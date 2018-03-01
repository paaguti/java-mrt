// This file is part of java-mrt
// A library to parse MRT files

// This file is released under LGPL 3.0
// http://www.gnu.org/licenses/lgpl-3.0-standalone.html

package org.javamrt.mrt;


import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Arrays;

public class MRTConstants {

	/**
	 *  for the ASPATH
	 */

	public static final int asSet = 1;
	public static final int asSequence = 2;
	public static final int asConfedSet = 3;
	public static final int asConfedSequence = 4;

	public static String asPathString(int type) {
	    switch (type) {
	    case asSet: 		return "asSet";
	    case asSequence:		return "asSequence";
	    case asConfedSet:		return "asConfedSet";
	    case asConfedSequence:	return "asConfedSequence";
	    default:			return "unknown ASPATH Type";
	    }
	}
	/*
	 * TODO :
	 */
	public static final byte BGP_ATTR_FLAG_OPTIONAL = (byte)0x80;
	public static final byte BGP_ATTR_FLAG_TRANS    = (byte)0x40;
	public static final byte BGP_ATTR_FLAG_PARTIAL  = (byte)0x20;
	public static final byte BGP_ATTR_FLAG_EXTLEN   = (byte) 0x10;

	public static final byte ORIGIN = 1;
	public static final byte AS_PATH = 2;
	public static final byte NEXT_HOP = 3;
	public static final byte MULTI_EXIT = 4;
	public static final byte LOCAL_PREF = 5;
	public static final byte ATOMIC_AGGREGATE = 6;
	public static final byte AGGREGATOR = 7;
	public static final byte COMMUNITY = 8;
	public static final byte ORIGINATOR_ID = 9;
	public static final byte CLUSTER_LIST = 10;
	public static final byte DPA = 11;
	public static final byte ADVERTISER = 12;
	public static final byte CLUSTER_ID = 13;
	public static final byte MP_REACH = 14;
	public static final byte MP_UNREACH = 15;
	public static final byte EXT_COMMUNITIES = 16;
	public static final byte AS4_PATH = 17;
	public static final byte AS4_AGGREGATOR = 18;
	public static final byte LARGE_COMMUNITY = 32;
	public static final int  ATTR_SET = 128;

	public static final int ATTRIBUTE_AS_PATH = 0;
	public static final int ATTRIBUTE_ORIGIN = 1;
	public static final int ATTRIBUTE_NEXT_HOP = 2;
	public static final int ATTRIBUTE_LOCAL_PREF = 3;
	public static final int ATTRIBUTE_MULTI_EXIT = 4;
	public static final int ATTRIBUTE_COMMUNITY = 5;
	public static final int ATTRIBUTE_ATOMIC_AGGREGATE = 6;
	public static final int ATTRIBUTE_AGGREGATOR = 7;
	public static final int ATTRIBUTE_ORIGINATOR_ID = 8;
	public static final int ATTRIBUTE_CLUSTER_LIST = 9;
	public static final int ATTRIBUTE_DPA = 10;
	public static final int ATTRIBUTE_ADVERTISER = 11;
	public static final int ATTRIBUTE_CLUSTER_ID = 12;
	public static final int ATTRIBUTE_MP_REACH = 13;
	public static final int ATTRIBUTE_MP_UNREACH = 14;
	public static final int ATTRIBUTE_EXT_COMMUNITIES = 15;
	public static final int ATTRIBUTE_AS4_PATH = 16;
	public static final int ATTRIBUTE_AS4_AGGREGATOR = 17;
	public static final int ATTRIBUTE_CONNECTOR = 20;
	public static final int ATTRIBUTE_ASPATHLIMIT = 21;
	public static final int ATTRIBUTE_LARGE_COMMUNITY = 22;
	public static final int ATTRIBUTE_TOTAL = 256;

	public static final int AFI_IPv4 = 1;
	public static final int AFI_IPv6 = 2;
	public static final int AFI_MAX = AFI_IPv6;

	public static final int IPv4_LENGTH = 4;
	public static final int IPv6_LENGTH = 16;

	public static final int SAFI_UNICAST = 1;
	public static final int SAFI_MULTICAST = 2;
	public static final int SAFI_UNICAST_MULTICAST = 3;
	public static final int SAFI_MAX = SAFI_UNICAST_MULTICAST;

	public static final int TABLE_DUMP = 12;

	public static final int TABLE_DUMP_v2 = 13;
	public static final int PEER_INDEX_TABLE = 1;
	public static final int RIB_IPV4_UNICAST = 2;
	public static final int RIB_IPV4_MULTICAST = 3;
	public static final int RIB_IPV6_UNICAST = 4;
	public static final int RIB_IPV6_MULTICAST = 5;
	public static final int RIB_GENERIC = 6;

	public static final int BGP4MP = 16;
	public static final int BGP4MP_STATE_CHANGE = 0;
	public static final int BGP4MP_MESSAGE = 1;
	public static final int BGP4MP_ENTRY = 2;
	public static final int BGP4MP_SNAPSHOT = 3;
	public static final int BGP4MP_MESSAGE_AS4 = 4;
	public static final int BGP4MP_STATE_CHANGE_AS4 = 5;

	public static final int BGP4MSG_OPEN = 1;
	public static final int BGP4MSG_UPDATE = 2;
	public static final int BGP4MSG_NOTIFICATION = 3;
	public static final int BGP4MSG_KEEPALIVE = 4;
	public static final int BGP4MSG_REFRESH = 5;

	public static final int BGPDUMP_TYPE_MRTD_BGP = 5;
	public static final int BGPDUMP_SUBTYPE_MRTD_BGP_UPDATE= 1;
	public static final int BGPDUMP_SUBTYPE_MRTD_BGP_STATE_CHANGE = 3;
	public static final int BGPDUMP_SUBTYPE_MRTD_BGP_KEEPALIVE = 7;

	public static final String UPDATE_STR_BGP4MP = "BGP4MP";
	public static final String UPDATE_STR_BGP = "BGP";

	public static final String mpSubType(int s) {
		switch (s) {
		case BGP4MP_STATE_CHANGE:
			return "BGP4MP_STATE_CHANGE";
		case BGP4MP_MESSAGE:
			return "BGP4MP_MESSAGE";
		case BGP4MP_ENTRY:
			return "BGP4MP_ENTRY";
		case BGP4MP_SNAPSHOT:
			return "BGP4MP_SNAPSHOT";
		case BGP4MP_MESSAGE_AS4:
			return "BGP4MP_MESSAGE_AS4";
		case BGP4MP_STATE_CHANGE_AS4:
			return "BGP4MP_STATE_CHANGE_AS4";
		default:
			return "BGP4MP_UNKNOWN";
		}
	}

	public static final String attrFlags(byte attr) {
		String result = "";
		if (0 != (attr & BGP_ATTR_FLAG_OPTIONAL)) result = result + "OPTIONAL ";
		if (0 != (attr & BGP_ATTR_FLAG_TRANS))    result = result + "TRANSITIVE ";
		if (0 != (attr & BGP_ATTR_FLAG_PARTIAL))  result = result + "PARTIAL ";
		if (0 != (attr & BGP_ATTR_FLAG_EXTLEN ))  result = result + "EXT_LEN ";
		return result.replaceAll(" $", "");
	}


	private static final String[] bgpTypes = {
		"BGP4MSG_UNKNOWN",
		"BGP4MSG_OPEN",
		"BGP4MSG_UPDATE",
		"BGP4MSG_NOTIFICATION",
		"BGP4MSG_KEEPALIVE",
		"BGP4MSG_REFRESH"
	};

	public static final String bgpType(int bgpType) {
		try {
			return bgpTypes[bgpType];
		} catch (Exception e) {
		}
		return bgpTypes[0];
	}

	protected static final SimpleDateFormat mrtFormat =
		new SimpleDateFormat("MM/dd/yy HH:mm:ss");


	/**
	 *
	 * @param InetAddress ia
	 * @param int afi
	 * @return boolean
	 *
	 * It looks if it is an ipv4 embedded in ipv6
	 */
	public static final boolean isInIpv4EmbeddedIpv6Format(InetAddress ia, int afi){
		if(ia instanceof Inet4Address && afi == AFI_IPv6){
			return true;
		}
		if (ia instanceof Inet6Address && ((Inet6Address)ia).isIPv4CompatibleAddress()) {
			return true;
		}
		return false;
	}

	/**
	 *
	 * @param byte[] base
	 * @return boolean
	 *
	 * It looks if it is an ipv4 embedded in ipv6
	 */
	public static final boolean isInIpv4EmbeddedIpv6Format(byte[] base) {
		try {
			InetAddress ia = InetAddress.getByAddress(base);
			if (ia instanceof Inet4Address && base.length == IPv6_LENGTH) {
				return true;
			}
			if (ia instanceof Inet6Address && ((Inet6Address)ia).isIPv4CompatibleAddress()) {
				return true;
			}
		}catch(Exception e) {
		    e.printStackTrace();
		}
		return false;
	}

	/**
	 *
	 * @param InetAddress inetAddress
	 * @param boolean isIpv4EmbeddedIpv6
	 * @return Textual representation of the InetAddress.
	 * It also parses if it is a ipv4 embedded in ip6
	 *
	 * for all addresses, removes initial name
	 */
	public static final String ipAddressString(InetAddress inetAddress, boolean isIpv4EmbeddedIpv6) {
//		String ipAddressString = inetAddress.getHostAddress().
//				// replaceFirst("^[^/]*/", "").
//						replaceFirst(":0(:0)+", "::").
//						replaceFirst("^0:", "").
//						replaceFirst(":::", "::");

		String ipAddressString = inetAddress.getHostAddress();
		try {

			if (isIpv4EmbeddedIpv6) {

				if(inetAddress instanceof Inet4Address)
					ipAddressString = "::ffff:" + ipAddressString;
				else if (inetAddress instanceof Inet6Address){
					byte[] ipv4Embedded = Arrays.copyOfRange(inetAddress.getAddress(), 12, IPv6_LENGTH);
                    String ipv4Str = (Inet4Address.getByAddress(ipv4Embedded)).getHostAddress();
                    if(ipv4Str.equals("0.0.0.0")) ipv4Str ="";
					ipAddressString = "::".concat(ipv4Str);
				}
			}

			if (ipAddressString.equals(":"))
				ipAddressString = "::";
		}catch(Exception e){
            e.printStackTrace();
		}

		return ipAddressString;
	}

	public static final String ipAddressString(byte[] ipAddressBase, boolean isIpv4EmbeddedIpv6){
		try{
			return ipAddressString(InetAddress.getByAddress(ipAddressBase), isIpv4EmbeddedIpv6);
		}catch(Exception e){
			e.printStackTrace();
			return new String("??/");
		}
	}
}
