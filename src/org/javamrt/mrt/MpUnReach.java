package org.javamrt.mrt; 
import java.util.Vector;

import org.javamrt.utils.RecordAccess;


public class MpUnReach
    implements Attribute
{
	// TODO : why am I not using SAFI here ???
  MpUnReach (byte[]buffer)
    throws Exception
  {
    int offset = 0;
    int len = buffer.length;
    int afi = RecordAccess.getU16 (buffer, offset);
    offset += 2;
    // int safi = RecordAccess.getU8 (buffer, offset);
    offset++;
    nlri = new StringBuffer ();

    while (len > offset)
      {
	Nlri base = new Nlri (buffer, offset, afi);
	offset += base.getOffset ();
	
	nlriVector.addElement (base);
	nlri.append (base.toString () + " ");
      }
  }
  
  public Vector < Nlri > getNlri ()
  {
    return nlriVector;
  }

  public String toString ()
  {
    return nlri.toString ();
  }
  
  private Vector < Nlri > nlriVector = new Vector < Nlri > ();
  private StringBuffer nlri;
}
