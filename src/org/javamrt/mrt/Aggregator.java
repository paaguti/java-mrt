package org.javamrt.mrt; 
import java.net.InetAddress;

import org.javamrt.utils.RecordAccess;


public class Aggregator
    implements Attribute
{
    protected int asSize = 2;
    
    protected Aggregator() // for As4Aggregator
    {
    }
    
    public Aggregator (byte[] buffer)
	throws Exception
    {
	this.asSize = 2;
	decode(buffer);
    }

    public Aggregator (byte[] buffer,int size)
	throws Exception
    {
	this.asSize = size;
	decode(buffer);
    }
    
    private void decode (byte[] buffer)
	throws Exception
    {
	int len = buffer.length;
	
	if ((len-asSize != 4) && (len-asSize != 16))
	    throw new Exception (String.format("Aggregator with %d bytes (must be %d or %d)",
					       len,4+asSize,16+asSize));
	
	aggregatorAS = new AS(RecordAccess.getBytes (buffer, 0,asSize));
	byte[] aip = RecordAccess.getBytes (buffer, asSize, len - asSize);
	aggregatorIP = InetAddress.getByAddress (aip);
    }
  
    public String toString ()
    {
	return aggregatorAS + " " + aggregatorIP.getHostAddress ();
    }
    
    public AS getAs()
  {
      return aggregatorAS;
  }
    
    public InetAddress getIP()
    {
	return aggregatorIP;
    }
    
    public boolean equals(Object o) {
    	if (o == null) return false;
    	if (o == this) return true;
    	if (o instanceof Aggregator) {
    		Aggregator a = (Aggregator)o;
    		return this.aggregatorAS.equals(a.aggregatorAS) &&
    			this.aggregatorIP.equals(a.aggregatorIP);
    	}
    	return false;
    }
    protected AS          aggregatorAS;
    protected InetAddress aggregatorIP;
}
