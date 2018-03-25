// This file is part of java-mrt
// A library to parse MRT files

// This file is released under LGPL 3.0
// http://www.gnu.org/licenses/lgpl-3.0-standalone.html

package org.javamrt.mrt;

import java.net.InetAddress;

import org.javamrt.utils.RecordAccess;


public class NextHop implements Attribute {

	protected InetAddress nextHopIA;
	protected byte[] nextHopIABytes;
	private boolean isInIpv4EmbeddedIpv6Format = false;

	public NextHop() throws Exception {
		byte nh[] = { (byte) 255, (byte) 255, (byte) 255, (byte) 255 };
		this.nextHopIA = InetAddress.getByAddress(nh);
	}

	public NextHop(byte[] buffer) throws Exception {
		int offset = 0;
		int len = buffer.length;
        nextHopIABytes = RecordAccess.getBytes(buffer, offset, len);
		this.nextHopIA = InetAddress.getByAddress(nextHopIABytes);
		isInIpv4EmbeddedIpv6Format = MRTConstants.isInIpv4EmbeddedIpv6Format(nextHopIABytes);
	}

	public NextHop(InetAddress nextHopIA) throws NullPointerException {
		if (nextHopIA == null)
			throw new NullPointerException(
					"Initialising nextHop with null pointer");
		this.nextHopIA = nextHopIA;
	}

	public InetAddress getNextHopIA() {
		return nextHopIA;
	}
	public byte[] getNextHopIABytes() {
	    return nextHopIABytes;
	}

	public String toString() {
		if (this.nextHopIA == null)
			return null;
		return MRTConstants.ipAddressString(nextHopIA, isInIpv4EmbeddedIpv6Format);
	}

	public boolean isDefault() {
		byte[] buffer = this.nextHopIA.getAddress();
		for (int i = 0; i < buffer.length; i++)
			if (buffer[i] != 0)
				return false;
		return true;
	}

	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (this == o)
			return true;
		if (o.getClass() == this.getClass())
			return this.nextHopIA.equals(((NextHop) o).nextHopIA);
		return false;
	}
}
