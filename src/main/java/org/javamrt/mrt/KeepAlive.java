// This file is part of java-mrt
// A library to parse MRT files

// This file is released under LGPL 3.0
// http://www.gnu.org/licenses/lgpl-3.0-standalone.html

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
