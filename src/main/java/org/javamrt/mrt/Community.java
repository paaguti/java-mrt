// This file is part of java-mrt
// A library to parse MRT files

// This file is released under LGPL 3.0
// http://www.gnu.org/licenses/lgpl-3.0-standalone.html

package org.javamrt.mrt;

import org.javamrt.utils.RecordAccess;

import java.util.Arrays;


public class Community implements Attribute {
	static final private int BGP_ATTR_COMMUNITY_NO_EXPORT = 0xFFFFFF01;
	static final private int BGP_ATTR_COMMUNITY_NO_ADVERTISE = 0xFFFFFF02;
	static final private int BGP_ATTR_COMMUNITY_NO_EXPORT_SUBCONFED = 0xFFFFFF03;
	static final private int BGP_ATTR_COMMUNITY_NOPEER = 0xFFFFFF04;

	protected byte[] community;

	private Community() {
		community = null;
	}

	public Community(byte[] buffer) {
		community = new byte[buffer.length];
		System.arraycopy(buffer, 0, community, 0, buffer.length);
	}

	public static Community empty() {
		return new Community();
	}

	public String toString() {
		return toStringBuilder(community).toString();
	}

	private static StringBuilder toStringBuilder(byte[] buffer) {
		StringBuilder result = new StringBuilder();

		if (null != buffer) {
			int len = buffer.length;

			for (int i = 0; i < len; i += 4) {
				long u32Community = RecordAccess.getU32(buffer, i);

				if (u32Community == BGP_ATTR_COMMUNITY_NO_EXPORT)
					result.append("no_export");
				else if (u32Community == BGP_ATTR_COMMUNITY_NO_ADVERTISE)
					result.append("no_advertise");
				else if (u32Community == BGP_ATTR_COMMUNITY_NO_EXPORT_SUBCONFED)
					result.append("no_export_subconfed");
				else if (u32Community == BGP_ATTR_COMMUNITY_NOPEER)
					result.append("no_peer");
				else {
					int comAS = RecordAccess.getU16(buffer, i);
					int comN = RecordAccess.getU16(buffer, i + 2);

					result.append(String.format("%d:%d", comAS, comN));
				}
				if (i + 4 != len)
					result.append(" ");
			}
		}
		return result;
	}

	public boolean equals(Community other) {
		return Arrays.equals(this.community, other.community);
	}

	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (this == o)
			return true;
		if (o instanceof Community)
			return equals((Community) o);
		return false;
	}

}
