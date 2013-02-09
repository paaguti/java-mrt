package org.javamrt.mrt;

import org.javamrt.utils.RecordAccess;


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
		for (int i = 0; i < buffer.length; i++)
			community[i] = buffer[i];
	}

	public static Community empty() {
		return new Community();
	}
	
	public String toString() {
		return toStringBuffer(community).toString();
	}

	private static StringBuffer toStringBuffer(byte[] buffer) {
		StringBuffer result = new StringBuffer();

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
		if (this.community.length != other.community.length)
			return false;
		for (int i = 0; i < this.community.length; i++)
			if (this.community[i] != other.community[i])
				return false;
		return true;
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
