package org.javamrt.utils;

public class RecordAccess
{

  static public long getU32 (byte[] buffer,int offset)
  {
    return getUINT(buffer, offset, 4);
  }

  static public long getUINT (byte[]buffer, int offset,int size)
  {
    long result = 0;
    //    try {
    for (int i = 0; i < size; i++)
      {
	result = ((result << 8) & 0xffffff00) + (buffer[offset + i] & 0xff);
      }
    /*
    } catch (java.lang.ArrayIndexOutOfBoundsException aioobe) {
        aioobe.printStackTrace(System.err);
        System.err.printf("Accessing %d bytes long buffer at pos %d\n",buffer.length,offset);
	dump(System.err,buffer);
        System.exit(1);
    }
    */
    return result;
  }

  static public int getU16 (byte[]buffer, int offset)
  {
    int result = 0;
    for (int i = 0; i < 2; i++)
      {
	result = ((result << 8) & 0xff00) + (buffer[offset + i] & 0xff);
      }
    return result;
  }

  static public int getU8 (byte[]buffer, int offset)
  {
    return ((int) buffer[offset] & 0x000000ff);
  }

  static public byte[] getBytes (byte[]buffer, int offset, int length)
  {
    byte[]result = new byte[length];
    for (int i = 0; i < length; i++)
      result[i] = buffer[offset + i];
    return result;
  }
  
  static public String arrayToString(byte[] buffer)
  {
    return arrayToString(buffer,0,buffer.length);
  }
  
  static public String arrayToString(byte[] buffer,int offset,int len) 
  {
    StringBuffer result = new StringBuffer();
    

    for (int i=offset - (offset % 8);i < offset+len;i++)
      {
	if (i % 8 == 0) 
	  result.append(String.format("\n%4d\t",i));
	if (i >= offset)
	  result.append(String.format("%02X ",buffer[i]));
	else
	  result.append("   ");
      }
    result.append("\n");
    
    return result.toString();
  }
  
  static public void dump(java.io.PrintStream out,byte[] buffer,int offset,int len) 
  {
    out.print(arrayToString(buffer,offset,len));
  }

  static public void dump(byte[] buffer,int offset,int len) 
  {
    dump(System.out,buffer,offset,len);
  }

  static public void dump(java.io.PrintStream out,byte[] buffer,int len) 
  {
    out.print(arrayToString(buffer,0,len));
  }

  static public void dump(byte[] buffer,int len) 
  {
    dump(System.out,buffer,len);
  }

  static public void dump(java.io.PrintStream out,byte[] buffer) 
  {
    out.printf("buffer is %d bytes long",buffer.length);
    out.print(arrayToString(buffer,0,buffer.length));
  }

  static public void dump(byte[] buffer) 
  {
    dump(System.out,buffer,buffer.length);
  }

}
