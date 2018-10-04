// This file is part of java-mrt
// A library to parse MRT files

// This file is released under LGPL 3.0
// http://www.gnu.org/licenses/lgpl-3.0-standalone.html

package org.javamrt.mrt;

import org.javamrt.progs.route_btoa;
import org.javamrt.utils.RecordAccess;

//IPv4/IPv6 address and mask length extractor from NLRI field in BGP

public class Nlri
        extends Prefix {
    // from Prefix.java
    //   byte[] addr;
    //   int    maskLength;

    public Nlri (byte[]record, int offset, int afi)
	throws Exception

  {
      super();
    /*
      Receives whole BGP update package as byte [] record with integer offset as indicator of the position of length field
      integer afi is used for deciding the type of address
      masklen value is inside length octet, where the value is given in bits
      +---------------------------+
      |   Length (1 octet)        |
      +---------------------------+
      |   Prefix (variable)       |
      +---------------------------+
    */
        if (afi == MRTConstants.AFI_IPv4)
            this.base = new byte[4];            //deciding the type or address
        else if (afi == MRTConstants.AFI_IPv6)
            this.base = new byte[16];
        else
            throw new Exception("NLRI: unknown Address Family: " + afi);
        this.maskLength = RecordAccess.getU8(record, offset); //reading length byte (bits)
        if (afi == MRTConstants.AFI_IPv4 && maskLength > 32) {
            route_btoa.System_err_println(String.format("Bit length %d is not feasible for IPv4 prefix (offset %d)%n", maskLength, offset));
            throw new BGPFileReaderException(String.format(
                    "Bit length %d is not feasible for IPv4 prefix (offset %d)", maskLength, offset), record);
        } else if (afi == MRTConstants.AFI_IPv6 && maskLength > 128) {
            route_btoa.System_err_println(String.format("Bit length %d is not feasible for IPv6 prefix (offset %d)%n", maskLength, offset));
            throw new BGPFileReaderException(String.format(
                    "Bit length %d is not feasible for IPv6 prefix (offset %d)", maskLength, offset), record);
        }
        offset++;
        if (offset + nrBytes() > record.length) {
            throw new ArrayIndexOutOfBoundsException(String.format(
                    "Not enough input bytes (%s) to read NLRI prefix (%d bytes from offset %d)",
                    RecordAccess.arrayToString(record), nrBytes(), offset));
        }
        System.arraycopy(record, offset, base, 0, nrBytes());
        setPrefix(this.base, this.maskLength);
    }
}
