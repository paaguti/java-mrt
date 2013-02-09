// This file is part of java-mrt
// A library to parse MRT files

// This file is released under LGPL 3.0
// http://www.gnu.org/licenses/lgpl-3.0-standalone.html

package org.javamrt.mrt;

import java.util.LinkedList;

import org.javamrt.utils.RecordAccess;


/**
 * The AS_PATH and AS4_PATH attributes are composed
 * of a series of one or more ASPATH_SEGMENTS<br>
 * This class unifies the decoding of such ASPATH_SEGMENTS
 *
 * @author paag
 * @version 1.00
 */
public class ASPathSegment
{
	protected LinkedList<AS> asSequence;
	private int bLen,type;

	/**
	 * Create a new ASPathSegment
	 *
	 * @param buffer   holding the ASPATH
	 * @param offset   offset into the ASPATH
	 * @param asSize   2 or 4 depending ASPATH or AS4PATH
	 * @throws Exception
	 */
	public ASPathSegment(byte[] buffer, int offset, int asSize)
	throws Exception
	{
		if (asSize != 2 && asSize != 4)
			throw new Exception (String.format("Invalid AS size %d in ASPathSegment.\n"+
					"must be 2 or 4",asSize));

		asSequence = new LinkedList<AS>();
		type = RecordAccess.getU8(buffer, offset);
		int len = RecordAccess.getU8(buffer, offset + 1);
		bLen = 2;
		try {
			for (int i = 0; i < len; i++) {
				asSequence.add(new AS(RecordAccess.getBytes(buffer, offset + bLen, asSize)));
				bLen += asSize;
			}
		} catch (Exception e) {
			System.err.printf("len = %d,offset = %d; bLen = %d\n",len,offset,bLen);
			RecordAccess.dump(System.err,buffer);
			throw e;
		}
	}

	/**
	 *
	 * @return the length of the ASPATH segment in bytes
	 */
	public int bLen() {
		return bLen;
	}

	/**
	 *
	 * @return the type of the ASPATH segment
	 * @see MRTConstants for valid values
	 */
	public int bType() {
		return type;
	}

	/**
	 *
	 * @return AS if ASPATH segment contained only one,<br> else null
	 */
	public AS getAS() {
		if (this.asSequence.size() == 1)
			return this.asSequence.getFirst();
		return null;
	}

	/**
	 *
	 * @return the sequence of AS's in the elements
	 */
	public LinkedList<AS> getASList() {
		return this.asSequence;
	}

	public String toString() {
		return this.asSequence.toString();
	}
}
