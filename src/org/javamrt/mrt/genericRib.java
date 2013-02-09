package org.javamrt.mrt; 
/*
// TODO: everything
import java.util.Vector;
import org.javamrt.utils.RecordAccess;
*/

public class genericRib extends MRTRecord
{
  public genericRib ()
  {
    super ();

    /*
    int offset = 0;
    long sequenceNo = RecordAccess.getU32 (this.record, offset);
      offset += 4;
    int afi = RecordAccess.getU16 (this.record, offset);
      offset += 2;
    int safi = RecordAccess.getU8 (this.record, offset);

      try
    {
      nlri = new Nlri (this.record, offset, afi);
    } catch (UnknownHostException e)
    {
      e.printStackTrace ();
    }
    offset += nlri.getOffset ();
    int entryCount = RecordAccess.getU16 (this.record, offset);
    / *
       try {
       stream=new BufferedInputStream(new FileInputStream("temp.xxx"));
       } catch (FileNotFoundException e1) {
       e1.printStackTrace();
       }
       try {
       body=new byte [stream.available()];
       int read=stream.read(body, 0, stream.available());
       } catch (IOException e) {
       e.printStackTrace();
       }
       IndexTable test=new IndexTable(body);
     * /
    for (int i = 0; i < entryCount; i++)
      {
	int peerIndex = RecordAccess.getU16 (this.record, offset);

	offset += 2;
	long time = RecordAccess.getU32 (this.record, offset);
	offset += 4;
	int attrLen = RecordAccess.getU16 (this.record, offset);
	offset += 2;
	GetAttributes bgp4Attributes =
	  new GetAttributes (record, attrLen, offset);

	Vector attributes = bgp4Attributes.getAttributes ();
	StringBuffer attr = new StringBuffer ();
	for (int j = 0; j < attributes.size (); j++)
	  {
	    if (attributes.elementAt (j) != null)
	      attr.append (attributes.elementAt (j).toString ());
	  }
	offset += attrLen;
	out.append ("TABLE_DUMP_V2|" + time + "|" +
		    address.elementAt (peerIndex).getHostAddress () + "|" +
		    peerAs[peerIndex] + "|" + attr.toString () +
		    nlri.getAddress () + "/" + nlri.getMaskLen () + "\n");
      }
    */
  }
  public String toString ()
  {
    return "TODO: genericRIB.toString()";
  }

  // private byte [] body;
  // private BufferedInputStream stream;
  // private final int RIB_IP4_AFI=1;
  // private final int RIB_IP6_AFI=2;
  // private Nlri nlri;
  // private byte[] header;
  // private byte[] record;
}
