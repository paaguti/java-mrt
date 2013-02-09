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
	long 		time,
	InetAddress 	gatewayIP,
	AS          	gatewayAS,
	int 		old_state,
	int 		new_state)
    {
      // System.out.println("State Change");
      this.time = time;
      this.gatewayIP = gatewayIP;
      this.gatewayAS = gatewayAS;
      this.old_state = old_state;
      this.new_state = new_state;
    }

    private long time;
    public long  getTime()
    {
	return this.time;
    }

    private InetAddress gatewayIP;
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


    public String toString()
    {
	return String.format("BGP4MP|%d|STATE|%s|%s|%d|%d",
			     this.time,
			     this.gatewayIP.getHostAddress(),
			     this.gatewayAS.toString(),
			     this.old_state,
			     this.new_state).replaceAll("(:0)+:0{0,1}","::");

    }
}
