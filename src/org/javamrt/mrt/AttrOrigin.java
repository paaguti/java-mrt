// This file is part of java-mrt
// A library to parse MRT files

// This file is released under LGPL 3.0
// http://www.gnu.org/licenses/lgpl-3.0-standalone.html

package org.javamrt.mrt;

import org.javamrt.utils.RecordAccess;


public class AttrOrigin implements Attribute {

	public AttrOrigin(byte[] buffer) {
		this.attrOrigin = RecordAccess.getU8(buffer, 0);

	}

	public int getAttrOrigin() {
		return attrOrigin;
	}

	public String toString() {
		switch (this.attrOrigin) {
		case originIBGP:
			return "IGP";
		case originEBGP:
			return "EGP";
		default:
		}
		return "INCOMPLETE";
	}

	  public boolean equals(Object o) {
		  if (o == null) return false;
		  if (o == this) return true;
		  if (o instanceof AttrOrigin)
			  return ((AttrOrigin)o).attrOrigin == this.attrOrigin;
		  return false;
	  }
	protected int attrOrigin;
	private static final int originIBGP = 0;
	private static final int originEBGP = 1;
}
