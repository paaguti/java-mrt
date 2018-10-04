// This file is part of java-mrt
// A library to parse MRT files

// This file is released under LGPL 3.0
// http://www.gnu.org/licenses/lgpl-3.0-standalone.html

package org.javamrt.mrt;

import java.net.InetAddress;

public class Notification
  extends MRTRecord
{
  protected InetAddress peerIP = null;
  protected AS peerAS = new AS(0);

  public Notification (byte[]header, byte[]record)
  {
    super(header, record);
  }

  public Notification(byte[] header, byte[] record, InetAddress peerIP, AS peerAS) {
    super(header, record);
    this.peerIP = peerIP;
    this.peerAS = peerAS;
  }

  public AS getPeerAS() {
    return this.peerAS;
  }
  public InetAddress getPeer() {
    return this.peerIP;
  }

  public String toString() {
    String peerString = MRTConstants.ipAddressString(this.peerIP, false);

    StringBuilder result = new StringBuilder("NOTIFICATION").append('|')
            .append(getTime()).append('|')
            // this comes from MRTRecord
            .append(peerString).append('|')
            .append(this.peerAS);

    return result.toString();
  }
}
