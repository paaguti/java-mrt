// This file is part of java-mrt
// A library to parse MRT files

// This file is released under LGPL 3.0
// http://www.gnu.org/licenses/lgpl-3.0-standalone.html

package org.javamrt.mrt;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Comparator;

// matches IPv4 and IPv6 prefixes

public class Prefix implements Comparable<Prefix>, Comparator<Prefix> {

	protected boolean isInIpv4EmbeddedIpv6Format = false;

	// for Nlri.java
	protected byte[] base;
	protected byte[] mask;
    protected byte[] broadcast;

	// protected InetAddress baseAddress;
	// protected InetAddress broadcastAddress;
	protected int maskLength;

	protected Prefix() {/* for inheritance*/}

	public Prefix(InetAddress addr, int maskLength)
			throws PrefixMaskException, UnknownHostException {
		setPrefix(addr.getAddress(), maskLength);
	}

	public Prefix(byte[] addr, int maskLength)
			throws PrefixMaskException, UnknownHostException {
		setPrefix(addr, maskLength);
	}

	public static Prefix parseString(String s) throws NumberFormatException, UnknownHostException, PrefixMaskException {
		return new Prefix(InetAddress.getByName(s.replaceAll("/.+$", "")),
				Integer.parseInt(s.replaceAll("^[^/]+/", "")));
	}

	protected void setPrefix(byte[] addr, int maskLen)
			throws PrefixMaskException, UnknownHostException {
		/*
		 * v2.00
		 *   Check before anything else
		 *   backported from org.paag.netkit v2.00
		 */
		if (addr.length != MRTConstants.IPv4_LENGTH && addr.length != MRTConstants.IPv6_LENGTH)
			throw new PrefixMaskException(addr, maskLen);

		if (addr.length * 8 < maskLen)
			throw new PrefixMaskException(addr, maskLen);

		this.base      = new byte[addr.length];
		this.mask      = new byte[addr.length];
		this.broadcast = new byte[addr.length];

		this.maskLength = maskLen;

		/*
		 * bug in version 1.00
		 *
		if (this.base.length * 8 < this.maskLength) {
			throw new PrefixMaskException(this.base, this.maskLength);
		}
		*/
		System.arraycopy(addr, 0, this.base, 0, this.mask.length);
		for (int n = this.maskLength; n > 0; n--) {
			for (int i = 0; i < this.mask.length; i++) {
				byte carry = (byte) (this.mask[i] & 0x01);
				this.mask[i] = (byte) (0x80 | this.mask[i] >> 1);
				if (carry == 0)
					break;
			}
		}
		//
		// 10.0.1.0/24 is legal, but 10.0.1.0/23 is illegal
		//
		for (int i = 0; i < this.mask.length; i++)
			if (this.base[i] != (byte) (this.base[i] & this.mask[i]))
				throw new PrefixMaskException(this.base, this.maskLength);

		for (int i = 0; i < this.mask.length; i++) {
			this.broadcast[i] = (byte) (this.base[i] | ~this.mask[i]);
		}

		isInIpv4EmbeddedIpv6Format = MRTConstants.isInIpv4EmbeddedIpv6Format(this.base);
	}

	public InetAddress getBaseAddress() {
		try {
			return InetAddress.getByAddress(this.base);
		} catch (Exception e) {
			return null; // should never happen
		}
	}

	public byte[] getByteBaseArrayAddress(){
		return this.base;
	}

	public boolean getIsInIpv4EmbeddedIpv6Format(){
		return isInIpv4EmbeddedIpv6Format;
	}

	public InetAddress getBroadcastAddress() {
		try {
			return InetAddress.getByAddress(this.broadcast);
		} catch (Exception e) {
			return null; // should never happen
		}
	}

	public int getMaskLength() {
		return this.maskLength;
	}

	public void setMaskLength(int maskLength)
	    throws PrefixMaskException, UnknownHostException {
	    byte[] temp= new byte[this.base.length];
	    System.arraycopy(base, 0, temp, 0, base.length);
	    setPrefix(temp,maskLength);
	}

	public boolean matches(InetAddress addr) {
		return matches(addr.getAddress());
	}

	protected boolean matches(byte[] addr) {
		if (addr.length == this.base.length) {
			for (int i = 0; i < this.base.length; i++) {
				if (this.mask[i] == 0)
					return true;
				if ((addr[i] & this.mask[i]) != this.base[i])
					return false;
			}
			return true;
		}
		return false;
	}

	public boolean includes(Prefix other) {
		return this.matches(other.base)
				&& this.matches(other.broadcast);
	}

	public boolean isIncludedBy(Prefix other) {
		return other.includes(this);
	}

	public String toString() {
		try {
			return MRTConstants.ipAddressString(this.base, isInIpv4EmbeddedIpv6Format).
				concat("/" + this.maskLength);
		} catch (Exception e) {
			return "??/" + this.maskLength;
		}
	}

	public boolean isIPv4() {
		return this.base.length == MRTConstants.IPv4_LENGTH;
	}

	public boolean isIPv6() {
		return this.base.length == MRTConstants.IPv6_LENGTH;
	}

	public int compare(Prefix p1, Prefix p2) {
		int result = p1.base.length - p2.base.length;
		if (result == 0) {
			for (int i=0; i<p1.base.length; i++) {
				result = ((int)p1.base[i] & 0xff) - ((int)p2.base[i] & 0xff);
				if (result != 0)
					break;
			}
			if (result == 0)
				result = p1.maskLength - p2.maskLength;
		}
		return result;
	}

	public int compareTo(Prefix other) {
		return compare(this, other);
	}

	public boolean equals(Prefix other) {
		return 0 == compare(this, other);
	}

	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null)
			return false;
		if (o instanceof Prefix)
			return this.equals(((Prefix) o));
		return false;
	}

	public boolean isDefault() {
		if (this.maskLength != 0)
			return false;
		for (int i=0; i < base.length; i++)
			if (base[i] != 0)
				return false;
		return true;
	}

	/**
	 * @return number of bytes required to represent this prefix
	 */
	public int nrBytes() {
		return maskLength > 0 ? 1 + (maskLength - 1) / 8 : 0;
	}

	/**
	 * @return the number of bytes needed to represent this prefix, plus one
	 */
	public int getOffset() {
		return 1 + nrBytes();
	}
}
