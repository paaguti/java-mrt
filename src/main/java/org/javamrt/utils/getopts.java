// This file is part of java-mrt
// A library to parse MRT files

// This file is released under LGPL 3.0
// http://www.gnu.org/licenses/lgpl-3.0-standalone.html

package org.javamrt.utils;

import java.io.PrintStream;

/**
 * Parse command line options the **IX way<br>
 * version 1.00 Only treats short options<br>
 * version 2.00 Includes an interface for short and long options
 * @author paag
 * @version 2.00
 */
public class getopts {
	/**
	 * next option to process
	 */
	public int optind = 0;
	/**
	 * current optional argument
	 */
	public String optarg = null;
	public String opterr;
	public String optopt = null;

	private String[] args;
	private LongOpt[] options;

	/**
	 * create a new class to process cmd line arguments<br><b>Sample code:</b><br>
	 * <code>
	 * 	getopt test = new getopts(args,<br>
&nbsp;&nbsp;new LongOpt('a',"arg",true);<br>
&nbsp;&nbsp;new LongOpt('b',"beg",false);<br>
&nbsp;&nbsp;new LongOpt('c',null,false));</code>
	 * @param args
	 * @param options
	 */
	public getopts(String[] args,LongOpt... options){
		this.args = args;
		this.options = options;
		this.optind  = 0;
	}

	/**
	 * create a command-line parser for the 'old' short options
	 * getopts options = new getopts(args,"abc:d:E");
	 */
	public getopts(String[] args, String optString) {
		this.optind = 0;
		this.args=args;

		char optChars[] = optString.toCharArray();
		int optCount = optChars.length;
		for (int pos = 0;pos < optChars.length;pos++) {
			if (optChars[pos] == ':') optCount--;
		}
		this.options = new LongOpt[optCount];

		optCount = 0;
		for (int pos = 0;pos < optChars.length;pos++) {
			if (pos != optChars.length-1 && optChars[pos+1] == ':') {
				this.options[optCount++] = new LongOpt(optChars[pos],null,true);
				pos++;
			} else {
				this.options[optCount++] = new LongOpt(optChars[pos],null,false);
			}
		}
	}
	/**
	 * try to process next option<br>
	 * @return <b>0</b> if no more options<br>
	 * <b> 255 </b> for missing argument<br>
	 * <b>'?'</b>  if option unknown... set opterr with unknown option<br>
	 * <b>shortOpt</b>  if option found... set optarg to optional argument
	 */

	// return 255 on error: missing argument
	// return '?' : unknown flag

	// TODO return codes < '0'

	private char nextShortOption(String currentArg) {
		this.opterr = null;
		for (int i=0;i<options.length;i++) {
			if (options[i].shortOpt < '0')
				continue;
			if (currentArg.charAt(1) == options[i].shortOpt) {
				this.optopt = String.format("%c",options[i].shortOpt);
				this.optind++;
				if (options[i].hasArg) {
					if (this.optind == args.length) {
						this.opterr = currentArg.substring(1);
						return 255;
					}
					this.optarg = this.args[this.optind++];
				}
				return options[i].shortOpt;
			}
		}
		this.opterr = currentArg.substring(1);
		return '?';
	}

	private char nextLongOption(String currentArg) {
		char result = '?';
		for (int i=0;i<options.length;i++) {
			if (options[i].longOpt == null)
				continue;
			String matcher = "^--"+options[i].longOpt+"(=.+){0,1}$";
			boolean matched = currentArg.matches(matcher);
			if (matched == false)
				continue;
			this.optopt = "--"+options[i].longOpt;
			result=options[i].shortOpt;
			if (options[i].hasArg == true) {
				matcher = "--"+options[i].longOpt + "=";
				matched = currentArg.startsWith(matcher);
				if (matched) {
					this.optarg=currentArg.replaceFirst(matcher,"");
				} else {
					this.opterr = options[i].longOpt;
					this.optarg = null;
					result = 255;
				}
			}
			optind ++;
			break;
		}
		return result;
	}

	public char nextOption() {
		this.optarg = null;
		this.opterr = null;

		if (this.optind == this.args.length)
			return 0;

		String currentArg = this.args[this.optind];

		if (currentArg.matches("-[a-zA-Z0-9]"))
			return nextShortOption(currentArg);
		if (currentArg.matches("--[0-9A-Za-z].+"))
			return nextLongOption(currentArg);
		return 0;
	}

/**
 * for convenience: try to parse optarg into an integer
 * @return
 */
	public int optarg() {
		return Integer.parseInt(this.optarg);
	}


	/**
	 * Demo code
	 */
	public static String arg = null;

	public static void main(String[] args) {
		for (int i=0; i<args.length; i++)
			System.out.print(args[i]+" ");
		System.out.println();
		getopts test = new getopts(args,
				new LongOpt('a',"arg", true, "<arg>: con argumento <arg>"),
				new LongOpt('b',"beg", false,"     : sin argumento"),
				new LongOpt('c',null, false, "     : sin largo"));
		char opcion;

		while ((opcion = test.nextOption()) > 0) {
			switch (opcion){
			case 'a':
				System.out.println("option ["+opcion+"]("+test.optopt+") argument: "+test.optarg);
				break;
			case 'b':
				System.out.println("short and long without option: ["+opcion+"]"+test.optopt);
				break;
			case 'c':
				System.out.println("Special short only option ["+opcion+"]"+test.optopt);
				break;
			case 255:
				System.out.println("Missing argument for "+test.opterr);
				System.exit(1);
			default:
				System.err.println("Error processing "+args[test.optind]);
				System.exit(1);
			}
		}
		for (int arg=test.optind; arg<args.length;arg++) {
			System.out.println("remaining "+args[arg]);
		}
		System.out.println();
		test.printHelp(System.out);
	}

	public void printHelp(PrintStream out) {
		int maxLong = 0;
		for (LongOpt lopt:this.options)
			try {
				maxLong = Math.max(maxLong,lopt.longOpt.length());
			} catch (Exception e) {
				;
			}
		String longFormat = null;
		if (maxLong != 0)
			longFormat = String.format("--%%-%ds ",maxLong);
		for (LongOpt lopt:this.options) {
			if (null != lopt.help) {
				out.printf("  -%c ",lopt.shortOpt);
				if (null != longFormat) {
					if (null != lopt.longOpt)
						out.printf(longFormat,lopt.longOpt);
					else
						out.printf(String.format("%%%ds",maxLong+3),"");
				}
				out.println(lopt.help);
			}
		}
	}
}
