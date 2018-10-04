// This file is part of java-mrt
// A library to parse MRT files

// This file is released under LGPL 3.0
// http://www.gnu.org/licenses/lgpl-3.0-standalone.html

package org.javamrt.mrt;

import org.javamrt.utils.RecordAccess;


public class OriginatorID implements Attribute {

	OriginatorID(byte[] buffer) {
		id = RecordAccess.getU32(buffer, 0);
	}

	public String toString() {
		return Long.toString(id);
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
