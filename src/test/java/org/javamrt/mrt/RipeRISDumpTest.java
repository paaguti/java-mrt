package org.javamrt.mrt;

import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertNull;

import java.io.*;

public class RipeRISDumpTest {
    @Test
    public void testParseDump()
    {
        Exception exception = null;
        try (InputStream input =
                getClass().getResourceAsStream("/updates.20170103.0630")) {
            BGPFileReader bgpFileReader =
                new BGPFileReader(new BufferedInputStream(input));
            MRTRecord mrtRecord = null;
            while (true) {
                mrtRecord = bgpFileReader.readNext();
                if (mrtRecord == null) {
                    break;
                }
            }
        } catch (Exception e) {
            exception = e;
        }
        assertNull(exception); 
    }
}
