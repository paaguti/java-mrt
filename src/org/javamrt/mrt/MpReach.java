// This file is part of java-mrt
// A library to parse MRT files

// This file is released under LGPL 3.0
// http://www.gnu.org/licenses/lgpl-3.0-standalone.html

package org.javamrt.mrt;

import java.net.InetAddress;
import java.util.Vector;

import org.javamrt.utils.Debug;
import org.javamrt.utils.RecordAccess;

public class MpReach implements Attribute {
	/*
	 * Implement RFC2283 MP_REACH_NLRI
	 * ignoring subnetwork points of attachment
	 */
	private int afi;
	private int safi;
	private InetAddress nextHop;
	private Vector<Nlri> nlriVector;

	// private int snpaNo;
	// private Vector < InetAddress > snpaVector;

	MpReach(byte[] buffer, boolean addPath) throws Exception {
		// snpaVector = new Vector < InetAddress > ();
		nlriVector = new Vector<Nlri>();

		/**
		 * +---------------------------------------------------------+
		 * | Address  Family Identifier (2 octets)                   |
		 * +---------------------------------------------------------+
		 * | Subsequent Address Family Identifier (1 octet)          |
		 * +---------------------------------------------------------+
		 * | Length of Next Hop Network Address (1 octet)            |
		 * +---------------------------------------------------------+
		 * | Network Address of Next Hop (variable)                  |
		 * +---------------------------------------------------------+
		 */
		afi = RecordAccess.getU16(buffer, 0);
		safi = RecordAccess.getU8(buffer, 2);
		int nextHopLen = RecordAccess.getU8(buffer, 3);
		if (Debug.compileDebug) {
			Debug.printf("ATTRIBUTE_MP_REACH: AFI = 0x%04x,SAFI=0x%02x,NHL=%d\n", afi,
					safi, nextHopLen);
			Debug.dump(buffer);
		}

		// TODO: investigate why sometimes afi > AFI_MAX

		if (afi > MRTConstants.AFI_MAX)
			return;
		if (nextHopLen == 0)
			return;

		int offset = 4;
		/*
		 * RFC 2545 BGP-4 Multiprotocol Extensions for IPv6 IDR
		 *
		 * A BGP speaker shall advertise to its peer in the Network Address of
		 * Next Hop field the global IPv6 address of the next hop, potentially
		 * followed by the link-local IPv6 address of the next hop.
		 *
		 * The value of the Length of Next Hop Network Address field on a
		 * MP_REACH_NLRI attribute shall be set to 16, when only a global
		 * address is present, or 32 if a link-local address is also included in
		 * the Next Hop field.
		 */

		int nhl = nextHopLen;
		if (afi == MRTConstants.AFI_IPv6) {
			if (nhl == 32)
				nhl = 16;
		}

		byte[] abuf = RecordAccess.getBytes(buffer,offset, nhl);
		nextHop = InetAddress.getByAddress(abuf);
		offset += nextHopLen;

		/**
		 *
		 * +-------------------------------+
		 * | Number of SNPAs (1 octet)     |
		 * +-------------------------------+
		 * | Length of first SNPA(1 octet) |
		 * +-------------------------------+
		 * | First SNPA (variable)         |
		 * +-------------------------------+
		 * | ...                           |
		 * +-------------------------------+
		 * | Length of Last SNPA (1 octet) |
		 * +-------------------------------+
		 * | Last SNPA (variable)          |
		 * +-------------------------------+
		 *
		 **/

		int snpaNo = RecordAccess.getU8(buffer, offset);

		if (Debug.compileDebug)
			Debug.println(" NEXT_HOP: " + nextHop.getHostAddress()
					+ "\n SNPA_NO: " + snpaNo);

		offset++;
		for (int i = 0; i < snpaNo; i++) {

			int snpaLen = RecordAccess.getU8(buffer, offset);
			offset++;
			/*
			 * Ignore the SNPA's for the moment being:
			 *
			 * byte[]snpa = RecordAccess.getBytes (this.buffer, offset,
			 * snpaLen); InetAddress subnetPA = InetAddress.getByAddress (snpa);
			 * snpavector.addElement(subnetPA);
			 */
			offset += snpaLen;
		}

		/*
		 * +---------------------------------------------------+
		 * | Network Layer Reachability Information (variable) |
		 * +---------------------------------------------------+
		 */

		while (offset < buffer.length) {
			Nlri base = new Nlri(buffer, offset, afi, addPath);
			offset += base.getOffset();
			nlriVector.addElement(base);
		}

	}

	public Vector<Nlri> getNlri() {
		return nlriVector;
	}

	public InetAddress getNextHop() {
		return nextHop;
	}

	/*
	 * public Vector < InetAddress > getSNPA () { return snpaVector; }
	 */
	public String toString() {
		String result = "NEXT HOP: " + nextHop.getHostAddress();
		for (Nlri nlri : nlriVector)
			result += "\n NRLI:" + nlri.toString();
		return result;
	}
}
