// This file is part of java-mrt
// A library to parse MRT files

// This file is released under LGPL 3.0
// http://www.gnu.org/licenses/lgpl-3.0-standalone.html

package org.javamrt.mrt;
import java.net.InetAddress;

public class StateChange
  extends MRTRecord
{

    public StateChange(
            long time,
            InetAddress gatewayIP,
            AS gatewayAS,
            int old_state,
            int new_state,
            String updateStr,
            byte[] header, byte[] record)
    {
        super(header, record);
      // route_btoa.System_err_println("State Change");
      this.time = time;
      this.gatewayIP = gatewayIP;
      this.gatewayAS = gatewayAS;
      this.old_state = old_state;
      this.new_state = new_state;
      this.updateStr = updateStr;
    }

    private long time;
    public long  getTime()
    {
	return this.time;
    }

    private InetAddress gatewayIP;

    public InetAddress getPeer()
    {
        return this.gatewayIP;
    }

    @Deprecated
    public InetAddress 	getPeerIP()
    {
	return this.gatewayIP;
    }

    private AS gatewayAS;
    public AS  getPeerAS()
    {
	return this.gatewayAS;
    }

    private int old_state;
    public int 	getOldState()
    {
	return this.old_state;
    }

    private int new_state;
    public int 	getNewState()
    {
	return this.new_state;
    }

    private String updateStr = "";

    public String toString()
    {
 	return String.format("%s|%d|STATE|%s|%s|%d|%d",
                 this.updateStr,
			     this.time,
			     MRTConstants.ipAddressString(gatewayIP),
			     this.gatewayAS.toString(),
			     this.old_state,
			     this.new_state);
    }
}
