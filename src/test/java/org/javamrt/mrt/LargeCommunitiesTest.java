package org.javamrt.mrt;


import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertNull;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
}
