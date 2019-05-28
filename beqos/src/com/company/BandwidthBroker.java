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
    Map<Integer, Integer> networkIndexer;

    /**
     * Stores the couples SiteIndex/Site
     */
    Map<Integer, Site> siteIndexer;


    private static BandwidthBroker ourInstance = new BandwidthBroker();

    public static BandwidthBroker getInstance() {
        return ourInstance;
    }

    private BandwidthBroker() {
        networkIndexer = new HashMap<>();
        siteIndexer = new HashMap<>();
    }

    public void makeReservation(ReservationData resData) throws EFCapacityReached {
        //src,dst
        Integer[] siteIndex = new Integer[2];
        siteIndex[0] = getSiteIndexFromIP(resData.getSrcIP());
        siteIndex[1] = getSiteIndexFromIP(resData.getDstIP());

        for (Integer i : siteIndex) {
            if (!siteIndexer.get(i).isReservationPossible(resData.getDataRateReq()))
                throw new EFCapacityReached();
        }

        //we know both sites have capacity
        for (Integer i : siteIndex) {
            siteIndexer.get(i).makeReservation(resData);
        }
    }

    public void removeReservation(ReservationData resData){
        Integer[] siteIndex = new Integer[2];
        siteIndex[0] = getSiteIndexFromIP(resData.getSrcIP());
        siteIndex[1] = getSiteIndexFromIP(resData.getDstIP());

        for (Integer i : siteIndex){
            siteIndexer.get(i).removeReservation(resData);
        }
    }

    public void addSite(Site site, Integer siteIndex){
        networkIndexer.put(site.getNetwork(), siteIndex);
        siteIndexer.put(siteIndex,site);
        System.out.println("Added site " + siteIndex + " with network " + utils.ipAddrConverter.ipIntegerToString(site.getNetwork()));
    }


    protected Integer getSiteNetworkFromIP(Integer ip) {
        Integer result = 0;
        Integer netmask;
        for (Integer network: networkIndexer.keySet()){
            netmask = network & ip;
            if ( Integer.compareUnsigned(netmask,result) > 0)
                result = netmask;
        }
        return result;
    }

    protected Integer getSiteIndexFromIP(Integer IP) {
        return networkIndexer.get(getSiteNetworkFromIP(IP));
    }
}
