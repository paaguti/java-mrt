// This file is part of java-mrt
// A library to parse MRT files

// This file is released under LGPL 3.0
// http://www.gnu.org/licenses/lgpl-3.0-standalone.html

package org.javamrt.mrt;

import java.net.InetAddress;

import org.javamrt.utils.RecordAccess;

//IPv4/IPv6 address and mask length extractor from NLRI field in BGP

public class Nlri extends Prefix
{
	// from Prefix.java
	//   byte[] addr;
	//   int    maskLength;
	private int bytes;
	protected long pathId = -1;

	public Nlri (byte[]record, int offset, int afi, boolean addPath) throws Exception
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
		if (addPath) {
			pathId = RecordAccess.getU32(record, offset);
			offset = offset + 4;
		}
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
	
	public String toString() {
		String string = super.toString();
		if (pathId != -1) string.concat(":").concat(Long.toString(pathId));
		return string;
	}

	public int getOffset() //returning the record offset
	{
		int offset = 0;
		if (pathId != -1) offset = 4; 
		offset = this.bytes + 1;
		return offset;
	}
}
