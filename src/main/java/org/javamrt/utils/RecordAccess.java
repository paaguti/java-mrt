// This file is part of java-mrt
// A library to parse MRT files

// This file is released under LGPL 3.0
// http://www.gnu.org/licenses/lgpl-3.0-standalone.html

package org.javamrt.utils;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class RecordAccess
{

  static public long getU32 (byte[] buffer,int offset)
  {
    return getUINT(buffer, offset, 4);
  }

  static public long getUINT (byte[]buffer, int offset,int size) {
    if (offset > buffer.length - size) {
      throw new ArrayIndexOutOfBoundsException(String.format(
              "Not enough bytes to read %d bytes from offset %d in %s", size, offset, arrayToString(buffer)));
    }
    return new BigInteger(1, Arrays.copyOfRange(buffer, offset, offset + size)).longValue();
  }

  static public int getU16 (byte[]buffer, int offset)
  {
    if (offset > buffer.length - 2) {
      throw new ArrayIndexOutOfBoundsException("Not enough bytes to read U16 from offset " + offset + " in " + arrayToString(buffer));
    }
    return ((buffer[offset] & 0xff) << 8) | (buffer[offset+1] & 0xff);
  }

  static public int getU8 (byte[]buffer, int offset)
  {
    return ((int) buffer[offset] & 0x000000ff);
  }

  static public byte[] getBytes (byte[]buffer, int offset, int length)
  {
    if (buffer.length < offset + length) {
      throw new ArrayIndexOutOfBoundsException(String.format(
              "Not enough bytes to read %d bytes from offset %d in %s", length, offset, arrayToString(buffer)));
    }
    return Arrays.copyOfRange(buffer, offset, offset + length);
  }

  static public String arrayToString(byte[] buffer)
  {
    final int offset = Math.max(0, buffer.length - 128);
    return arrayToString(buffer, offset, buffer.length - offset);
  }

  static public String arrayToString(byte[] buffer,int offset,int len)
  {
    StringBuilder result = new StringBuilder();


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
    dump(System.err,buffer,offset,len);
  }

  static public void dump(java.io.PrintStream out,byte[] buffer,int len)
  {
    out.print(arrayToString(buffer,0,len));
  }

  static public void dump(byte[] buffer,int len)
  {
    dump(System.err,buffer,len);
  }

  static public void dump(java.io.PrintStream out,byte[] buffer)
  {
      out.printf("buffer is %d bytes long", buffer.length);
    out.print(arrayToString(buffer,0,buffer.length));
  }

  static public void dump(byte[] buffer)
  {
    dump(System.err,buffer,buffer.length);
  }

  public static long getUINT(ByteBuffer buffer, int size) {
    byte[] bytes = new byte[size];
    buffer.get(bytes);
    return new BigInteger(1, bytes).longValue();
  }

  public static int getU16(ByteBuffer buffer) {
    return buffer.getShort() & 0xFFFF;
  }

  public static byte[] getBytes(ByteBuffer buffer, int size) {
    byte[] bytes = new byte[size];
    buffer.get(bytes);
    return bytes;
  }
}
