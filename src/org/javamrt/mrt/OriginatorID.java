package org.javamrt.mrt;

import org.javamrt.utils.RecordAccess;


public class OriginatorID implements Attribute {

	OriginatorID(byte[] buffer) {
		id = RecordAccess.getU32(buffer, 0);
	}

	public String toString() {
		return "" + id;
	}

	public long originatorId() {
		return id;
	}

	protected long id;

	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (this == o)
			return true;
		if (o.getClass() == this.getClass())
			return ((OriginatorID) o).id == this.id;
		return false;
	}

}
