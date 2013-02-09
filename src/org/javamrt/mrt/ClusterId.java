// This file is part of java-mrt
// A library to parse MRT files

// This file is released under LGPL 3.0
// http://www.gnu.org/licenses/lgpl-3.0-standalone.html

package org.javamrt.mrt;

import org.javamrt.utils.RecordAccess;

public class ClusterId
    implements Attribute
{
  //
  // TODO: recheck this
  //
  public ClusterId (byte[]buffer)
  {

    rcid = new int[buffer.length/2];

    for (int i = 0; i < rcid.length; i++)
      {
	rcid[i] = RecordAccess.getU16 (buffer, i*2);
      }
    clusterId = null;
  }

  public String toString ()
  {
    if (clusterId != null)
      return clusterId;

    clusterId = String.format("%d",rcid[0]);
    for (int i = 1; i < rcid.length; i++)
      {
	clusterId = clusterId.concat(String.format(":%d",rcid[i]));
      }
    return clusterId;
  }

  public boolean equals (ClusterId other) {
	  if (this.rcid.length != other.rcid.length)
		  return false;
	  for (int i=0;i<this.rcid.length;i++)
		  if (this.rcid[i] != other.rcid[i])
			  	return false;
	  return true;
  }

  public boolean equals (Object o) {
	  if (o == null)
		  return false;
	  if (this == o)
		  return true;
	  if (o instanceof ClusterId)
		  return equals((ClusterId)o);
	  return false;
  }
  protected int[] rcid;
  private String clusterId;
}
