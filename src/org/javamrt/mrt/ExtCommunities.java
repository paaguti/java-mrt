package org.javamrt.mrt;

import java.net.InetAddress;

import org.javamrt.utils.RecordAccess;


public class ExtCommunities implements Attribute {
	public ExtCommunities(byte[] buffer) throws Exception {
		/*
		 * TODO: why am I not using this
		 * 
		 * int subType = RecordAccess.getU8 (buffer, 1);
		 * if (subType == 0x02) { extComDet =
		 * "\nEXTENDED ROUTE TARGET COMMUNITY: ";
		 * } else if (subType == 0x03) { extComDet =
		 * "\nEXTENDED ROUTE ORIGIN COMMUNITY: ";
		 * }
		 * extCom=extCom+typeHigh+"|"+subType+"|";
		 * extComDet=extComDet+"\nType High: "+typeHigh+"\nTypeLow: "+subType;
		 */
		int typeHigh = RecordAccess.getU8(buffer, 0);
		int offset = 2;

		if ((typeHigh == 0x00) || (typeHigh == 0x02)) // Route Target Community
		{
			as = RecordAccess.getU16(buffer, offset);
			offset += 2;
			number = RecordAccess.getU32(buffer, offset);
			extCom = as + ":" + number;
		} else if (typeHigh == 0x01) {

			byte[] value = RecordAccess.getBytes(buffer, offset, 4);
			offset += 4;
			ip = InetAddress.getByAddress(value).getHostAddress();
			offset += 4;
			number = (long) RecordAccess.getU16(buffer, offset);
			extCom = extCom + "|" + ip + ":" + number;
		}
	}

	public String toString() {
		return extCom;
	}

	/*
	 * TODO private int typeHigh; private int subType;
	 */
	private String extCom;
	private long number;
	private String ip;
	private int as;
}
