// This file is part of java-mrt
// A library to parse MRT files

// This file is released under LGPL 3.0
// http://www.gnu.org/licenses/lgpl-3.0-standalone.html

package org.javamrt.progs;

import org.javamrt.mrt.*;
import org.javamrt.utils.Debug;
import org.javamrt.utils.getopts;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class route_btoa {

	public static Prefix prefix = null;
    public static InetAddress peer = null;
    public static AS      originator = null;
    public static AS      traverses = null;
    public static boolean showIPv4 = true;
    public static boolean showIPv6 = true;
    public static boolean printRFC4893violations = false;
    public static String outputToFileExt = "";
    public static boolean callSystemExit = true;

	public static void main(String args[]) {
		BGPFileReader in;
		MRTRecord record;
		getopts prueba;

		if (Debug.compileDebug)
			prueba = new getopts(args, "46Dhmf:P:p:o:t:v");
		else
			prueba = new getopts(args, "46hmf:P:p:o:v:t:");

		char opcion;

		boolean oldall = false;

		while ((opcion = prueba.nextOption()) != 0) {
			try {
				switch (opcion) {
				case '4':
					if (showIPv4 == true && showIPv6 == true)
						showIPv6 = false;
					else
						exit(usage(1));
					break;

				case '6':
					if (showIPv4 == true && showIPv6 == true)
						showIPv4 = false;
					else
						exit(usage(1));
					break;
				case 'D':
					Debug.setDebug(true);
					break;
				case 'm':
					oldall = true;
					break;
				case 'p':
					peer = InetAddress.getByName(prueba.optarg);
					break;

				case 'P':
					prefix = Prefix.parseString(prueba.optarg);
					break;

				case 'v':
					printRFC4893violations  = true;
					break;

				case 'o':
					originator = AS.parseString(prueba.optarg);
					break;
				case 't':
					traverses = AS.parseString(prueba.optarg);
					break;
                case 'f':
                    outputToFileExt = prueba.optarg;
                    break;
                
				case 'h':
				default:
					exit(usage((opcion) == 'h' ? 0 : 1));
					break;
				}
			} catch (UnknownHostException e) {
				e.printStackTrace();
				exit(1);
			} catch (PrefixMaskException e) {
				e.printStackTrace();
				exit(1);
			} catch (Exception e) {
				e.printStackTrace();
				exit(1);
			}
		}

		if (args.length == prueba.optind) {
            exit(usage(0));
        }
        
        FileWriter out;
        for (int arg = prueba.optind; arg < args.length; arg++) {
            out = null;
            try {
                in = new BGPFileReader(args[arg]);
                if (!outputToFileExt.equals("")) {
                    out = new FileWriter(args[arg] +
                            (outputToFileExt.startsWith(".") ? outputToFileExt : "." + outputToFileExt));
                }
                int cnt=0;
                while (false == in.eof()) {
                    try {
                        if ((record = in.readNext()) == null)
                            break;
                        ++cnt;
                        if (record instanceof Open
                                || record instanceof KeepAlive
                                || record instanceof Notification)
                            continue;
                        if (record instanceof StateChange) {
                            if (oldall == true && checkPeer(record)) {
                                outputData(record.toString(), out);
                                continue;
                            }
                        }
                        if ((record instanceof TableDump)
                                || (record instanceof Bgp4Update)) {

                            if (!showIPv4 && record.getPrefix().isIPv4())
                                continue;
                            if (!showIPv6 && record.getPrefix().isIPv6())
                                continue;
                            try {
                                if (!checkPrefix(record))
                                    continue;
                                if (!checkPeer(record))
                                    continue;
                                if (!checkASPath(record))
                                    continue;
                            } catch (Exception e) {
                                e.printStackTrace(System.err);
                                System.err.printf("record = %s\n",record);
                            }
                            outputData(record.toString(), out);
                        }
                    } catch (RFC4893Exception rfce) {
                        if (printRFC4893violations)
                            System.err.println(rfce.toString());
                    } catch (Exception e) {
                        System.err.println("Failed on record [" + cnt + "]");
                        e.printStackTrace(System.err);
                    }
                }
                in.close();
                if (out != null) {
                    out.close();
                }
            } catch (java.io.FileNotFoundException e) {
                System.err.println("File not found: " + args[arg]);
            } catch (Exception ex) {
                System.err.println("Exception caught when reading " + args[arg]);
                ex.printStackTrace(System.err);
            }
        } // for (int arg...
    } // main()


    private static int usage(int retval) {
		PrintStream ps = System.err;

		ps.println("route_btoa <options> f1 ...");
		ps.println("  -h        print this help message");
		ps.println("  -m        legacy compatibility wth MRT: include all records");
		ps.println("  -4        print IPv4 prefixes only");
		ps.println("  -6        print IPv6 prefixes only");
        ps.println("  -f ext    writes the data to a file with extension e.g. .txt or txt");
		ps.println("  -p peer   print updates from a specific peer only");
		ps.println("  -P prefix print updates for a specific prefix only");
		if (Debug.compileDebug)
			ps.println("  -D        enable debugging");
		ps.println("  -o as     print updates generated by one AS only");
		ps.println("  -t as     print updates where AS is in ASPATH");
		ps.println("         -4 and -6 together are not allowed");
		ps.println(" f1 ... are filenames or URL's");
		ps.println(" Use URL's according to the server's policies");
		ps.println(" Only prints records in machine readable format\n");

		return retval;
	}

	private static boolean checkPrefix(MRTRecord mrt) {
		if (prefix == null)
			return true;
		return prefix.equals(mrt.getPrefix());
	}

	private static boolean checkPeer(MRTRecord mrt) {
		if (peer == null)
			return true;
		return peer.equals(mrt.getPeer());
	}

	private static boolean checkASPath(MRTRecord mrt) {
		if (originator == null) {
			if (traverses == null)
				return true;
			//
			// check whether AS is traversed by the prefix
			//
			return mrt.getASPath().contains(traverses);
		}
		//
		// check whether the AS originates the prefix
		//
		return originator.equals(mrt.getASPath().generator());
	}
    
    private static void outputData(String data, FileWriter output) throws IOException {
        if (output != null) {
            output.write(data + System.lineSeparator());
        } else {
            System.out.println(data);
        }
    }
    
    public static void exit(int exitCode) {
        if (callSystemExit) {
            System.exit(exitCode);
        }
    }
    
    
}
