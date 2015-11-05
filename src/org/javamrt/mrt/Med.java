// This file is part of java-mrt
// A library to parse MRT files

// This file is released under LGPL 3.0
// http://www.gnu.org/licenses/lgpl-3.0-standalone.html

package org.javamrt.mrt;

import org.javamrt.utils.RecordAccess;

public class Med implements Attribute
{
	protected long med;

	public Med(long med)
	{
		this.med = med;
	}

	public Med(byte[] buffer)
	{
		med = RecordAccess.getU32 (buffer, 0);
	}

	public String toString ()
	{
		return Long.toString(med);
	}

	public long getMed ()
	{
		return med;
	}

	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (this == o)
			return true;
		if (o.getClass() == this.getClass())
			return ((Med) o).med == this.med;
		return false;
	}
}
