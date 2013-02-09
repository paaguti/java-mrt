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
