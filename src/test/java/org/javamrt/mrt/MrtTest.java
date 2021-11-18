package org.javamrt.mrt;

import org.testng.annotations.Test;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

public class MrtTest {

    private Base64.Decoder b64 = Base64.getDecoder();

    @Test
    public void testParseUnsignedAsn()
    {
        try {
            String asPath = "[37989, 4844, 7473, 6461, 209, 4200000013]";
            byte[] asnBuffer = new byte[]{2, 6, 0, 0, -108, 101, 0, 0, 18, -20, 0, 0, 29, 49, 0, 0, 25, 61, 0, 0, 0, -47, -6, 86, -22, 13};

            int asSize = 4;
            int offset = 0;

            ASPathSegment asPathSegment = new ASPathSegment(asnBuffer, offset, asSize);
            assertEquals(asPathSegment.toString(), asPath);

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
        String[] resultStr = new String[] {"BGP4MP|1518029168|A|2001:7f8:24:0:0:0:0:b2|25435|1.0.4.0/22|25435 6939 4826 38803 56203|IGP|62.121.192.1|0|0||NAG||",
                "BGP4MP|1518029168|A|2001:7f8:24:0:0:0:0:b2|25435|103.29.76.0/22|25435 6939 4826 38803 56203|IGP|62.121.192.1|0|0||NAG||",
                "BGP4MP|1518029168|A|2001:7f8:24:0:0:0:0:b2|25435|103.2.179.0/24|25435 6939 4826 38803 56203|IGP|62.121.192.1|0|0||NAG||",
                "BGP4MP|1518029168|A|2001:7f8:24:0:0:0:0:b2|25435|103.2.178.0/24|25435 6939 4826 38803 56203|IGP|62.121.192.1|0|0||NAG||",
                "BGP4MP|1518029168|A|2001:7f8:24:0:0:0:0:b2|25435|103.2.177.0/24|25435 6939 4826 38803 56203|IGP|62.121.192.1|0|0||NAG||",
                "BGP4MP|1518029168|A|2001:7f8:24:0:0:0:0:b2|25435|103.2.176.0/24|25435 6939 4826 38803 56203|IGP|62.121.192.1|0|0||NAG||",
                "BGP4MP|1518029168|A|2001:7f8:24:0:0:0:0:b2|25435|103.2.176.0/22|25435 6939 4826 38803 56203|IGP|62.121.192.1|0|0||NAG||",
                "BGP4MP|1518029168|A|2001:7f8:24:0:0:0:0:b2|25435|1.0.7.0/24|25435 6939 4826 38803 56203|IGP|62.121.192.1|0|0||NAG||",
                "BGP4MP|1518029168|A|2001:7f8:24:0:0:0:0:b2|25435|1.0.6.0/24|25435 6939 4826 38803 56203|IGP|62.121.192.1|0|0||NAG||",
                "BGP4MP|1518029168|A|2001:7f8:24:0:0:0:0:b2|25435|1.0.5.0/24|25435 6939 4826 38803 56203|IGP|62.121.192.1|0|0||NAG||",
                "BGP4MP|1518029168|A|2001:7f8:24:0:0:0:0:b2|25435|1.0.4.0/24|25435 6939 4826 38803 56203|IGP|62.121.192.1|0|0||NAG||"};

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

    @Test
    public void testIpv4EmbeddedIpv6With1(){

        byte[] prefixBuffer = new byte[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, -94, 97, 44, 0};
        int netmask = 118;

        try {
            Prefix prefix = new Prefix(prefixBuffer, netmask);
            String prefixResult = "::ffff:162.97.44.0/118";
           assertEquals(prefix.toString(), prefixResult);
        } catch (PrefixMaskException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testIpv4EmbeddedIpv6With0(){

        byte[] prefixBuffer = new byte[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 67, 16, -88, 0};
        int netmask = 118;

        try {
            Prefix prefix = new Prefix(prefixBuffer, netmask);
            String prefixResult = "::67.16.168.0/118";
            assertEquals(prefix.toString(), prefixResult);
        } catch (PrefixMaskException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void should_parse_empty_update() throws Exception {
        final byte[] bytes = b64.decode("RtX+aQAQAAEAAAAnMr0xbgAAAAHDQuDjw0Lh8f////////////////////8AFwIAAAAA");
        final ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
        final BGPFileReader bgpFileReader = new BGPFileReader(stream);
        final MRTRecord mrtRecord = bgpFileReader.readNext();
        assertEquals(mrtRecord.getPeer().toString(), "/195.66.224.227");
        assertEquals(mrtRecord.getPeerAS().toString(), "12989");
        assertEquals(mrtRecord.getTime(), 1188429417);
        assertEquals(mrtRecord.getType(), MRTConstants.BGP4MP);
        assertEquals(mrtRecord.getSubType(), MRTConstants.BGP4MP_MESSAGE);
        assertNull(mrtRecord.getASPath());
        assertNull(mrtRecord.getPrefix());
    }

    @Test
    public void should_parse_update_without_nlri() throws Exception {
        final byte[] bytes = b64.decode("RwcWQgAQAAEAAAB+GxsxbgAAAAIgAQf4AAQAAAAAAAAbGwABIAEH+AAEAAAAAAAAMW4AAP////////////////////8AVgIAAAA/QAEBAEACEAIHGxsJ1B3sXt9fqlHlCx+ADiUAAgEgIAEH+AAEAAAAAAAAGxsAAf6AAAAAAAAAAgzb//7/EysA");
        final ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
        final BGPFileReader bgpFileReader = new BGPFileReader(stream);
        final Bgp4Update mrtRecord = (Bgp4Update)bgpFileReader.readNext();
        assertEquals(mrtRecord.getPeer().toString(), "/2001:7f8:4:0:0:0:1b1b:1");
        assertEquals(mrtRecord.getPeerAS().toString(), "6939");
        assertEquals(mrtRecord.getTime(), 1191646786);
        assertEquals(mrtRecord.getType(), MRTConstants.BGP4MP);
        assertEquals(mrtRecord.getSubType(), MRTConstants.BGP4MP_MESSAGE);
        assertEquals(mrtRecord.getASPath().toString(), "6939 2516 7660 24287 24490 20965 2847");
        assertNull(mrtRecord.getPrefix());
        assertEquals(mrtRecord.getAttributes().toString(), "6939 2516 7660 24287 24490 20965 2847|IGP|2001:7f8:4:0:0:0:1b1b:1|0|0||NAG||");
    }

    @Test
    public void should_parse_8byte_state_change() throws Exception {
        final byte[] bytes = b64.decode("OfZLTAAQAAAAAAAIAAAAAAADAAQ=");
        final ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
        final BGPFileReader bgpFileReader = new BGPFileReader(stream);
        BGPFileReader.setLenient(true);
        final MRTRecord mrtRecord = bgpFileReader.readNext();
        assertEquals(mrtRecord.getClass(), StateChange.class);
        assertEquals(mrtRecord.toString(), "BGP4MP|972442444|STATE|0.0.0.0|0|3|4");
    }

    @Test
    public void should_parse_AFI0_and_realign_bgp_message() throws Exception {
        final byte[] bytes = b64.decode("OfZOxAAQAAEAAAAhAAAAAP////////////////////8AHQEEMGYAtNQyoccA");
        final ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
        final BGPFileReader bgpFileReader = new BGPFileReader(stream);
        BGPFileReader.setLenient(true);
        final MRTRecord mrtRecord = bgpFileReader.readNext();
        assertEquals(mrtRecord.getClass(), Open.class);
        assertEquals(mrtRecord.toString(), "OPEN|972443332|0.0.0.0|0|3560088007");
    }

    @Test
    public void should_truncate_attributes_to_record_length() throws Exception {
        final byte[] bytes = b64.decode("USa7OAAQAAQAAAByAAAjKgAAMW4AAAACIAEH+AAEAAAAAAAAIyoAASABB/gABAAAAAAAADFuAAD/////////////////////AEYCAAAAL0ABAQBAAgoCAgAAIyoAABp2kA4AGgACARAgAQf4AAQAAAAAAAAadgABACAgAQm4");
        final ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
        final BGPFileReader bgpFileReader = new BGPFileReader(stream);
        BGPFileReader.setLenient(true);
        final MRTRecord mrtRecord = bgpFileReader.readNext();
        assertEquals(mrtRecord.getClass(), Advertisement.class);
        assertEquals(mrtRecord.toString(), "BGP4MP|1361492792|A|2001:7f8:4:0:0:0:232a:1|9002|2001:9b8:0:0:0:0:0:0/32|9002 6774|IGP|2001:7f8:4:0:0:0:1a76:1|0|0||NAG||");
    }

    @Test
    public void should_parse_ipv4_end_of_rib() throws Exception {
        final byte[] bytes = new byte[] {91,15,-4,42,0,16,0,4,0,0,0,43,0,4,5,-96,0,0,49,110,0,0,0,1,-69,16,-36,-63,-69,16,-40,23,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,0,23,2,0,0,0,0};
        final ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
        final BGPFileReader bgpFileReader = new BGPFileReader(stream);
        final MRTRecord mrtRecord = bgpFileReader.readNext();
        assertEquals(mrtRecord.getClass(), EndOfRib.class);
        assertEquals(mrtRecord.toString(), "BGP4MP|1527774250|EOR|187.16.220.193|263584|");
    }

    @Test
    public void should_parse_ipv6_end_of_rib() throws Exception {
        final byte[] bytes = new byte[] {91,16,-4,-111,0,16,0,4,0,0,0,73,0,0,-77,72,0,0,49,110,0,0,0,2,32,1,13,-16,2,-24,16,0,0,0,0,0,0,0,0,1,32,1,6,124,2,-24,0,2,-1,-1,0,0,0,4,0,40,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,0,29,2,0,0,0,6,-128,15,3,0,2,1};
        final ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
        final BGPFileReader bgpFileReader = new BGPFileReader(stream);
        final MRTRecord mrtRecord = bgpFileReader.readNext();
        assertEquals(mrtRecord.getClass(), EndOfRib.class);
        assertEquals(mrtRecord.toString(), "BGP4MP|1527839889|EOR|2001:df0:2e8:1000:0:0:0:1|45896|||255.255.255.255|0|0||NAG||");
    }
}
