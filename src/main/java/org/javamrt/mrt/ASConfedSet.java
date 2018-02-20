// This file is part of java-mrt
// A library to parse MRT files

// This file is released under LGPL 3.0
// http://www.gnu.org/licenses/lgpl-3.0-standalone.html

package org.javamrt.mrt;

import java.util.LinkedList;

public class ASConfedSet extends AS {

	private LinkedList<AS> asList;

	public ASConfedSet(LinkedList<AS> asList) {
		this.asList = new LinkedList<AS>();
		this.asList.addAll(asList);
	}


	public String toString() {
		String result = "{";
		for (AS as:asList)
			result = result.concat(" "+as.toString());
		return result.concat(" }");
	}
}

