// This file is part of java-mrt
// A library to parse MRT files

// This file is released under LGPL 3.0
// http://www.gnu.org/licenses/lgpl-3.0-standalone.html

package org.javamrt.mrt;

import java.util.LinkedList;

// import org.javamrt.utils.RecordAccess;

public class ASSet extends AS
{
	protected LinkedList<AS> asSet;

	public ASSet(LinkedList<AS> asList) {
		this.asSet = new LinkedList<AS>();
		this.asSet.addAll(asList);
	}

	public boolean equals(Object other) {
		if (other == null)
			return false;
		if (this == other)
			return true;
		if (other instanceof ASSet)
			return equals((ASSet) other);
		return false;
	}

	private boolean equals(ASSet other) {
		return this.asSet.equals(other.asSet);
	}

	public LinkedList<AS> getASList() {
		return this.asSet;
	}

	public int asSetSize() {
		return this.asSet.size();
	}

	public boolean is4Byte() {
		int max = this.asSetSize();
		for (int i=0;i<max;i++)
			if (this.getAS(i).is4Byte())
				return true;
		return false;
	}

	public AS getAS(int i) {
		return this.asSet.get(i);
	}

	public String toString() {
		String result = "(".concat(this.asSet.get(0).toString());
		for (int i = 1; i < asSet.size(); i++)
			result = result.concat(" ").concat(this.asSet.get(i).toString());
		return result.concat(")");
	}

}
