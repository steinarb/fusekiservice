package no.priv.bang.fuseki.fusekiservice;

import static org.junit.Assert.*;

import org.junit.Test;

public class FusekiServiceTest {

    @Test
    public void test() throws InterruptedException {
        FusekiService endpoint = new FusekiService();
        endpoint.startDaemon();
        Thread.sleep(10000);
        endpoint.stopDaemon();
    }

}
