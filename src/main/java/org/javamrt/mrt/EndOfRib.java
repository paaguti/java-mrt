package org.javamrt.mrt;

import java.net.InetAddress;

public class EndOfRib extends Bgp4Update {
    protected char updateType = 0;

    public EndOfRib(byte[] header, byte[] record, InetAddress peerIP, AS peerAS, String updateStr) {
        super(header, record, peerIP, peerAS, null, updateStr);
    }

    public EndOfRib(byte[] header, byte[] record, InetAddress peerIP, AS peerAS, Attributes updateAttr, String updateStr) {
        super(header, record, peerIP, peerAS, null, updateAttr, updateStr);
    }

    @Override
    public String toString() {
        String peerString = MRTConstants.ipAddressString(this.peerIP, false);
        String attrString = updateAttr == null ? "" : updateAttr.toString();

        return String.format("%s|%s|EOR|%s|%s|%s",
                updateStr, getTime(), peerString, peerAS, attrString);
    }
}
