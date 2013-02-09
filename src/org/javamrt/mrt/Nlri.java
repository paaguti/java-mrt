package org.javamrt.mrt;

import org.javamrt.utils.RecordAccess;

//IPv4/IPv6 address and mask length extractor from NLRI field in BGP

public class Nlri
    extends Prefix
{
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
    if (afi==MRTConstants.AFI_IPv4)
	this.base=new byte[4];			//deciding the type or address
    else if (afi==MRTConstants.AFI_IPv6)
	this.base=new byte[16];
    else
	throw new Exception("NLRI: unknown Address Family: "+afi);
    this.maskLength=RecordAccess.getU8(record, offset); //reading length byte (bits)
    offset++;
    this.bytes = 0;
    if (this.maskLength > 0)
	{
	    this.bytes=(this.maskLength-1)/8+1; //converting bits into bytes and deciding number of bytes to be read
	}
    
    int i=0;
    
    while (i < bytes)
      this.base[i++] = (byte)RecordAccess.getU8(record,offset++); //extracting byte by byte of prefix field
    //and adding to address array	
    while (i < this.base.length) //filling up with zeros to complete the length of IPv4/v6 address
      this.base[i++] = 0;

    setPrefix(this.base,this.maskLength);
  }
	

  public Prefix toPrefix()
  {
    return (Prefix)this;
  }
  
  public int getOffset() //returning the record offset
  {
    return this.bytes+1;
  }
  
  private int bytes;
}
