package org.javamrt.mrt;


import org.testng.annotations.Test;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

public class LargeCommunitiesTest {
    @Test
    public void testParseDump()
    {
        Exception exception = null;
        List<LargeCommunity> largeCommunities =
            new ArrayList<LargeCommunity>();

        try (InputStream input =
                getClass().getResourceAsStream("/updates.20170103.0630")) {
            BGPFileReader bgpFileReader =
                new BGPFileReader(new BufferedInputStream(input));
            while (true) {
                MRTRecord mrtRecord = bgpFileReader.readNext();
                if (mrtRecord == null) {
                    break;
                }
                if (mrtRecord instanceof Bgp4Update) {
                    Bgp4Update bgp4update = (Bgp4Update) mrtRecord;
                    Attributes attributes = bgp4update.getAttributes();
                    if (attributes != null) {
                        LargeCommunity largeCommunity =
                            attributes.getLargeCommunity();
                        if (largeCommunity != null) {
                            largeCommunities.add(largeCommunity);
                        }
                    }
                }
            }
        } catch (Exception e) {
            exception = e;
        }
        assertNull(exception);

        assertEquals(largeCommunities.size(), 4);

        Set<String> strings =
            largeCommunities.stream()
                            .map(LargeCommunity::toString)
                            .collect(Collectors.toSet());
        assertEquals(strings.size(), 1);
        assertEquals(strings.iterator().next(), "200753:200:46524131");
    }

    @Test
    public void should_parse_large_community_with_zero_length() throws Exception {
        final String base64 = "X3+NoAAQAAQAAACRAADjuwAAMW4AAAABW840glvONP3/////////////////////AH0CADwYF4YQGBeGERgXlyEYF6mgGBerYBhAwAAYQMABGEDAAhhAwAMYQMAEGEDABRhybOsYcmz4GLYSyBi2EskAJkABAQBAAg4CAwAA47sAABsbAAB+BUADBFvONILACATjuwPo4CAAGJ73ew==";
        final byte[] bytes = Base64.getDecoder().decode(base64);
        final BGPFileReader bgpFileReader = new BGPFileReader(new ByteArrayInputStream(bytes));
        MRTRecord mrtRecord = null;
        MRTRecord tmp;
        while (!bgpFileReader.eof()) {
            tmp = bgpFileReader.readNext();
            if (tmp != null) mrtRecord = tmp;
        }
        assertEquals(mrtRecord.getClass(), Advertisement.class);
        assertEquals(mrtRecord.toString(), "BGP4MP|1602194848|A|91.206.52.130|58299|158.247.123.0/24|58299 6939 32261|IGP|91.206.52.130|0|0|58299:1000|NAG||");
    }
}
