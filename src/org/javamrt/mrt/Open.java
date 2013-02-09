// This file is part of java-mrt
// A library to parse MRT files

// This file is released under LGPL 3.0
// http://www.gnu.org/licenses/lgpl-3.0-standalone.html

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
