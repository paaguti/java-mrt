package org.javamrt.mrt;


import org.testng.annotations.Test;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;

public class MrtTest {

    @Test
    public void testParseUnsignedAsn()
    {
        try {
            String asPath = "[37989, 4844, 7473, 6461, 209, 4200000013]";
            byte[] asnBuffer = new byte[]{2, 6, 0, 0, -108, 101, 0, 0, 18, -20, 0, 0, 29, 49, 0, 0, 25, 61, 0, 0, 0, -47, -6, 86, -22, 13};

            int asSize = 4;
            int offset = 0;

            ASPathSegment asPathSegment = new ASPathSegment(asnBuffer, offset, asSize);
            assertEquals(asPath, asPathSegment.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testMrtIpv6peerIpv4Prefix(){
        List<String> result = new ArrayList<>(11);
        byte[] header = new byte[] {90, 123, 73, 112, 0, 16, 0, 4, 0, 0, 0, -108};
        byte[] body = new byte[] {0, 0, 99, 91, 0, 0, 49, 110, 0, 0, 0, 2, 32, 1, 7, -8, 0, 36, 0, 0, 0, 0, 0, 0, 0, 0, 0, -78, 32, 1, 7, -8, 0, 36, 0, 0, 0, 0, 0, 0, 0, 0, 0, -3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 104, 2, 0, 0, 0, 37, 64, 1, 1, 0, 80, 2, 0, 22, 2, 5, 0, 0, 99, 91, 0, 0, 27, 27, 0, 0, 18, -38, 0, 0, -105, -109, 0, 0, -37, -117, 64, 3, 4, 62, 121, -64, 1, 22, 1, 0, 4, 22, 103, 29, 76, 24, 103, 2, -77, 24, 103, 2, -78, 24, 103, 2, -79, 24, 103, 2, -80, 22, 103, 2, -80, 24, 1, 0, 7, 24, 1, 0, 6, 24, 1, 0, 5, 24, 1, 0, 4};

        byte[] mrt = new byte[] {90, 123, 73, 112, 0, 16, 0, 4, 0, 0, 0, -108,0, 0, 99, 91, 0, 0, 49, 110, 0, 0, 0, 2, 32, 1, 7, -8, 0, 36, 0, 0, 0, 0, 0, 0, 0, 0, 0, -78, 32, 1, 7, -8, 0, 36, 0, 0, 0, 0, 0, 0, 0, 0, 0, -3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 104, 2, 0, 0, 0, 37, 64, 1, 1, 0, 80, 2, 0, 22, 2, 5, 0, 0, 99, 91, 0, 0, 27, 27, 0, 0, 18, -38, 0, 0, -105, -109, 0, 0, -37, -117, 64, 3, 4, 62, 121, -64, 1, 22, 1, 0, 4, 22, 103, 29, 76, 24, 103, 2, -77, 24, 103, 2, -78, 24, 103, 2, -79, 24, 103, 2, -80, 22, 103, 2, -80, 24, 1, 0, 7, 24, 1, 0, 6, 24, 1, 0, 5, 24, 1, 0, 4};
        String[] resultStr = new String[] {"BGP4MP|1518029168|A|2001:7f8:24::b2|25435|1.0.4.0/22|25435 6939 4826 38803 56203|IGP|62.121.192.1|0|0||NAG||",
                "BGP4MP|1518029168|A|2001:7f8:24::b2|25435|103.29.76.0/22|25435 6939 4826 38803 56203|IGP|62.121.192.1|0|0||NAG||",
                "BGP4MP|1518029168|A|2001:7f8:24::b2|25435|103.2.179.0/24|25435 6939 4826 38803 56203|IGP|62.121.192.1|0|0||NAG||",
                "BGP4MP|1518029168|A|2001:7f8:24::b2|25435|103.2.178.0/24|25435 6939 4826 38803 56203|IGP|62.121.192.1|0|0||NAG||",
                "BGP4MP|1518029168|A|2001:7f8:24::b2|25435|103.2.177.0/24|25435 6939 4826 38803 56203|IGP|62.121.192.1|0|0||NAG||",
                "BGP4MP|1518029168|A|2001:7f8:24::b2|25435|103.2.176.0/24|25435 6939 4826 38803 56203|IGP|62.121.192.1|0|0||NAG||",
                "BGP4MP|1518029168|A|2001:7f8:24::b2|25435|103.2.176.0/22|25435 6939 4826 38803 56203|IGP|62.121.192.1|0|0||NAG||",
                "BGP4MP|1518029168|A|2001:7f8:24::b2|25435|1.0.7.0/24|25435 6939 4826 38803 56203|IGP|62.121.192.1|0|0||NAG||",
                "BGP4MP|1518029168|A|2001:7f8:24::b2|25435|1.0.6.0/24|25435 6939 4826 38803 56203|IGP|62.121.192.1|0|0||NAG||",
                "BGP4MP|1518029168|A|2001:7f8:24::b2|25435|1.0.5.0/24|25435 6939 4826 38803 56203|IGP|62.121.192.1|0|0||NAG||",
                "BGP4MP|1518029168|A|2001:7f8:24::b2|25435|1.0.4.0/24|25435 6939 4826 38803 56203|IGP|62.121.192.1|0|0||NAG||"};

        try (BufferedInputStream in = new BufferedInputStream(new ByteArrayInputStream(mrt))) {
            BGPFileReader reader = new BGPFileReader(in);

//            MRTRecord next = reader.readNext();
            while (!reader.eof()) {
                MRTRecord next = reader.readNext();
                if (next != null) {
                    result.add(next.toString());
                }
            }
            assertEquals(result.toArray(), resultStr);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
