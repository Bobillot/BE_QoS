package dataStructs;

import com.sun.org.apache.xpath.internal.operations.Bool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static utils.networkUtils.*;

public class Site {
    //site number as collection index
    Integer netmask;
    Integer edgeRouterIPinside;
    String edgeRouterInterfaceInside;
    Integer edgeRouterIPoutisde;
    String edgeRouterInterfaceOutside;
    Integer totalEFCapacity; //modifiy with SLA class if more information
    Integer usedEfCapacity;

    /**
     * Map storing a reservation and the queue index associated  with it
     */
    Map<ReservationData,Integer> queueReservationList;
    Integer tcqueueIndexCounter;

    public Site(Integer netmask, Integer edgeRouterIP, Integer totalEFCapacity) {
        this.netmask = netmask;
        this.edgeRouterIPinside = edgeRouterIP;
        this.totalEFCapacity = totalEFCapacity;
        this.usedEfCapacity = 0;
        queueReservationList = new HashMap<>();
        tcqueueIndexCounter = 0;
    }

    public boolean isReservationPossible(Integer reqCapacity){
        Integer remainingCapacity = totalEFCapacity - usedEfCapacity;
        return remainingCapacity >= reqCapacity;
    }

    public void makeReservation(ReservationData resData){
        //TODO add capacity check
        
        Boolean init = resData.getSrcIP() == findIpInNetwork(resData.getSrcIP(),resData.getDstIP(),this.getNetwork());

        //TODO implement
        //get values for generating config strings
        Integer ipConfig = init ? resData.getDstIP() : resData.getSrcIP();
        Integer portConfig = init ? resData.getDstPort() : resData.getSrcPort();

        String

        System.out.println("made reservation for site " + getNetwork());
    }

    public void removeReservation(ReservationData resData){
        //TODO implement
    }

    public Integer getNetwork() {
        Integer network = edgeRouterIPinside & netmask;
        return network;
    }

    public Integer getNetmask() {
        return netmask;
    }

    public void setNetmask(Integer netmask) {
        this.netmask = netmask;
    }

    public Integer getEdgeRouterIPinside() {
        return edgeRouterIPinside;
    }

    public void setEdgeRouterIPinside(Integer edgeRouterIPinside) {
        this.edgeRouterIPinside = edgeRouterIPinside;
    }

    public Integer getTotalEFCapacity() {
        return totalEFCapacity;
    }

    public void setTotalEFCapacity(Integer totalEFCapacity) {
        this.totalEFCapacity = totalEFCapacity;
    }

    public Integer getUsedEfCapacity() {
        return usedEfCapacity;
    }

    public void setUsedEfCapacity(Integer usedEfCapacity) {
        this.usedEfCapacity = usedEfCapacity;
    }

    public List<ReservationData> getQueueReservationList() {
        return queueReservationList;
    }

    public void setQueueReservationList(List<ReservationData> queueReservationList) {
        this.queueReservationList = queueReservationList;
    }

    public String getEdgeRouterInterfaceInside() {
        return edgeRouterInterfaceInside;
    }

    public void setEdgeRouterInterfaceInside(String edgeRouterInterfaceInside) {
        this.edgeRouterInterfaceInside = edgeRouterInterfaceInside;
    }

    public Integer getEdgeRouterIPoutisde() {
        return edgeRouterIPoutisde;
    }

    public void setEdgeRouterIPoutisde(Integer edgeRouterIPoutisde) {
        this.edgeRouterIPoutisde = edgeRouterIPoutisde;
    }

    public String getEdgeRouterInterfaceOutside() {
        return edgeRouterInterfaceOutside;
    }

    public void setEdgeRouterInterfaceOutside(String edgeRouterInterfaceOutside) {
        this.edgeRouterInterfaceOutside = edgeRouterInterfaceOutside;
    }

    private String generateConfigStringTc()
    { //TODO : convertir en ligne de commande Netcat
        println("tc filter add dev " + this.getEdgeRouterInterfaceOutside() + "parent 1:1 classid ")
    }
    private String generateConfigStringIpTables(Integer ipDest, Integer portDest)
    {//TODO : convertir en ligne de commande Netcat

    }
    private String generateConfigStringDscp(Integer ipDest, Integer portDest)
    {//TODO : convertir en ligne de commande Netcat

    }
}
