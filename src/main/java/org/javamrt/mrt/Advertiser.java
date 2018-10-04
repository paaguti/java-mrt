// This file is part of java-mrt
// A library to parse MRT files

// This file is released under LGPL 3.0
// http://www.gnu.org/licenses/lgpl-3.0-standalone.html

package org.javamrt.mrt;

import org.javamrt.utils.RecordAccess;

public class Advertiser
    implements Attribute
{
  public Advertiser (byte[] buffer)
  {
    version   = RecordAccess.getU8 (buffer, 0);
    clusterId = RecordAccess.getU16 (buffer, 1);
  }

  public String toString ()
  {
    return String.format("%d:%d", version, clusterId);
  }

  public int getVersion()
  {
    return version;
  }

  public int getClusterId()
  {
    return clusterId;
  }

  public boolean equals(Object o) {
	  if (o == null) return false;
	  if (o == this) return true;
	  if (o instanceof Advertiser) {
		  Advertiser a = (Advertiser)o;
		  return a.version == this.version && a.clusterId == this.clusterId;
	  }
	  return false;
  }
  protected int version;
  protected int clusterId;
}
