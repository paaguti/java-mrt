package org.javamrt.mrt;

import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;

public class ASTest {

    @Test
    public void should_create_AS() {
        assertEquals(0L, new AS(0L).getASN());
        assertEquals(5L, new AS(5L).getASN());
        assertEquals(65535L, new AS(65535L).getASN());
        assertEquals(65536L, new AS(65536L).getASN());
        assertEquals(0xFFFFFFFFL, new AS(0xFFFFFFFFL).getASN());
    }

    @Test
    public void should_create_AS_from_2_bytes() {
        assertEquals(0L, new AS(new byte[]{0,0}).getASN());
        assertEquals(5L, new AS(new byte[]{0,5}).getASN());
        assertEquals(255L, new AS(new byte[]{0,-1}).getASN());
        assertEquals(256L, new AS(new byte[]{1,0}).getASN());
        assertEquals(0xffffL, new AS(new byte[]{-1,-1}).getASN());
        assertEquals(0x8990L, new AS(new byte[]{(byte)0x89,(byte)0x90}).getASN());
    }

    @Test
    public void should_create_AS_from_4_bytes() {
        assertEquals(0L, new AS(new byte[]{0,0,0,0}).getASN());
        assertEquals(5L, new AS(new byte[]{0,0,0,5}).getASN());
        assertEquals(65535L, new AS(new byte[]{0,0,-1,-1}).getASN());
        assertEquals(65536L, new AS(new byte[]{0,1,0,0}).getASN());
        assertEquals(0xFFFFFFFFL, new AS(new byte[]{-1,-1,-1,-1}).getASN());
    }

    @Test
    public void should_print_AS() {
        assertEquals("5", new AS(5L).toString());
        assertEquals("0", new AS(0L).toString());
        assertEquals("65535", new AS(65535L).toString());
        assertEquals("65536", new AS(65536L).toString());
        assertEquals("4294967295", new AS(0xFFFFFFFFL).toString());
    }

    @Test
    public void should_compare() {
        assertEquals(0, new AS(0L).compareTo(new AS(0L)));
        assertEquals(0, new AS(5L).compareTo(new AS(5L)));
        assertEquals(0, new AS(65535L).compareTo(new AS(65535L)));
        assertEquals(0, new AS(65536L).compareTo(new AS(65536L)));
        assertEquals(0, new AS(0xFFFFFFFFL).compareTo(new AS(0xFFFFFFFFL)));

        assertEquals(1, new AS(5L).compareTo(new AS(0L)));
        assertEquals(1, new AS(65535L).compareTo(new AS(5L)));
        assertEquals(1, new AS(65536L).compareTo(new AS(65535L)));
        assertEquals(1, new AS(0xFFFFFFFFL).compareTo(new AS(65536L)));

        assertEquals(-1, new AS(0L).compareTo(new AS(5L)));
        assertEquals(-1, new AS(5L).compareTo(new AS(65535L)));
        assertEquals(-1, new AS(65535L).compareTo(new AS(65536L)));
        assertEquals(-1, new AS(65536L).compareTo(new AS(0xFFFFFFFFL)));
    }

    @Test
    public void should_equal() {
        assertEquals(new AS(0L), new AS(0L));
        assertEquals(new AS(5L), new AS(5L));
        assertEquals(new AS(65535L), new AS(65535L));
        assertEquals(new AS(65536L), new AS(65536L));
        assertEquals(new AS(0xFFFFFFFFL), new AS(0xFFFFFFFFL));
    }

    @Test
    public void should_recognise_4_byte_asn() {
        assertEquals(false, new AS(0L).is4Byte());
        assertEquals(false, new AS(5L).is4Byte());
        assertEquals(false, new AS(65535L).is4Byte());
        assertEquals(true, new AS(65536L).is4Byte());
        assertEquals(true, new AS(0xFFFFFFFFL).is4Byte());
    }
}
