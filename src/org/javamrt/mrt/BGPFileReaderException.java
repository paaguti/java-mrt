// This file is part of java-mrt
// A library to parse MRT files

// This file is released under LGPL 3.0
// http://www.gnu.org/licenses/lgpl-3.0-standalone.html

package org.javamrt.mrt;

import org.javamrt.utils.RecordAccess;

public class BGPFileReaderException
  extends java.lang.Exception
{

    public BGPFileReaderException(String message, byte[] header)
    {
	super(message + RecordAccess.arrayToString(header));
    }
    private static final long serialVersionUID = 1L;
}
