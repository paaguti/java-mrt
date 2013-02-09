// This file is part of java-mrt
// A library to parse MRT files

// This file is released under LGPL 3.0
// http://www.gnu.org/licenses/lgpl-3.0-standalone.html

package org.javamrt.mrt;

import java.net.InetAddress;

import org.javamrt.utils.RecordAccess;


public class MRTRecord {
	protected byte[] record;
	protected int type;
	protected int subtype;
	protected long time;

	public MRTRecord() {
	}

	protected MRTRecord(byte[] header) {
		setHeaderData(header);
	}

	public void setGeneric(byte[] header, byte[] record) {
		setHeaderData(header);
		this.record = record;
	}

	public void setHeaderData(byte[] header) {
		this.time = RecordAccess.getU32(header, 0);
		this.type = RecordAccess.getU16(header, 4);
		this.subtype = RecordAccess.getU16(header, 6);
	}

	public long getTime() {
		return this.time;
	}

	public int getType() {
		return this.type;
	}

	public long getSubType() {
		return this.subtype;
	}

	// will be overriden by derived classes

	public String toString() {
		return String
				.format("MRT|%d|%d|%d", this.time, this.type, this.subtype);
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
