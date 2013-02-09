package org.javamrt.utils;

import java.io.PrintStream;

public class Debug {

	public static final boolean compileDebug = false;
	
	public static boolean doDebug = false;
	
	public static PrintStream debugStream = System.err;
	
	public static void setDebug(boolean debug) {
		doDebug = debug;
	}
	
	public static void debug(String fmt, Object... objects) {
		if (doDebug == false)
			return;
		debugStream.printf(fmt, objects);
	}
	
	public static void printf(String fmt, Object...objects ) {
		debug(fmt,objects);
	}
	
	public static void println(String s) {
		if (doDebug == false)
			return;
		debugStream.println(s);
	}

	public static void print(String s) {
		if (doDebug == false)
			return;
		debugStream.print(s);
	}


	public static void dump(byte[] record) {
		if (doDebug == false)
			return;
		RecordAccess.dump(debugStream, record);
	}
}