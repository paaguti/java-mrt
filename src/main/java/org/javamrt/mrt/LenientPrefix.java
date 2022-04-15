package org.javamrt.mrt;

import org.javamrt.progs.route_btoa;
import org.javamrt.utils.RecordAccess;

import java.net.InetAddress;

public class LenientPrefix extends Prefix {

    public LenientPrefix(byte[] record, int offset, int afi) {
        super();
        maskLength = RecordAccess.getU8(record, offset++);
        if (afi != MRTConstants.AFI_IPv4 && afi != MRTConstants.AFI_IPv6) {
            route_btoa.System_err_println("Unknown AFI: " + afi + ". Assuming IPv4.");
        }
        base = new byte[afi == MRTConstants.AFI_IPv6 ? 16 : 4];
        mask = new byte[afi == MRTConstants.AFI_IPv6 ? 16 : 4];
        if (offset + nrBytes() > record.length) {
            throw new ArrayIndexOutOfBoundsException(String.format(
                    "Not enough input bytes (%s) to read NLRI prefix (%d bytes from offset %d)",
                    RecordAccess.arrayToString(record), nrBytes(), offset));
        }
        System.arraycopy(record, offset, base, 0, nrBytes());
        isInIpv4EmbeddedIpv6Format = MRTConstants.isInIpv4EmbeddedIpv6Format(this.base);
    }

    @Override
    public InetAddress getBroadcastAddress() {
        throw new UnsupportedOperationException("Not implemented in a lenient prefix");
    }

    @Override
    protected boolean matches(byte[] addr) {
        throw new UnsupportedOperationException("Not implemented in a lenient prefix");
    }
}
