// This file is part of java-mrt
// A library to parse MRT files

// This file is released under LGPL 3.0
// http://www.gnu.org/licenses/lgpl-3.0-standalone.html

package org.javamrt.mrt;

import org.javamrt.utils.RecordAccess;

public class Refresh
  extends MRTRecord
{
  /*
   *
   * RFC 2918
   *
   *
          Type: 5 - ROUTE-REFRESH

          Message Format: One <AFI, SAFI> encoded as

                  0       7      15      23      31
                  +-------+-------+-------+-------+
                  |      AFI      | Res.  | SAFI  |
                  +-------+-------+-------+-------+

          The meaning, use and encoding of this <AFI, SAFI> field is the
          same as defined in [BGP-MP, sect. 7]. More specifically,

               AFI  - Address Family Identifier (16 bit).

               Res. - Reserved (8 bit) field. Should be set to 0 by the
                      sender and ignored by the receiver.

               SAFI - Subsequent Address Family Identifier (8 bit).
  */

  private int AFI;
  private int SAFI;

  public Refresh (byte[]header, byte[]record)
  {
    super (header);
    this.AFI  = RecordAccess.getU16(record,0);
    this.SAFI = RecordAccess.getU8 (record,2);
  }

  public String toString ()
  {
    return String.format("BGP4MP|%d|REFRESH|%d|%d",
			 this.time,
			 this.AFI,
			 this.SAFI);
  }
}
