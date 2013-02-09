package org.javamrt.mrt; 
import java.net.InetAddress;
/**
 * This class extends TableDump just to signal that 
 * it is a TableDumpv2 in the toString(). The rest
 * is common.
 * 
 * @see org.javamrt.mrt.TableDump
 * @author paag
 */
public class TableDumpv2
  extends TableDump
	  
{
  protected TableDumpv2(int           view,
	      int           sequence,
	      Prefix        prefix,
	      long          origTime,
	      InetAddress   peer,
	      AS            peerAs,
	      Attributes    attributes)
  {
    super(view,
	  sequence,
	  prefix,
	  origTime,
	  peer,
	  peerAs,
	  attributes);
    this.type = "TABLE_DUMP2";
  }
  
  TableDumpv2 (byte[]cabecera, byte[]record,int subtype)
		    throws Exception {
	  super(cabecera, record, subtype);
	  this.type = "TABLE_DUMP2";
  }
  
  public TableDumpv2 toTableDumpv2() {
	  return this;
  }
}

