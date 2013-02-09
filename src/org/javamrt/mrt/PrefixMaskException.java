package org.javamrt.mrt;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class PrefixMaskException
  extends java.lang.Exception
{
	String description;
	/*
    public PrefixMaskException(InetAddress ia, int mask)
    {
    	super();	
    	description = String.format("Mask length (%d) unfeasible for address %s",mask,ia.getHostAddress());
    }
    */
	
    public PrefixMaskException(byte[] addr, int mask) 
    {
    	super();
        try {
			description = String.format("Mask length (%d) unfeasible for address %s",mask,InetAddress.getByAddress(addr).getHostAddress());
		} catch (UnknownHostException e) {
			description = "Impossible IP address [";
			for (int i=0;i<addr.length;i++)
				description+=String.format("%02x%c",addr[i],(i==addr.length-1) ? ']':'.');
		}    		
	}

    public String toString() {
    	return new String(description);
    }
    
	private static final long serialVersionUID = 1L;
}
