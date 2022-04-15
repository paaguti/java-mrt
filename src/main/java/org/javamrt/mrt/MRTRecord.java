// This file is part of java-mrt
// A library to parse MRT files

// This file is released under LGPL 3.0
// http://www.gnu.org/licenses/lgpl-3.0-standalone.html

package org.javamrt.mrt;

import org.javamrt.utils.RecordAccess;

import java.net.InetAddress;
import java.util.Arrays;


public class MRTRecord {
	final protected byte[] header;
	final protected byte[] body;

	protected MRTRecord(byte[] header, byte[] body) {
		this.header = header;
		this.body = body;
	}

	public byte[] getBytes() {
		byte[] result = Arrays.copyOf(header, header.length + body.length);
		System.arraycopy(body, 0, result, header.length, body.length);
		return result;
	}

	public long getTime() {
		return RecordAccess.getU32(header, 0);
	}

	public int getType() {
		return RecordAccess.getU16(header, 4);
	}

	public long getSubType() {
		return RecordAccess.getU16(header, 6);
	}

	// will be overriden by derived classes

	public String toString() {
		return String
				.format("MRT|%d|%d|%d", getTime(), getType(), getSubType());
	}

	public boolean hasAsPathPrepend() {
		return false;
	}

	public Prefix getPrefix() {
		return null;
	}

	public InetAddress getPeer() {
		return null;
	}

	public ASPath getASPath() {
		return null;
	}

	public AS getPeerAS() {
		// TODO Auto-generated method stub
		return null;
	}

	// Override in subclasses
	public TableDump toTableDump() {
		return null;
	}

	// Override in subclasses
	public Advertisement toAdvertisement() {
		return null;
	}

	// Override in subclasses
	public Withdraw toWithdraw() {
		return null;
	}

}
