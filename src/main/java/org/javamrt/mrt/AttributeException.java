// This file is part of java-mrt
// A library to parse MRT files

// This file is released under LGPL 3.0
// http://www.gnu.org/licenses/lgpl-3.0-standalone.html

/**
 *
 */
package org.javamrt.mrt;

/**
 * @author paag
 *
 */
public class AttributeException extends Exception {

	public AttributeException(String cause) {
		super(cause);
	}

	public AttributeException(int unhandledType) {
		super(String.format("Attribute TYPE %d (0x%02x) unhandled\n",
				unhandledType, unhandledType));
	}

	public AttributeException(String format,Object... objs) {
		super(String.format(format,objs));
	}
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

}
