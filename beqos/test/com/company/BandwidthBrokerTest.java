package com.company;

import dataStructs.Site;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static utils.ipAddrConverter.*;

class BandwidthBrokerTest {

    @BeforeEach
    void setUp() {
        Site site1 = new Site(ipStringToInteger("255.255.255.0"), ipStringToInteger("192.168.10.1"), 3000);
        Site site2 = new Site(ipStringToInteger("255.255.255.0"), ipStringToInteger("192.168.20.1"),3000);
        BandwidthBroker BB = BandwidthBroker.getInstance();

        BB.addSite(site1,1);
        BB.addSite(site2,2);
    }

    @Test
    void getSiteNetworkFromIP() {
        BandwidthBroker BB = BandwidthBroker.getInstance();

        Integer ipTest = ipStringToInteger("192.168.10.5");
        Integer network = ipStringToInteger("192.168.10.0");
        assertEquals(network,BB.getSiteNetworkFromIP(ipTest));
    }

    @Test
    void getSiteIndexFromIP() {
        BandwidthBroker BB = BandwidthBroker.getInstance();

        Integer ipTest = ipStringToInteger("192.168.10.5");
        Integer indexTest = 1;
        assertEquals(indexTest,BB.getSiteIndexFromIP(ipTest));

    }
}