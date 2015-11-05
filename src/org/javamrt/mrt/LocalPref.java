// This file is part of java-mrt
// A library to parse MRT files

// This file is released under LGPL 3.0
// http://www.gnu.org/licenses/lgpl-3.0-standalone.html

package org.javamrt.mrt;

import org.javamrt.utils.RecordAccess;


public class LocalPref implements Attribute {
	protected long localPref;
	
	LocalPref(byte[] buffer) {
		localPref = RecordAccess.getU32(buffer, 0);
	}

	public String toString() {
		return "" + localPref;
	}

	public long getLocalPref() {
		return localPref;
	}

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
