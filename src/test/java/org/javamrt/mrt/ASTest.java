package org.javamrt.mrt;

import org.junit.Assert;
import org.junit.Test;

public class ASTest {

    @Test
    public void should_create_AS() {
        Assert.assertEquals(0L, new AS(0L).getASN());
        Assert.assertEquals(5L, new AS(5L).getASN());
        Assert.assertEquals(65535L, new AS(65535L).getASN());
        Assert.assertEquals(65536L, new AS(65536L).getASN());
        Assert.assertEquals(0xFFFFFFFFL, new AS(0xFFFFFFFFL).getASN());
    }

    @Test
    public void should_create_AS_from_bytes() {
        Assert.assertEquals(0L, new AS(new byte[]{0,0,0,0}).getASN());
        Assert.assertEquals(5L, new AS(new byte[]{0,0,0,5}).getASN());
        Assert.assertEquals(65535L, new AS(new byte[]{0,0,-1,-1}).getASN());
        Assert.assertEquals(65536L, new AS(new byte[]{0,1,0,0}).getASN());
        Assert.assertEquals(0xFFFFFFFFL, new AS(new byte[]{-1,-1,-1,-1}).getASN());
    }

    @Test
    public void should_print_AS() {
        Assert.assertEquals("5", new AS(5L).toString());
        Assert.assertEquals("0", new AS(0L).toString());
        Assert.assertEquals("65535", new AS(65535L).toString());
        Assert.assertEquals("65536", new AS(65536L).toString());
        Assert.assertEquals("4294967295", new AS(0xFFFFFFFFL).toString());
    }

    @Test
    public void should_compare() {
        Assert.assertEquals(0, new AS(0L).compareTo(new AS(0L)));
        Assert.assertEquals(0, new AS(5L).compareTo(new AS(5L)));
        Assert.assertEquals(0, new AS(65535L).compareTo(new AS(65535L)));
        Assert.assertEquals(0, new AS(65536L).compareTo(new AS(65536L)));
        Assert.assertEquals(0, new AS(0xFFFFFFFFL).compareTo(new AS(0xFFFFFFFFL)));

        Assert.assertEquals(1, new AS(5L).compareTo(new AS(0L)));
        Assert.assertEquals(1, new AS(65535L).compareTo(new AS(5L)));
        Assert.assertEquals(1, new AS(65536L).compareTo(new AS(65535L)));
        Assert.assertEquals(1, new AS(0xFFFFFFFFL).compareTo(new AS(65536L)));

        Assert.assertEquals(-1, new AS(0L).compareTo(new AS(5L)));
        Assert.assertEquals(-1, new AS(5L).compareTo(new AS(65535L)));
        Assert.assertEquals(-1, new AS(65535L).compareTo(new AS(65536L)));
        Assert.assertEquals(-1, new AS(65536L).compareTo(new AS(0xFFFFFFFFL)));
    }

    @Test
    public void should_equal() {
        Assert.assertEquals(new AS(0L), new AS(0L));
        Assert.assertEquals(new AS(5L), new AS(5L));
        Assert.assertEquals(new AS(65535L), new AS(65535L));
        Assert.assertEquals(new AS(65536L), new AS(65536L));
        Assert.assertEquals(new AS(0xFFFFFFFFL), new AS(0xFFFFFFFFL));
    }

    @Test
    public void should_recognise_4_byte_asn() {
        Assert.assertEquals(false, new AS(0L).is4Byte());
        Assert.assertEquals(false, new AS(5L).is4Byte());
        Assert.assertEquals(false, new AS(65535L).is4Byte());
        Assert.assertEquals(true, new AS(65536L).is4Byte());
        Assert.assertEquals(true, new AS(0xFFFFFFFFL).is4Byte());
    }
}
