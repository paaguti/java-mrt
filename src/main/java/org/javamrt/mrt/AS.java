// This file is part of java-mrt
// A library to parse MRT files

// This file is released under LGPL 3.0
// http://www.gnu.org/licenses/lgpl-3.0-standalone.html

package org.javamrt.mrt;

import java.math.BigInteger;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author paag
 *
 * tests in AStest
 */
public class AS implements Comparable<AS>, Comparator<AS> {
	public static final int AS_TRANS = 23456;
	public static final AS NullAS = new AS(0);
	private long asNumber;

	protected AS() {
	}

	public AS(long ASnumber) {
		asNumber = ASnumber & 0xffffffffL;
	}

	public AS(byte[] as) {
		if (as.length != 2 && as.length != 4) {
			throw new IllegalArgumentException(String.format("AS must be 2 or 4 bytes long (%d not allowed)", as.length));
		}
		asNumber = new BigInteger(1, as).longValue();
	}

	public long getASN() {
		return asNumber;
	}

	public void setASN(long ASnumber) {
		this.asNumber = ASnumber & 0xffffffffL;
	}

	public int compareTo(org.javamrt.mrt.AS other) {
		return compare(this, other);
	}

	public int compare(org.javamrt.mrt.AS as1, org.javamrt.mrt.AS as2) {
		return Long.compare(as1.asNumber, as2.asNumber);
	}

	/**
	 * @param AS other
	 * @return true if the other AS
	 */
	private boolean equals(AS other) {
		return other != null && this.asNumber == other.asNumber;
	}

	/**
	 *
	 * @param other textual representation of AS
	 * @return true if the textual representation of this equals the other
	 */
	private boolean equals(String other) {
		if (other.startsWith("AS"))
			return other.equals(toString("AS"));
		return other.equals(toString());
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object other) {
		if (other == null)
			return false;
		if (other == this)
			return true;
		if (other instanceof String)
			return equals((String) other);
		if (other instanceof AS)
			return equals((AS) other);
		return false;
	}

	@Override
	public int hashCode() {
		return Long.hashCode(asNumber);
	}

	/**
	 * @return true if this is a 4 byte AS number
	 */
	public boolean is4Byte() {
		return asNumber > 0xFFFFL;
	}

	/**
	 * @return true if the AS number is 23456
	 */
	public boolean isPlaceholder() {
		return asNumber == AS_TRANS;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
// this has never worked, so do not think we should fix it now
//		if (this.equals(0))        return "AS_NULL";
//		if (this.equals(AS_TRANS)) return "AS_TRANS";

		return String.valueOf(asNumber);
	}

	/**
	 * @param prefix
	 * @return a String containing the prefix followed by the AS textual representation
	 */
	public String toString(String prefix) {
		return prefix.concat(this.toString());
	}

	public static AS parseString(String asspec) throws Exception {

		AS result = null;
		if (asspec.matches("^(AS){0,1}[1-9][0-9]*(\\.[0-9]+){0,1}$")) {
			asspec = asspec.replaceFirst("AS", "");
			if (asspec.matches("^[1-9][0-9]*\\.[0-9]+$")) {
				long asnum = Long.parseLong(asspec.replaceFirst("\\.[0-9]+$",""));
				long asnum1 = Long.parseLong(asspec.replaceFirst("[0-9]+\\.", ""));

				if ((asnum < 0) || (asnum > 65535) ||
					(asnum1 < 0) || (asnum1 > 65535))
					throw new Exception ("numbers must be in [0,65535]");
				asnum = asnum * 0x10000L + asnum1;
				result = new AS(asnum);
			} else {
				long number = Long.parseLong(asspec);
				if (number < 0 || number > 0xFFFFFFFFL) {
					throw new IllegalArgumentException("number must be in [0,4294967295]");
				}
				result = new AS(number);
			}
		} else {
			throw new Exception ("Incorrect AS specification: "+asspec);
		}
		return result;
	}

	/**
	 * @return list of ASNs, especially useful when not interested in whether it is some type of set, but just want to deal with the numbers
	 */
	public List<AS> getASList() {
		return Collections.singletonList(this);
	}
}
