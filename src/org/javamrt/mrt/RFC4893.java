package org.javamrt.mrt;

import org.javamrt.utils.RecordAccess;


/**
 * @author paag
 * 
 *         replaces the AS4PATH segment in the ASPATH, following RFC4883 4 byte
 *         ASN's from AS4PATH attribute (raw input as bytes)
 * 
 * 
 *         throws RFC4893Exception if ConfedSequence or ConfedSet is found
 *         throws AttributeException if the ASPATH was not previously defined
 *  @version 1.00
 *  todov1.00: Use ASPath to create the 4 byte ASPATH chunk
 *  @version 2.00
 *  
 */
public class RFC4893 {
	private static boolean DEBUG = false;

	public static void replaceAS23456(byte[] buffer, ASPath aspath)
			throws RFC4893Exception, AttributeException, Exception {
		if (DEBUG) {
			System.err.printf("ASPATH = %s\nAS4PATH attribute:\n", aspath
					.toString());
			RecordAccess.dump(System.err, buffer);
		}

		if (buffer.length == 0)
			return;
		if (aspath == null)
			throw new AttributeException("AS4PATH for undefined ASPATH");

		ASPath as4path = new ASPath(buffer, 4);

		/*
		 *    [rfc4893] To prevent the possible propagation of confederation path
		 *    segments outside of a confederation, the path segment types
		 *    AS_CONFED_SEQUENCE and AS_CONFED_SET [RFC3065] are declared invalid
		 *    for the AS4_PATH attribute.
         */
		
		for (AS as4:as4path.path) {
			if (as4 instanceof ASConfedSet || as4 instanceof ASConfedSequence)
				throw new RFC4893Exception(MRTConstants.asConfedSequence, aspath,as4path);
		}

		if (DEBUG) {
			System.err.printf("as4path    = %s\n", as4path);
			System.err.printf("old ASPATH = %s\n", aspath);
		}

		/*
		 * If the number of AS numbers in the AS_PATH attribute is less than the
		 * number of AS numbers in the AS4_PATH attribute, then the AS4_PATH
		 * attribute SHALL be ignored, and the AS_PATH attribute SHALL be taken
		 * as the AS path information.
		 */
		if (aspath.length() < as4path.length())
			return;
		/*
		 * If the number of AS numbers in the AS_PATH attribute is larger than
		 * or equal to the number of AS numbers in the AS4_PATH attribute, then
		 * the AS path information SHALL be constructed by taking as many AS
		 * numbers and path segments as necessary from the leading part of the
		 * AS_PATH attribute, and then prepending them to the AS4_PATH attribute
		 * so that the AS path information has an identical number of AS numbers
		 * as the AS_PATH attribute.
		 */
		int asElement = aspath.length() - as4path.length();
		aspath.getPath().subList(asElement, aspath.length()).clear();
		aspath.getPath().addAll(as4path.getPath());
		if (DEBUG)
			System.err.printf("new ASPath = %s\n", aspath.toString());

		aspath.mkPrependers(); // rebuild prepender list
	}
}
