package org.javamrt.mrt;

import org.javamrt.utils.RecordAccess;


public class LocalPref implements Attribute {
	LocalPref(byte[] buffer) {
		localPref = RecordAccess.getU32(buffer, 0);
	}

	public String toString() {
		return "" + localPref;
	}

	public long getLocalPref() {
		return localPref;
	}

	protected long localPref;

	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (this == o)
			return true;
		if (o.getClass() == this.getClass())
			return ((LocalPref) o).localPref == this.localPref;
		return false;
	}

}
