package org.javamrt.mrt; 

public class Notification
  extends MRTRecord
{
  public Notification (byte[]header, byte[]record)
  {
    super(header);
  }
  
  public String toString ()
  {
    return "NOTIFICATION";
  }
}
