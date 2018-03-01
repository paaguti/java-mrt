package org.javamrt.mrt;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class ASPathTest {

    // BGP4MP|1519047444|A|2001:478:124::146|25152|2405:6e00::/32|25152 6939 2299530082 133612 {18291} {18291}|IGP|2001:478:124::176|0|0||NAG|65512 10.247.255.147|
    private String as4update =
            "WorTFAAQAAQAAACQAABiQAAAMW4AAAACIAEEeAEkAAAAAAAAAAABRiABBHgBJAAAAAAAAAAAAXH/////////////////////AGQCAAAATYAOGgACARAgAQR4ASQAAAAAAAAAAAF2ACAkBW4AQAEBAEACHgIEAABiQAAAGxuJEAtiAAIJ7AEBAABHcwEBAABHc8AHCAAA/+gK9/+T";

    // BGP4MP|1517836325|A|195.66.224.133|22822|45.127.112.0/23|22822 54825 40138|IGP|195.66.224.133|0|0||NAG||
    // BGP4MP|1517836325|A|195.66.224.133|22822|103.196.39.0/24|22822 54825 40138|IGP|195.66.224.133|0|0||NAG||
    // BGP4MP|1517836325|A|195.66.224.133|22822|103.196.38.0/24|22822 54825 40138|IGP|195.66.224.133|0|0||NAG||
    // BGP4MP|1517836325|A|195.66.224.133|22822|103.196.36.0/24|22822 54825 40138|IGP|195.66.224.133|0|0||NAG||
    // BGP4MP|1517836325|A|195.66.224.133|22822|103.196.37.0/24|22822 54825 40138|IGP|195.66.224.133|0|0||NAG||
    // BGP4MP|1517836325|A|195.66.224.133|22822|45.127.114.0/24|22822 54825 40138|IGP|195.66.224.133|0|0||NAG||
    // BGP4MP|1517836325|A|195.66.224.133|22822|45.127.115.0/24|22822 54825 40138|IGP|195.66.224.133|0|0||NAG||
    private String as2Update =
            "WnhYJQAQAAEAAABgWSYxbgAAAAHDQuCFw0Lh8f////////////////////8AUAIAAAAdQAEBAEACCAIDWSbWKZzKQAMEw0LghYAEBAAAAAAXLX9wGGfEJxhnxCYYZ8QkGGfEJRgtf3IYLX9z";


    @Test
    public void testShouldCreateAsPathFromAs4Update() {
        List<MRTRecord> mrtRecords = parseMrts(as4update);
        Assert.assertEquals(mrtRecords.size(), 1);
        MRTRecord mrtRecord = mrtRecords.get(0);
        Assert.assertTrue(mrtRecord instanceof Advertisement);
        ASPath asPath = mrtRecord.getASPath();
        Assert.assertEquals(asPath.toString(), "25152 6939 2299530082 133612 18291 18291");
    }

    @Test
    public void testShouldCreateAsPathFromAs2Update() {
        List<MRTRecord> mrtRecords = parseMrts(as2Update);
        Assert.assertEquals(mrtRecords.size(), 7);
        for (final MRTRecord mrtRecord : mrtRecords) {
            Assert.assertTrue(mrtRecord instanceof Advertisement);
            ASPath asPath = mrtRecord.getASPath();
            Assert.assertEquals(asPath.toString(), "22822 54825 40138");
        }
    }

    public static List<MRTRecord> parseMrts(String base64) {
        byte[] bytes = Base64.getDecoder().decode(base64);
        return parseMrts(bytes);
    }

    public static List<MRTRecord> parseMrts(byte[] bytes) {
        List<MRTRecord> result = new ArrayList<>();

        try (BufferedInputStream in = new BufferedInputStream(new ByteArrayInputStream(bytes))) {
            BGPFileReader reader = new BGPFileReader(in);
            while (!reader.eof()) {
                MRTRecord next = reader.readNext();
                if (next != null) {
                    result.add(next);
                }
            }
        } catch (Exception e) {
            final String encoded = Base64.getEncoder().encodeToString(bytes);
            throw new RuntimeException("Error parsing MRT bytes " + encoded, e);
        }

        return result;
    }

}
