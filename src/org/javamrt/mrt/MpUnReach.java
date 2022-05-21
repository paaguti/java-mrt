// This file is part of java-mrt
// A library to parse MRT files

// This file is released under LGPL 3.0
// http://www.gnu.org/licenses/lgpl-3.0-standalone.html

package org.javamrt.mrt;
import java.util.Vector;

import org.javamrt.utils.RecordAccess;


public class MpUnReach
    implements Attribute
{
	// TODO : why am I not using SAFI here ???
  MpUnReach (byte[]buffer, boolean addPath)
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
	Nlri base = new Nlri (buffer, offset, afi, addPath);
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
