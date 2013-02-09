// This file is part of java-mrt
// A library to parse MRT files

// This file is released under LGPL 3.0
// http://www.gnu.org/licenses/lgpl-3.0-standalone.html

package org.javamrt.utils;

public class LongOpt {

	public char shortOpt;
	public boolean hasArg;
	public String longOpt;
	public String help = null;

	public LongOpt(char shortOpt,String longOpt,boolean hasArg) {
		this.shortOpt  = shortOpt;
		this.longOpt   = longOpt;
		this.hasArg    = hasArg;
	}

	public LongOpt(char shortOpt,String longOpt,boolean hasArg,String help) {
		this.shortOpt  = shortOpt;
		this.longOpt   = longOpt;
		this.hasArg    = hasArg;
		this.help      = help;
	}


}
