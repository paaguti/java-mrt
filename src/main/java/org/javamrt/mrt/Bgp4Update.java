// This file is part of java-mrt
// A library to parse MRT files

// This file is released under LGPL 3.0
// http://www.gnu.org/licenses/lgpl-3.0-standalone.html

package org.javamrt.mrt;

import java.net.InetAddress;
import java.util.Comparator;

public class Bgp4Update
	extends MRTRecord
	implements Comparable<Bgp4Update>, Comparator<Bgp4Update>
{
	protected char updateType = '?';
	protected String updateStr = "BGP4MP";
	protected InetAddress peerIP = null;
	protected AS peerAS = new AS(0);
	protected Prefix prefix = null;
	protected Attributes updateAttr = null;

	public Bgp4Update(byte[] header, InetAddress peerIP, AS peerAS,
			Prefix prefix, Attributes updateAttr) {
		super(header);
		this.peerIP = peerIP;
		this.peerAS = peerAS;
		this.prefix = prefix;
		this.updateAttr = updateAttr;
	}

	public Bgp4Update(byte[] header, InetAddress peerIP, AS peerAS,
			Prefix prefix) {
		super(header);
		this.peerIP = peerIP;
		this.peerAS = peerAS;
		this.prefix = prefix;
		this.updateAttr = null;
	}

	public String toString() {
		String peerString = MRTConstants.ipAddressString(this.peerIP);

		String result = this.updateStr + "|" + this.time + "|"
				+ // this comes from MRTRecord
				this.updateType + "|" + peerString + "|"
				+ this.peerAS + "|" + this.prefix.toString();

		if (this.updateAttr != null)
			result += '|' + this.updateAttr.toString();

		return result;
	}

	/*
	 * public String shortString () { String result = this.time+"|"+ // this
	 * comes from MRTRecord this.updateType+"|"+
	 * this.peerIP.getHostAddress()+"|"+ this.prefix.toString();
	 *
	 * if (this.updateAttr != null) result += "|" +
	 * this.updateAttr.getASPath().toString();
	 *
	 * return result; }
	 */


	public boolean isIPv4() {
		return this.prefix.isIPv4();
	}

	public boolean isIPv6() {
		return this.prefix.isIPv6();
	}

	public Prefix getPrefix() {
		return this.prefix;
	}

	public AS getPeerAS() {
		return this.peerAS;
	}
	public InetAddress getPeer() {
		return this.peerIP;
	}

	public ASPath getASPath() {
		if (this.updateAttr != null)
			return this.updateAttr.getASPath();
		return null;
	}

	public boolean hasAsPathPrepend() {
		if (this.updateAttr != null)
			return this.updateAttr.getASPath().hasAsPathPrepend();
		return false;
	}

	public Attributes getAttributes() {
		return this.updateAttr;
	}

	/**
	 * Order by prefixes, then by peer and then by time.
	 */
	public int compareTo(Bgp4Update other) {
		int result = this.prefix.compareTo(other.prefix);
		if (result == 0) {
			result = org.javamrt.utils.InetAddressComparator.compara(this.peerIP,
					other.peerIP);
			if (result == 0) {
				if (this.time < other.time)
					result = -1;
				else if (this.time > other.time)
					result = 1;
/*
 *  Ignore sorting by update type
 *
 * 				else {
					if (this.updateType > other.updateType)
						result = 1;
					else if (this.updateType < other.updateType)
						result = -1;
					else
						result = 0;
				}
*/
			}
		}
		return result;
	}

	public int compare(Bgp4Update o1, Bgp4Update o2) {
		return o1.compareTo(o2);
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 *
	 * the implementation of equals:
	 *  1) same update type
	 *  2) at the same time
	 *  3) coming from the same peer
	 *  4) referring to the same prefix
	 *  if the update is an advertisement, check also the AS_PATH
	 */
	public boolean equals(Bgp4Update o) {
		if (this.updateType == ((Bgp4Update)o).updateType
			&& this.time == ((Bgp4Update)o).time
			&& this.peerIP.equals(((Bgp4Update)o).peerIP)
			&& this.prefix.equals(((Bgp4Update)o).prefix)) {
				// Advertisements => compare Attributes
				if (this instanceof Advertisement)
					return this.updateAttr.equals(o.updateAttr);
				return true;
			}
		return false;
	}

	public boolean equals(Object o) {
		if (null == o)
			return false;
		if (this == o)
			return true;
		if (o instanceof Bgp4Update) {
			return equals((Bgp4Update) o);
		}
		return false;
	}

	/**
	 * overrides {@link MRTRecord#toAdvertisement()}
	 * return this if it is an advertisement or null otherwise
	 *
	 * @see org.javamrt.mrt.MRTRecord#toAdvertisement()
	 * @see org.javamrt.mrt.Advertisement#toAdvertisement()
	 */
	@Override
	public Advertisement toAdvertisement() {
		return null;
	}

	/**
	 * overrides {@link MRTRecord#toWithdraw()}
	 * return this if it is an withdraw or null otherwise
	 *
	 * @see org.javamrt.mrt.MRTRecord#toWithdraw()
	 * @see org.javamrt.mrt.Withdraw#toWithdraw()
	 */
	@Override
	public Withdraw toWithdraw() {
		return null;
	}
}
