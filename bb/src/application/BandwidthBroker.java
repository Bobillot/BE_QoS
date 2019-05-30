package application;

import dataStructs.ReservationData;
import dataStructs.Site;
import exceptions.EFCapacityReached;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        socketIndexer = new HashMap<>();
    }

    /**
     * Makes a reservation for  traffic
     * Configures router
     *
     * @param resData
     * @throws EFCapacityReached
     */
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
        List<String> stringList;
        for (Integer i : siteIndex) {
            stringList = siteIndexer.get(i).makeReservation(resData);
            //configure router
            for (String s : stringList) {
                configureRouter(s, i);
            }
        }
    }

    /**
     * Remove reservation
     * reset router settings as needed
     *
     * @param resData
     */
    public void removeReservation(ReservationData resData) {
        Integer[] siteIndex = new Integer[2];
        siteIndex[0] = getSiteIndexFromIP(resData.getSrcIP());
        siteIndex[1] = getSiteIndexFromIP(resData.getDstIP());

        List<String> stringList;
        for (Integer i : siteIndex) {
            stringList = siteIndexer.get(i).removeReservation(resData);
            //reset router commands
            for(String s : stringList){
                configureRouter(s,i);
            }
        }
    }

    public void addSite(Site site, Integer siteIndex) {
        networkIndexer.put(site.getNetwork(), siteIndex);
        siteIndexer.put(siteIndex, site);
        Socket socket = null;
        try {
            socket = new Socket(InetAddress.getByName(ipIntegerToString(site.getEdgeRouterIPinside())),
                                site.getNetcatPort());
        } catch (IOException e) {
            e.printStackTrace();
        }
        socketIndexer.put(siteIndex, socket);
        initialRouterConfiguration(site);
        System.out.println("Added site " + siteIndex + " with network " + utils.ipAddrConverter.ipIntegerToString(
                site.getNetwork()));
    }


    /**
     * Returns the network of a website from an ip address in the site
     * @param ip
     * @return
     */
    protected Integer getSiteNetworkFromIP(Integer ip) {
        Integer result = 0;
        Integer netmask;
        for (Integer network : networkIndexer.keySet()) {
            netmask = network & ip;
            if (Integer.compareUnsigned(netmask, result) > 0)
                result = netmask;
        }
        return result;
    }

    /**
     * Return the index of a site from a given IP address in the site
     * @param IP
     * @return
     */
    protected Integer getSiteIndexFromIP(Integer IP) {
        return networkIndexer.get(getSiteNetworkFromIP(IP));
    }

    protected void configureRouter(String command, Integer siteIndex) {
        Socket socket = socketIndexer.get(siteIndex);
        try {
            OutputStream os = socket.getOutputStream();
            String s = command + "\r\n";
            System.out.println(command);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
            bw.write(command);
            bw.flush();
            bw.newLine();
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Initial configuration of TC queues on the router
     * @param mySite
     */
    private void initialRouterConfiguration(Site mySite)
    {
        Integer siteIndex = networkIndexer.get(mySite.getNetwork());
        List<String> stringList = new ArrayList<>();

        //generate commands
        //repart de 0
        stringList.add(
                "tc qdisc del dev " + mySite.getEdgeRouterInterfaceOutside() + " root");

        //création de la racine, les paquets seront par défaut étiquetés avec 20
        stringList.add(
                "tc qdisc add dev " + mySite.getEdgeRouterInterfaceOutside()
                + " root handle 1: htb default 20");
        //création à la racine d'une branche 1:1 à totalEF Mbit
        stringList.add(
                "tc class add dev " + mySite.getEdgeRouterInterfaceOutside()
                + " parent 1: classid 1:1 htb rate "
                + mySite.getTotalEFCapacity() + "Mbit ceil " + mySite.getTotalEFCapacity() + "Mbit");
        //création à la racine d'une branche 1:2 à 10Mbit
        stringList.add("tc class add dev " + mySite.getEdgeRouterInterfaceOutside()
                       + " parent 1: classid 1:2 htb rate 10Mbit ceil 10Mbit");
        //les paquets étiquetés 20 iront dans la file 1:2 (BE)
        stringList.add("tc filter add dev " + mySite.getEdgeRouterInterfaceOutside()
                + " parent 1: protocol ip prio 1 handle 20 fw flowid 1:2");

        stringList.add("iptables -A POSTROUTING -t mangle -p udp -j MARK --set-mark 20");

        stringList.add("iptables -A POSTROUTING -t mangle -p udp -j DSCP --set-dscp-class BE");

        //send commands to router
        for (String s : stringList) {
            configureRouter(s, siteIndex);
        }
    }

    public Map<Integer, Integer> getNetworkIndexer() {
        return networkIndexer;
    }

    public void setNetworkIndexer(Map<Integer, Integer> networkIndexer) {
        this.networkIndexer = networkIndexer;
    }

    public Map<Integer, Site> getSiteIndexer() {
        return siteIndexer;
    }

    public void setSiteIndexer(Map<Integer, Site> siteIndexer) {
        this.siteIndexer = siteIndexer;
    }

    public Map<Integer, Socket> getSocketIndexer() {
        return socketIndexer;
    }

    public void setSocketIndexer(Map<Integer, Socket> socketIndexer) {
        this.socketIndexer = socketIndexer;
    }
}
