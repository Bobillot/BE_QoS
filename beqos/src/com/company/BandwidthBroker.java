package com.company;

import dataStructs.ReservationData;
import dataStructs.Site;
import exceptions.EFCapacityReached;

import java.util.HashMap;
import java.util.Map;
import java.net.*;
import java.io.*;

import static utils.ipAddrConverter.*;

public class BandwidthBroker {

    /**
     * Stores the associations netmwork/SiteIndex
     */
    Map<Integer, Integer> networkIndexer;

    /**
     * Stores the couples SiteIndex/Site
     */
    Map<Integer, Site> siteIndexer;

    /**
     * Stores couples SiteIndex/Socket
     */
    Map<Integer, Socket> socketIndexer;


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
        Socket socket = new Socket(InetAddress.getByName(ipIntegerToString(site.getEdgeRouterIPoutside())),site.getNetcatPort());
        socketIndexer.put(siteIndex,socket);
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

    protected void configureRouter(String command, Integer ip, Integer port){

    }

    private void initialRouterConfiguration(Site mySite) //function to be called to initiate a new queue on a CE router
    {
        //TODO : Convertir les println en "commande Netcat"
        println("tc qdisc del dev " + mySite.getEdgeRouterInterfaceOutside() + " root");                                                 //repart de 0
        println("tc qdisc add dev " + mySite.getEdgeRouterInterfaceOutside() + " root handle 1: htb default 2");                         //création de la racine, les paquets non étiquetés iront dans 1:2
        println("tc class add dev " + mySite.getEdgeRouterInterfaceOutside() + " parent 1: classid 1:1 htb rate " + mySite.getTotalEFCapacity() + "Mbit ceil "+ mySite.getTotalEFCapacity() + "Mbit");      //création à la racine d'une branche 1:1 à totalEF Mbit
        println("tc class add dev " + mySite.getEdgeRouterInterfaceOutside() + " parent 1: classid 1:2 htb rate 10Mbit ceil 10Mbit");    //création à la racine d'une branche 1:2 à 10Mbit
    }
}
