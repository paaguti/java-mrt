package org.javamrt.mrt;

import java.net.InetAddress;
import java.text.SimpleDateFormat;

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
	public static final int ATTRIBUTE_ASPATHLIMIT = 21;
	public static final int ATTRIBUTE_TOTAL = 22;

	public static final int AFI_IPv4 = 1;
	public static final int AFI_IPv6 = 2;
	public static final int AFI_MAX = AFI_IPv6;

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
	 * @return Textual representation of the InetAddress
	 * 
	 * for all addresses, removes initial name
	 */
	protected static final String ipAddressString(InetAddress ia) 
	{
		return ia.getHostAddress().
			// replaceFirst("^[^/]*/", "").
			replaceFirst(":0(:0)+","::").
			replaceFirst("^0:","").
			replaceFirst(":::", "::");	
	}
}
