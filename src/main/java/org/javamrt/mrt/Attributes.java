// This file is part of java-mrt
// A library to parse MRT files

// This file is released under LGPL 3.0
// http://www.gnu.org/licenses/lgpl-3.0-standalone.html

package org.javamrt.mrt;

import org.javamrt.progs.route_btoa;
import org.javamrt.utils.Debug;
import org.javamrt.utils.RecordAccess;

import java.net.InetAddress;
import java.util.Vector;

public class Attributes {

	private final byte[] bytes;

	public Attributes(byte[] record, int attrLen, int attrPos, int attrBytes)
			throws Exception {
		if (attrBytes != 2 && attrBytes != 4)
			throw new AttributeException(String.format(
					"Attributes needs attrBytes 2 or 4 (not %d", attrBytes));
		bytes = new byte[attrLen];
		System.arraycopy(record, attrPos, bytes, 0, attrLen);
		decode(bytes, attrLen, attrBytes);
	}

	public Attributes(byte[] record, int attrLen, int attrPos) throws Exception {
		bytes = new byte[attrLen];
		System.arraycopy(record, attrPos, bytes, 0, attrLen);
		decode(bytes, attrLen, 2);
	}

	public byte[] getBytes() { return bytes; }

	private void decode(byte[] record, int attrLen, int attrBytes)
			throws Exception {
		byte[] buffer;

		int here = 0;

		if (Debug.compileDebug)
			Debug.printf("Attributes(...,%d,%d,%d)\n", attrLen, 0,
					attrBytes);

		attributes = new Vector<Attribute>(MRTConstants.ATTRIBUTE_TOTAL);

		for (int i = 0; i < MRTConstants.ATTRIBUTE_TOTAL; i++)
			if (i == MRTConstants.ATTRIBUTE_NEXT_HOP)
				attributes.addElement(new NextHop());
			else
				attributes.addElement(null);

		while (here < attrLen) {

			int flag = RecordAccess.getU8(record, here);
			int type = RecordAccess.getU8(record, here + 1);
			int len;
			int dato;

			if ((flag & MRTConstants.BGP_ATTR_FLAG_EXTLEN) == 0) {
				len = RecordAccess.getU8(record, here + 2);
				dato = here + 3;
			} else {
				len = RecordAccess.getU16(record, here + 2);
				dato = here + 4;
			}
			buffer = RecordAccess.getBytes(record, dato, len);
			here = dato + len;

			if (Debug.compileDebug)
				Debug.printf("Flag = 0x%02x(%s) type = %02d Len=%d\n", flag,
						MRTConstants.attrFlags((byte) flag), type, len);

			switch (type) {
			case MRTConstants.AS_PATH:
				Attribute asPath = new ASPath(buffer, attrBytes);
				attributes.set(MRTConstants.ATTRIBUTE_AS_PATH, asPath);
				if (Debug.compileDebug)
					Debug.println("ATTRIBUTE_AS_PATH = " + asPath);
				break;

			case MRTConstants.ORIGIN:
				Attribute attrOrigin = new AttrOrigin(buffer);
				attributes.set(MRTConstants.ATTRIBUTE_ORIGIN, attrOrigin);
				if (Debug.compileDebug)
					Debug.println("ATTRIBUTE_ORIGIN, ");
				break;

			case MRTConstants.NEXT_HOP:
				Attribute nextHop = new NextHop(buffer);
				attributes.set(MRTConstants.ATTRIBUTE_NEXT_HOP, nextHop);
				if (Debug.compileDebug)
					Debug.println("ATTRIBUTE_NEXT_HOP " + nextHop);
				break;

			case MRTConstants.LOCAL_PREF:
				Attribute localPref = new LocalPref(buffer);
				attributes.set(MRTConstants.ATTRIBUTE_LOCAL_PREF, localPref);
				if (Debug.compileDebug)
					Debug.println("ATTRIBUTE_LOCAL_PREF, ");
				break;

			case MRTConstants.MULTI_EXIT:
				Attribute med = new Med(buffer);
				attributes.set(MRTConstants.ATTRIBUTE_MULTI_EXIT, med);
				if (Debug.compileDebug)
					Debug.println("ATTRIBUTE_MULTI_EXIT, ");
				break;

			case MRTConstants.COMMUNITY:
				Attribute community = new Community(buffer);
				attributes.set(MRTConstants.ATTRIBUTE_COMMUNITY, community);
				if (Debug.compileDebug)
					Debug.println("ATTRIBUTE_COMMUNITY, ");
				break;

			case MRTConstants.ATOMIC_AGGREGATE:
				Attribute atomAggr = new AtomAggr();
				attributes.set(MRTConstants.ATTRIBUTE_ATOMIC_AGGREGATE,
						atomAggr);
				if (Debug.compileDebug)
					Debug.println("ATTRIBUTE_ATOMIC_AGGREGATE, ");
				break;

			case MRTConstants.AGGREGATOR:
				Attribute aggregator = new Aggregator(buffer, attrBytes);
				attributes.set(MRTConstants.ATTRIBUTE_AGGREGATOR, aggregator);
				if (Debug.compileDebug)
					Debug.println("ATTRIBUTE_AGGREGATOR, ");
				break;

			case MRTConstants.ORIGINATOR_ID:
				Attribute originatorId = new OriginatorID(buffer);
				attributes.set(MRTConstants.ATTRIBUTE_ORIGINATOR_ID,
						originatorId);
				if (Debug.compileDebug)
					Debug.println("ATTRIBUTE_ORIGINATOR_ID, ");
				break;

			case MRTConstants.CLUSTER_LIST:
				Attribute clusterList = new ClusterList(buffer);
				attributes
						.set(MRTConstants.ATTRIBUTE_CLUSTER_LIST, clusterList);
				if (Debug.compileDebug)
					Debug.println("ATTRIBUTE_CLUSTER_LIST, ");
				break;

			case MRTConstants.DPA:
				Attribute dpa = new Dpa(buffer);
				attributes.set(MRTConstants.ATTRIBUTE_DPA, dpa);
				if (Debug.compileDebug)
					Debug.println("ATTRIBUTE_DPA, ");
				break;

			case MRTConstants.ADVERTISER:
				Attribute advertiser = new Advertiser(buffer);
				attributes.set(MRTConstants.ATTRIBUTE_ADVERTISER, advertiser);
				if (Debug.compileDebug)
					Debug.println("ATTRIBUTE_ADVERTISER, ");
				break;

			case MRTConstants.CLUSTER_ID:
				Attribute clusterId = new ClusterId(buffer);
				attributes.set(MRTConstants.ATTRIBUTE_CLUSTER_ID, clusterId);
				if (Debug.compileDebug)
					Debug.printf("ATTRIBUTE_CLUSTER_ID = %s\n", clusterId
							.toString());
				break;

			case MRTConstants.MP_REACH:
				MpReach mpReach = new MpReach(buffer);
				attributes.set(MRTConstants.ATTRIBUTE_MP_REACH, mpReach);
				InetAddress nhia = mpReach.getNextHop();
				try {
					NextHop nh = new NextHop(nhia);
					attributes.set(MRTConstants.ATTRIBUTE_NEXT_HOP, nh);
					if (Debug.compileDebug)
						Debug.printf("ATTRIBUTE_MP_REACH :%s\n NEXT HOP:%s",
								mpReach.toString(), nh.toString());
				} catch (NullPointerException npe) {
					// ignore silently
				}
				break;

			case MRTConstants.MP_UNREACH:
				Attribute mpUnreach = new MpUnReach(buffer);
				attributes.set(MRTConstants.ATTRIBUTE_MP_UNREACH, mpUnreach);
				if (Debug.compileDebug)
					Debug.println("ATTRIBUTE_MP_UNREACH " + mpUnreach);
				break;

			case MRTConstants.EXT_COMMUNITIES:
				Attribute extCommunities = new ExtCommunities(buffer);
				attributes.set(MRTConstants.ATTRIBUTE_EXT_COMMUNITIES,
						extCommunities);
				if (Debug.compileDebug)
					Debug.println("ATTRIBUTE_EXT_COMMUNITIES, ");
				break;

			/*
			 * TODO: Handle 4 byte AS stuff correctly
			 *
			 * Observed in rrc01: around April 2007 sometimes AS4PATH is set to
			 * 0
			 */
			case MRTConstants.AS4_PATH:
				if (buffer.length == 0) {
					hasAS4PathBug = true;
				} else {
					/**
					 * throws RFC4893Exception, AttributeException
					 */
				    //					As4Path.replaceAS23456(buffer, (ASPath) getAttribute(MRTConstants.ATTRIBUTE_AS_PATH));
					RFC4893.replaceAS23456(buffer, (ASPath) getAttribute(MRTConstants.ATTRIBUTE_AS_PATH));
				}
				break;

			case MRTConstants.AS4_AGGREGATOR:
				/*
				 * Override 2byte AS Aggregator
				 *
				 * TODO: sanity check: make sure 2 byte aggregator was 23456
				 */
				Attribute as4Aggregator = new Aggregator(buffer, 4);
				// attributes.set (ATTRIBUTE_AS4_AGGREGATOR, as4Aggregator);
				attributes
						.set(MRTConstants.ATTRIBUTE_AGGREGATOR, as4Aggregator);
				if (Debug.compileDebug)
					Debug.println("ATTRIBUTE_AS4_AGGREGATOR ");
				break;

			case MRTConstants.ATTRIBUTE_CONNECTOR:
				// deprecated attribute. ignoring
				if (Debug.compileDebug)
					Debug.println("ATTRIBUTE_CONNECTOR (deprecated)");
				break;

			// Expired but present in RRC!

			case MRTConstants.ATTRIBUTE_ASPATHLIMIT:
				Attribute asPathLimit = new ASPathLimit(buffer);
				attributes.set(MRTConstants.ATTRIBUTE_ASPATHLIMIT, asPathLimit);
				if (Debug.compileDebug)
					Debug.printf("ATTRIBUTE_ASPATHLIMIT %s\n", asPathLimit
							.toString());
				this.hasASPATHLimit = true;
				break;

				case MRTConstants.LARGE_COMMUNITY:
					if (buffer.length > 0) {
						Attribute largeCommunity = new LargeCommunity(buffer);
						attributes.set(MRTConstants.ATTRIBUTE_LARGE_COMMUNITY, largeCommunity);
					}
					break;

				default:
					// make sure to not overwrite other attributes in the Vector,
					// as index in the Vector is not the same as type value
					if (type > MRTConstants.ATTRIBUTE_LARGE_COMMUNITY && type < MRTConstants.ATTRIBUTE_TOTAL) {
						final UnsupportedAttribute attribute = new UnsupportedAttribute(type, buffer);
						attributes.set(type, attribute);
					} else {
						route_btoa.System_err_println("Ignoring unknown attribute type " + type);
					}
					break;
			}
		}
	}

	/*
	 * private Vector <Attribute> getAttributes () { return attributes; }
	 */
	private Vector<Attribute> attributes;

	public Attribute getAttribute(int index) throws Exception {
		return attributes.elementAt(index);
	}

	public String toString() {
		if (toStr != null)
			return toStr;

		toStr = "";

		for (int i = MRTConstants.ATTRIBUTE_AS_PATH; i <= MRTConstants.ATTRIBUTE_AGGREGATOR; i++) {
			if (attributes.elementAt(i) != null) {
				if (i == MRTConstants.ATTRIBUTE_NEXT_HOP) {
					if (attributes.elementAt(MRTConstants.ATTRIBUTE_MP_REACH) != null) {
						toStr = toStr.concat(attributes.elementAt(MRTConstants.ATTRIBUTE_MP_REACH).toString()).concat("|");
						continue;
					}
				}
				toStr = toStr.concat(attributes.elementAt(i).toString());
			} else {
				if (i == MRTConstants.ATTRIBUTE_LOCAL_PREF
						|| i == MRTConstants.ATTRIBUTE_MULTI_EXIT)
					toStr = toStr.concat("0");
				else if (i == MRTConstants.ATTRIBUTE_ATOMIC_AGGREGATE)
					toStr = toStr.concat("NAG");
			}
			toStr = toStr.concat("|");
		}
		return toStr;
	}

	public ASPath getASPath() {
		return (ASPath) attributes.elementAt(MRTConstants.ATTRIBUTE_AS_PATH);
	}

	public Community getCommunity() {
		Community result = (Community) attributes
				.elementAt(MRTConstants.ATTRIBUTE_COMMUNITY);
		if (result != null)
			return result;
		return Community.empty();
	}

	public LargeCommunity getLargeCommunity() {
		return (LargeCommunity)
                    attributes.elementAt(MRTConstants.ATTRIBUTE_LARGE_COMMUNITY);
	}

	public Med getMed() {
		Med result = (Med) attributes
				.elementAt(MRTConstants.ATTRIBUTE_MULTI_EXIT);
		if (result == null)
			return new Med(0);
		return result;
	}

	public boolean hasAS4PathBug = false;

	public boolean hasASPATHLimit = false;

	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (o == this)
			return true;
		if (o instanceof Attributes) {
			//
			// duplicated if _all_ attributes are the same
			//
			for (int i = 0; i < MRTConstants.ATTRIBUTE_TOTAL; i++) {
				Attribute a1 = this.attributes.elementAt(i), a2 = ((Attributes) o).attributes
						.elementAt(i);
				if (a1 == null) {
					if (a2 != null)
						return false;
					continue;
				}
				if (!a1.equals(a2))
					return false;
			}
			return true;
		} // else
		return false;
	}

	private String toStr = null;
}
