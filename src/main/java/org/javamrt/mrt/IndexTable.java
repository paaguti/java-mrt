// This file is part of java-mrt
// A library to parse MRT files

// This file is released under LGPL 3.0
// http://www.gnu.org/licenses/lgpl-3.0-standalone.html

package org.javamrt.mrt;

import org.javamrt.progs.route_btoa;
import org.javamrt.utils.RecordAccess;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Vector;



public class IndexTable
{

  IndexTable (byte[]record)
  {
    this.record = record;
    //this.header=header;
    int offset = 0;
    /* TODO
    long collectorId = RecordAccess.getU32 (this.record, offset);
    byte[] collectorIdbyte = RecordAccess.getBytes (this.record, offset, 4);
    /*
      try {
        route_btoa.System_err_println(InetAddress.getByAddress(collectorIdbyte).getHostAddress());
      } catch (UnknownHostException e) {
        route_btoa.printStackTrace(e);
      }
     */
    offset += 4;
    int nameLength = RecordAccess.getU16 (this.record, offset);
    //route_btoa.System_err_println(nameLength);
    offset += 2;
    // byte[] viewName = null;
    if (nameLength > 0)
      {
	// viewName = RecordAccess.getBytes (this.record, offset, nameLength);
	offset += nameLength;
      }
    peerCount = RecordAccess.getU16 (this.record, offset);
    offset += 2;
    /*
      route_btoa.System_err_println("\nRecord: "+this.record.length+"\nCollectorId: "+collectorId
      +"\nnameLength: "+nameLength+"\nfirstMask(byte): "+
      "\npeerCount: "+peerCount);
    */
    peerIp = new Vector < InetAddress > (peerCount);
    for (int i = 0; i < peerCount; i++)
      peerIp.addElement (null);
    peerBgp = new long[peerCount];
    peerAs = new long[peerCount];
    for (int i = 0; i < peerCount; i++)
      {
	int bit0 = 1;
	int bit1 = 2;
	//int peerCount=RecordAccess.getU16(this.record, offset);
	int peerType = RecordAccess.getU8 (this.record, offset);
	offset++;
	int firstBit;
	int secondBit;
	//byte[] peerCount2=RecordAccess.getBytes(this.record, offset, 1);
	//route_btoa.System_err_println("Bit 1: "+peerType);
	if ((peerType & bit0) != 0)
	  {
	    //route_btoa.System_err_println("upit (peertype&bit0) tacan!");
	    firstBit = 16;
	  }
	else
	  firstBit = 4;
	if ((peerType & bit1) != 0)
	  {
	    secondBit = 4;
	    //route_btoa.System_err_println("upit (peertype&bit1) tacan!");
	  }
	else
	  secondBit = 2;
	//route_btoa.System_err_println("FirstBit: "+firstBit+"\nSecond Bit: "+secondBit+"\nOFFSET: "+offset);
	long peerBgpId = RecordAccess.getU32 (this.record, offset);
	//route_btoa.System_err_println("peerBgpId: "+peerBgpId+" numero i: "+i);

	peerBgp[i] = peerBgpId;
	//route_btoa.System_err_println("peerBgpId: "+peerBgpId);
	offset += 4;
	//route_btoa.System_err_println("firstBIt: "+firstBit+" offset: "+offset+" recordlen: "+this.record.length);

	try
	{
	  takeIp =
	    InetAddress.getByAddress (RecordAccess.
				      getBytes (this.record, offset,
						firstBit));
	} catch (UnknownHostException e1)
	{
	  route_btoa.printStackTrace(e1);
	}
	peerIp.set (i, takeIp);
	/*
	   try{
	   route_btoa.System_err_println("atresa1 je: "+InetAddress.getByAddress(RecordAccess.getBytes(this.record, offset, firstBit)).getHostAddress());
	   }
	   catch(UnknownHostException e){}
	 */
	offset += firstBit;
	long takePeerAs;
	if (secondBit == 2)
	  takePeerAs = RecordAccess.getU16 (this.record, offset);
	else
	  takePeerAs = RecordAccess.getU32 (this.record, offset);
	peerAs[i] = takePeerAs;
	//route_btoa.System_err_println("PeerAs je: "+RecordAccess.getBytes(this.record,offset, secondBit));
	offset += secondBit;
      }
    //for (int i=0;i<peerCount;i++) route_btoa.System_err_println(peerBgp[i]);
  }
  public long[] getPeerBgp ()
  {
    return peerBgp;
  }
  public long[] getPeerAs ()
  {
    return peerAs;
  }
  public Vector < InetAddress > getPeerIp ()
  {
    return peerIp;
  }
  public byte[] getViewName ()
  {
    return viewName;
  }
  private byte[] viewName;
  private byte[] record;
//private byte [] header;
  private long[] peerBgp;
  private long[] peerAs;
  private Vector < InetAddress > peerIp;
  private InetAddress takeIp;
  private int peerCount;
}
