// This file is part of java-mrt
// A library to parse MRT files

// This file is released under LGPL 3.0
// http://www.gnu.org/licenses/lgpl-3.0-standalone.html

package org.javamrt.mrt;

import java.net.InetAddress;
import java.util.Objects;

public class KeepAlive extends MRTRecord {
    private final AS peerAs;
    private final InetAddress peerIp;
    private final AS localAs;
    private final InetAddress localIp;

    KeepAlive(byte[] header, byte[] record, AS peerAs, InetAddress peerIp, AS localAs, InetAddress localIp) {
        super(header, record);
        this.peerAs = peerAs;
        this.peerIp = peerIp;
        this.localAs = localAs;
        this.localIp = localIp;
    }

    @Override
    public InetAddress getPeer() {
        return peerIp;
    }

    @Override
    public AS getPeerAS() {
        return peerAs;
    }

    public AS getLocalAs() {
        return localAs;
    }

    public InetAddress getLocalIp() {
        return localIp;
    }

    @Override
    public String toString() {
        return String.format("KEEP_ALIVE|%d|%s|%s",
   				getTime(), MRTConstants.ipAddressString(peerIp, false), peerAs);
   	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final KeepAlive keepAlive = (KeepAlive) o;
        return getTime() == keepAlive.getTime() &&
                Objects.equals(peerAs, keepAlive.peerAs) &&
                Objects.equals(peerIp, keepAlive.peerIp) &&
                Objects.equals(localAs, keepAlive.localAs) &&
                Objects.equals(localIp, keepAlive.localIp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(peerAs, peerIp, localAs, localIp);
    }
}
