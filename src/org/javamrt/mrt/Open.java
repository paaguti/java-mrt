package org.javamrt.mrt; 

import org.javamrt.utils.Debug;

public class Open
  extends MRTRecord
{
  Open (byte[]header, byte[]record)
  {
    super (header);

    if (Debug.compileDebug) {
    	Debug.println("Open message");
    	Debug.dump(header);
    	Debug.dump(record);
    }
      
  }
  
  public String toString ()
  {
    return "OPEN";
  }
}
