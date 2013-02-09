// This file is part of java-mrt
// A library to parse MRT files

// This file is released under LGPL 3.0
// http://www.gnu.org/licenses/lgpl-3.0-standalone.html

package org.javamrt.mrt;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.Date;

import org.javamrt.utils.RecordAccess;


public class TableDump
  extends MRTRecord
{

protected TableDump(int           view,
	    int           sequence,
	    Prefix        prefix,
	    long          origTime,
	    InetAddress   peer,
	    AS            peerAs,
	    Attributes    attributes)
  {
    if (false)
    System.out.printf("TableDump(int    view     =%d\n"+
		      "   int           sequence = %d\n"+
		      "   Prefix        prefix   = %s\n"+
		      "   long          origTime = %d\n"+
		      "   InetAddress   peer     = %s\n"+
		      "   AS            peerAs   = %s\n"+
		      "   Attributes    attributes = %s);\n",
		      view,
		      sequence,
		      prefix,
		      origTime,
		      peer,
		      peerAs,
		      attributes);

    this.view       = view;
    this.sequence   = sequence;
    this.prefix     = prefix;
    this.origTime   = origTime;
    this.Peer       = peer;
    this.PeerAS     = peerAs;
    this.attributes = attributes;
    //this.OrigTime = new Date (this.origTime * 1000L);

    setAsPath();
  }

  TableDump (byte[]cabecera, byte[]record,int subtype)
    throws Exception
  {
    super(cabecera);

    int decodeOffset = 0;
    int addrSize     = (subtype == MRTConstants.AFI_IPv4) ? 4 : 16;

    view = RecordAccess.getU16 (record, decodeOffset);
    decodeOffset += 2;
    sequence = RecordAccess.getU16 (record, decodeOffset);
    decodeOffset += 2;

    byte[] base = RecordAccess.getBytes (record, decodeOffset, addrSize);
    decodeOffset += addrSize;
    this.prefix =
      new Prefix (base, RecordAccess.getU8 (record, decodeOffset));
    decodeOffset++;
    decodeOffset++;		// skip status
    this.origTime = RecordAccess.getU32 (record, decodeOffset);
    decodeOffset += 4;
    this.Peer =
      InetAddress.getByAddress (RecordAccess.
				 getBytes (record, decodeOffset, addrSize));
    decodeOffset += addrSize;
    this.PeerAS = new AS(RecordAccess.getU16 (record, decodeOffset));
    decodeOffset += 2;
    int attrLength = RecordAccess.getU16 (record, decodeOffset);
    decodeOffset += 2;
    this.attributes =
      new Attributes (record, attrLength, decodeOffset);

    setAsPath();
  }

  private void setAsPath()
  {
    try {
    	asPath =
    		(ASPath)attributes.getAttribute(MRTConstants.ATTRIBUTE_AS_PATH);
     } catch (Exception e) {
    	 asPath = null;
     }
  }

  public InetAddress getPeer ()
  {
    return this.Peer;
  }

  public AS getPeerAS ()
  {
    return this.PeerAS;
  }
  public Prefix getPrefix ()
  {
    return this.prefix;
  }
  public Date getOrigTime ()
  {
    return new Date (this.origTime * 1000L);
  }

  public long getTime()
  {
	  return this.origTime;
  }
  public String toString ()
  {

    if (dumpString != null)
      return dumpString.toString ();
    String cleanPeer = // this.Peer.getHostAddress ().replaceFirst("(:0){2,7}", ":").replaceFirst("^0::", "::");
    	MRTConstants.ipAddressString(this.Peer);
    dumpString =
      new StringBuffer (this.type+"|" + this.origTime);
    dumpString.append ("|B|" + cleanPeer  +"|");
    if (this.PeerAS != null)
      dumpString.append (this.PeerAS.toString ());
    dumpString.append ("|" + this.prefix.toString ());
    dumpString.append ("|" + this.attributes.toString());
    return dumpString.toString ();
  }

  public boolean matches (InetAddress addr)
  {
    return this.prefix.matches (addr);
  }

  public boolean hasAsPathPrepend ()
  {
    if (this.asPath == null)
      return false;
    return this.asPath.hasAsPathPrepend();
  }

  public ASPath getASPath()
  {
    return this.asPath;
  }

  public boolean isIPv4()
  {
    return prefix.getBaseAddress() instanceof Inet4Address;
  }

  public boolean isIPv6()
  {
    return prefix.getBaseAddress() instanceof Inet6Address;
  }

  @Override
  public TableDump toTableDump() {
	  return this;
  }

  protected Prefix      prefix;
  protected InetAddress Peer      = null;
  protected AS          PeerAS    = null;
  protected long        origTime = 0;

  protected StringBuffer dumpString = null;

  protected Attributes attributes;
  protected ASPath asPath;

  protected int view;
  protected int sequence;

  protected String type="TABLE_DUMP";
}
