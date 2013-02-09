package org.javamrt.mrt; 

public class KeepAlive extends MRTRecord
{
  KeepAlive (byte[]header, byte[]record)
  {
    super (header);
  }
  
  public String toString ()
  {
    return "KEEP_ALIVE";
  }
}
