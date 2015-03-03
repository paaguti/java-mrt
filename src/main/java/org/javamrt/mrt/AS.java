// This file is part of java-mrt
// A library to parse MRT files

// This file is released under LGPL 3.0
// http://www.gnu.org/licenses/lgpl-3.0-standalone.html

package org.javamrt.mrt;

import java.util.Comparator;

/**
 * @author paag
 *
 * tests in AStest
 */
public class AS implements Comparable<AS>, Comparator<AS> {
	public static final int AS_TRANS = 23456;
	public static final AS NullAS = new AS(0);
	// protected long ASnumber;
	protected byte ASCode[] = {0,0,0,0};

	protected AS() {
	}

	public AS(long ASnumber) {
		setASCode(ASnumber & 0xffffffff);
	}

	public AS(byte[] as) throws Exception {
		setASCode(as);
	}


	private void setASCode(long val) {
		for (int i = 0; i < 4; i++)
			ASCode[i] = (byte) ((val >> ((3 - i) * 8)) & 0xff);
	}

	private void setASCode(byte[] newval) throws Exception {
		if (newval.length == 2) {
			this.ASCode[0] =
			this.ASCode[1] = 0;
			this.ASCode[2] = newval[0];
			this.ASCode[3] = newval[1];
		} else if (newval.length == 4) {
			this.ASCode[0] = newval[0];
			this.ASCode[1] = newval[1];
			this.ASCode[2] = newval[2];
			this.ASCode[3] = newval[3];
		} else
			throw new Exception(String.format("AS must be 2 or 4 bytes long (%d not allowed)",newval.length));
	}

	public long getASN() {
		long result = 0;
		for (int i=0; i<ASCode.length;i++) {
			result = (result << 8) & 0xffffff00;
			result = result | (ASCode[i] & 0x000000ff);
		}
		return result;
	}

	public void setASN(long ASnumber) {
		this.setASCode(ASnumber & 0xffffffff);
	}

//	public long getASN() {
//		return this.ASnumber;
//	}


	public int compareTo(org.javamrt.mrt.AS other) {
		return compare(this, other);
	}

	public int compare(org.javamrt.mrt.AS as1, org.javamrt.mrt.AS as2) {
		for (int i=0;i<4;i++) {
			if (as1.ASCode[i] == as2.ASCode[i])
				continue;
			return ((int)as1.ASCode[i] & 0xff) - ((int)as2.ASCode[i] & 0xff);
		}
		return 0;
	}

	/**
	 * @param AS other
	 * @return true if the other AS
	 */
	private boolean equals(AS other) {
		for (int i=0; i<4; i++)
			if (this.ASCode[i] != other.ASCode[i])
				return false;
		return true;
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

	/**
	 * @return true if this is a 4 byte AS number
	 */
	public boolean is4Byte() {
		return this.ASCode[0] != 0 || this.ASCode[1] != 0;
	}

	/**
	 * @return true if the AS number is 23456, the 4 byte AS place holder
	 * comparison is byte by byte
	 */
	public boolean isPlaceholder() {
		return this.equals(AS_TRANS);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		if (this.equals(0))        return "AS_NULL";
		if (this.equals(AS_TRANS)) return "AS_TRANS";
		if (this.is4Byte())        return String.format("%d",(hiWord()<<16) + loWord());
		return String.format("%d", loWord());
	}

	private int hiWord() {
		return ((this.ASCode[0] & 0xff) << 8) | (this.ASCode[1] & 0xff);
	}

	private int loWord() {
		return ((this.ASCode[2] & 0xff) << 8) | (this.ASCode[3] & 0xff);
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
				result = new AS(Long.parseLong(asspec));
			}
		} else {
			throw new Exception ("Incorrect AS specification: "+asspec);
		}
		return result;
	}
}
