package application;

import dataStructs.ReservationData;
import dataStructs.Site;
import static org.junit.jupiter.api.Assertions.*;

import exceptions.EFCapacityReached;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static utils.ipAddrConverter.*;

class BandwidthBrokerTest {

    @BeforeEach
    void setUp() {
        Site site1 = new Site(ipStringToInteger("255.255.255.0"),
                              ipStringToInteger("192.168.10.1"),
                              "eth0",
                              ipStringToInteger("202.205.205.1"),
                              "eth1",
                              4000,3000);
        Site site2 = new Site(ipStringToInteger("255.255.255.0"),
                              ipStringToInteger("192.168.20.1"),
                              "eth0",
                              ipStringToInteger("206.206.206.1"),
                              "eth1",
                              4000,3000);
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

    @Test
    void makeReservation() {
        ReservationData resData = new ReservationData(5000,6000,ipStringToInteger("192.168.10.5"),
                                                      ipStringToInteger("192.168.20.5"), 500);
        BandwidthBroker BB = BandwidthBroker.getInstance();
        try {
            BB.makeReservation(resData);
            assertEquals(500,BB.getSiteIndexer().get(1).getUsedEfCapacity());
        } catch (EFCapacityReached efCapacityReached) {
            fail("Should not have thrown");
        }
    }

    @Test
    void removeReservation() {
    }

    @Test
    void addSite() {
    }
}