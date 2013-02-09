package org.javamrt.mrt; 

import org.javamrt.utils.RecordAccess;

public class Med
  implements Attribute
{
  public Med (long med)
  {
    this.med = med;
  }
  
  public Med (byte[] buffer)
  {
    med = RecordAccess.getU32 (buffer, 0);
  }
  
  public String toString ()
  {
    return ""+med;
  }
  
  public long getMed ()
  {
    return med;
  }
  
  protected long med;
  
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
