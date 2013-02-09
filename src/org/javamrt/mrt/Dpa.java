// This file is part of java-mrt
// A library to parse MRT files

// This file is released under LGPL 3.0
// http://www.gnu.org/licenses/lgpl-3.0-standalone.html

package org.javamrt.mrt;

import org.javamrt.utils.RecordAccess;

public class Dpa
    implements Attribute
{
  public Dpa (byte[]buffer)
  {
    asNo = RecordAccess.getU16 (buffer, 0);
    dpaValue = RecordAccess.getU32 (buffer, 2);
  }

  public String toString ()
  {
    return String.format("%d:%d", asNo, dpaValue);
  }

  protected int asNo;
  protected long dpaValue;

	public boolean equals(Dpa other) {
		  return this.asNo     == other.asNo &&
		  		 this.dpaValue == other.dpaValue;
	}

	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (this == o)
			return true;
		if (o.getClass() == this.getClass())
			return equals((Dpa) o);
		return false;
	}


}
