// This file is part of java-mrt
// A library to parse MRT files

// This file is released under LGPL 3.0
// http://www.gnu.org/licenses/lgpl-3.0-standalone.html

package org.javamrt.mrt;

import org.javamrt.utils.Debug;

import java.net.InetAddress;

public class Open
  extends MRTRecord
{
    protected InetAddress peerIP = null;
    protected AS peerAS = new AS(0);
    protected long bgpId;

    public Open(byte[] header, byte[] record, InetAddress peerIP, AS peerAS, long bgpId) {
        super(header, record);
        this.peerIP = peerIP;
        this.peerAS = peerAS;
        this.bgpId = bgpId;
    }

      Open (byte[]header, byte[]record)
  {
    super (header, record);

    if (Debug.compileDebug) {
    	Debug.println("Open message");
    	Debug.dump(header);
    	Debug.dump(record);
    }

  }

    public AS getPeerAS() {
        return this.peerAS;
    }
    public InetAddress getPeer() {
        return this.peerIP;
    }
    public long getBgpId(){return this.bgpId;}

    public String toString() {
        String peerString = MRTConstants.ipAddressString(this.peerIP, false);

        StringBuilder result = new StringBuilder("OPEN").append('|')
                .append(getTime()).append('|')
                // this comes from MRTRecord
                .append(peerString).append('|')
                .append(this.peerAS).append('|')
                .append(this.bgpId);

        return result.toString();
    }
}
