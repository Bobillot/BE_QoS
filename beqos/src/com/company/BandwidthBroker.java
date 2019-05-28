package com.company;

import dataStructs.ReservationData;
import dataStructs.Site;
import exceptions.EFCapacityReached;

import java.util.HashMap;
import java.util.Map;

public class BandwidthBroker {

    /**
     * Stores the associations netmwork/SiteIndex
     */
    Map<Integer, Integer> netmaskIndexer;

    /**
     * Stores the couples SiteIndex/Site
     */
    Map<Integer, Site> siteIndexer;


    private static BandwidthBroker ourInstance = new BandwidthBroker();

    public static BandwidthBroker getInstance() {
        return ourInstance;
    }

    private BandwidthBroker() {
        netmaskIndexer = new HashMap<>();
        siteIndexer = new HashMap<>();
    }

    public void makeReservation(ReservationData InitiatorResData) throws EFCapacityReached {
        //src,dst
        Integer[] siteIndex = new Integer[2];
        siteIndex[0] = getSiteIndexFromIP(InitiatorResData.getSrcIP());
        siteIndex[1] = getSiteIndexFromIP(InitiatorResData.getDstIP());

        for (Integer i : siteIndex) {
            if (!siteIndexer.get(i).isReservationPossible(InitiatorResData.getDataRateReq()))
                throw new EFCapacityReached();
        }

        //we know both sites have capacity
        for (Integer i : siteIndex) {
            siteIndexer.get(i).makeReservation(InitiatorResData);
        }
    }

    public void addSite(Site site, Integer siteIndex){
        netmaskIndexer.put(site.getNetwork(), siteIndex);
        siteIndexer.put(siteIndex,site);
        System.out.println("Added site " + siteIndex + " with network " + utils.ipAddrConverter.ipIntegerToString(site.getNetwork()));
    }


    private Integer getSiteNetmaskFromIP(Integer ip) {
        //TODO Implement
        return 0;
    }

    private Integer getSiteIndexFromIP(Integer IP) {
        return netmaskIndexer.get(getSiteNetmaskFromIP(IP));
    }
}
