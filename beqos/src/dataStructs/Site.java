package dataStructs;

import java.util.List;

public class Site {
    //site number as collection index
    Integer netmask;
    Integer edgeRouterIPinside;
    Integer edgeRouterIPoutisde;
    Integer totalEFCapacity; //modifiy with SLA class if more information
    Integer usedEfCapacity;

    /**
     * netmask associated with the site
     * used to check if an ip is from a given site
     */


    List<ReservationData> queueReservationList;

    public Site(Integer netmask, Integer edgeRouterIP, Integer totalEFCapacity) {
        this.netmask = netmask;
        this.edgeRouterIPinside = edgeRouterIP;
        this.totalEFCapacity = totalEFCapacity;
        this.usedEfCapacity = 0;
    }

    public boolean isReservationPossible(Integer reqCapacity){
        Integer remainingCapacity = totalEFCapacity - usedEfCapacity;
        return remainingCapacity >= reqCapacity;
    }

    public void makeReservation(ReservationData resData){
        //TODO add capacity check

        //TODO implement check if initiator or dest

        //TODO implement
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
}
