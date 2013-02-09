package org.javamrt.mrt; 
import java.net.InetAddress;

public class Advertisement
  extends Bgp4Update
{

public
    Advertisement(byte[]      header,
		  InetAddress peerIP,
		  AS          peerAS,
		  Prefix      prefix,
		  Attributes  updateAttr)
  {
    super(header,
	  peerIP,
	  peerAS,
	  prefix,
	  updateAttr);
    this.updateType = 'A';
    //
    //    this.updateStr  = updateStr;
    //
  }

@Override
  public boolean isIPv4()
  {
    return this.prefix.isIPv4();
  }
@Override
  public boolean isIPv6()
  {
    return this.prefix.isIPv6();
  }

  public String getCommunity()
  {
    Community comm = this.updateAttr.getCommunity();
    return comm.toString();
  }
  
  public long getMed()
  {
    Med result = this.updateAttr.getMed();
    
    return result.getMed();
  }

  /**
   * overrides {@link Bgp4Update#toAdvertisement()}
   */
  @Override
	public Advertisement toAdvertisement() {
		return this;
	}
}
