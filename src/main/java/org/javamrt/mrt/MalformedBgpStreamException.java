package org.javamrt.mrt;

import java.io.IOException;

/**
 * Thrown if the input stream of data is broken in a way that is not possible to read any more MRT records from it.
 * Therefore you should not try to call BGPFileReader.readNext() on the same input anymore.
 * All other exceptions are potentially local to the MRT record they are thrown for, so the readNext() should be possible.
 */
public class MalformedBgpStreamException extends IOException {
    public MalformedBgpStreamException(Throwable e) {
        super(e);
    }

    public MalformedBgpStreamException(String s) {
        super(s);
    }
}
