package dataStructs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static utils.ipAddrConverter.*;

import static org.junit.jupiter.api.Assertions.*;

class SiteTest {

    Site site;
    ReservationData resDataSrc;
    @BeforeEach
    void setUp() {
        site = new Site(ipStringToInteger("255.255.255.0"),
                        ipStringToInteger("192.168.10.1"),
                        "eth0",
                        ipStringToInteger("202.205.205.1"),
                        "eth1",
                        4000,3000);
        resDataSrc = new ReservationData(5000,6000,
                                                         ipStringToInteger("192.168.10.5"),
                                                         ipStringToInteger("192.168.10.5"),
                                                         64);
    }

    @Test
    void makeReservation() {

        Integer usedBeg = site.getUsedEfCapacity();
        Integer queueSizeBeg = site.getQueueReservationList().size();
        List<String> lstStr = site.makeReservation(resDataSrc);
        assertEquals(usedBeg+64,site.getUsedEfCapacity());
        assertTrue(site.getUsedEfCapacity()>=0);
        assertEquals(queueSizeBeg+1,site.getQueueReservationList().size());
    }

    @Test
    void removeReservation() {
        site.makeReservation(resDataSrc);
        Integer usedBeg = site.getUsedEfCapacity();
        Integer queueSizeBeg = site.getQueueReservationList().size();
        List<String> lstStr = site.removeReservation(resDataSrc);
        assertEquals(usedBeg-64,site.getUsedEfCapacity());
        assertTrue(site.getUsedEfCapacity()>=0);
        assertEquals(queueSizeBeg-1,site.getQueueReservationList().size());
    }
}