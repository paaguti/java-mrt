package org.javamrt.mrt;

import java.util.LinkedList;

public class ASConfedSequence
	extends AS
{
	LinkedList<AS> asList;
	
	public ASConfedSequence(LinkedList<AS> asList) {
		this.asList = new LinkedList<AS>();
		this.asList.addAll(asList);
	}

	public String toString() {
		String result = "[";
		for (AS as:asList)
			result = result.concat(" "+as.toString());
		return result.concat(" ]");
	}
}
