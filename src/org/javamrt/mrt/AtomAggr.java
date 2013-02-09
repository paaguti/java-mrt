// This file is part of java-mrt
// A library to parse MRT files

// This file is released under LGPL 3.0
// http://www.gnu.org/licenses/lgpl-3.0-standalone.html

package org.javamrt.mrt;

public class AtomAggr
    implements Attribute
{
  AtomAggr ()
  {
    atomAggr = "AG";
  }
  public String toString ()
  {
    return atomAggr;
  }
  public String getAtomAggr ()
  {
    return atomAggr;
  }

  public boolean equals(Object o) {
	  if (o == null) return false;
	  if (o == this) return true;
	  if (o instanceof AtomAggr) return true;
	  return false;
  }
  private String atomAggr;
}
