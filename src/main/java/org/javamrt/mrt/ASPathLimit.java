// This file is part of java-mrt
// A library to parse MRT files

// This file is released under LGPL 3.0
// http://www.gnu.org/licenses/lgpl-3.0-standalone.html

package org.javamrt.mrt;

import org.javamrt.utils.RecordAccess;


public class ASPathLimit
    implements Attribute
{
	/**
	 *

http://tools.ietf.org/html/draft-ietf-idr-as-pathlimit-03

Network Working Group                                         T. Li, Ed.
Internet-Draft                                       Cisco Systems, Inc.
Intended status: Standards Track                        R. Fernando, Ed.
Expires: July 8, 2007                             Juniper Networks, Inc.
                                                           J. Abley, Ed.
                                                                 Afilias
                                                         January 4, 2007



The AS_PATHLIMIT Path Attribute


draft-ietf-idr-as-pathlimit-03


5. The AS_PATHLIMIT Attribute


   The AS_PATHLIMIT attribute is a transitive optional BGP path
   attribute, with Type Code 21.  The AS_PATHLIMIT attribute has a fixed
   length of 5 octets.  The first octet is an unsigned number that is
   the upper bound on the number of ASes in the AS_PATH attribute of the
   associated paths.  One octet suffices because the TTL field of the IP
   header ensures that only one octet's worth of ASes can ever be
   traversed.  The second thru fifth octets are the AS number of the AS
   that attached the AS_PATHLIMIT attribute to the NLRI.

	 */

    public ASPathLimit (byte[] buffer)
	throws Exception
    {
    	decode(buffer);
    }

    private void decode (byte[] buffer)
	throws Exception
    {
    	int len = buffer.length;
    	if (len != 5)
    		throw new AttributeException("AS_PATH Limit attribute length (%d != 5)",len);
    	this.limit = RecordAccess.getU8(buffer, 0);
    	this.origAS = new AS(RecordAccess.getBytes(buffer, 1, 4));
    }

    public String toString ()
    {
	return String.format("ASPath_LIMIT %d by %s",this.limit,this.origAS);
    }

    public int getLimit()
    {
      return this.limit;
    }

    public AS getLimitAS()
    {
    	return this.origAS;
    }

    public boolean equals(Object o) {
    	if (o == null) return false;
    	if (o == this) return true;
    	if (o instanceof ASPathLimit) {
    		ASPathLimit a = (ASPathLimit)o;
    		return this.limit == a.limit && this.origAS.equals(a.origAS);
    	}
    	return false;
    }
    protected int limit = 0;
	protected AS origAS;

}
