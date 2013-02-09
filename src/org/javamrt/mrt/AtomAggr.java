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
