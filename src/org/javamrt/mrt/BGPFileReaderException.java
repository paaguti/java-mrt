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
