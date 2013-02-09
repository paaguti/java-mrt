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
