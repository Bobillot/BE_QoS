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
    Integer netcatPort;
    Integer totalEFCapacity; //modifiy with SLA class if more information
    Integer usedEfCapacity;

    /**
     * Map storing a reservation and the queue index associated  with it
     */
    Map<ReservationData,Integer> queueReservationList;
    Integer tcqueueIndexCounter;

    public Site(Integer netmask, Integer edgeRouterIP, Integer totalEFCapacity, Integer netcatPort) {
        this.netmask = netmask;
        this.edgeRouterIPinside = edgeRouterIP;
        this.totalEFCapacity = totalEFCapacity;
        this.usedEfCapacity = 0;
        this.netcatPort = netcatPort;
        queueReservationList = new HashMap<>();
        tcqueueIndexCounter = 0;
    }

    public boolean isReservationPossible(Integer reqCapacity){
        Integer remainingCapacity = totalEFCapacity - usedEfCapacity;
        return remainingCapacity >= reqCapacity;
    }

    /**
     * Returns list of string for the BB to configure the router.
     * Makes the necessary changes to the Site instance state.
     * @param resData
     * @return
     */
    public List<String> makeReservation(ReservationData resData){

        ArrayList<String> result = new ArrayList<>();

        //TODO add capacity check

        usedEfCapacity += resData.getDataRateReq();
        Boolean init = resData.getSrcIP().equals(
                findIpInNetwork(resData.getSrcIP(), resData.getDstIP(), this.getNetwork()));

        //TODO implement
        //get values for generating config strings
        Integer ipConfig = init ? resData.getDstIP() : resData.getSrcIP();
        Integer portConfig = init ? resData.getDstPort() : resData.getSrcPort();

        //increment queue counter
        tcqueueIndexCounter++;

        //generate strings
        result.add(generateConfigStringTc());
        result.add(generateConfigStringIpTables(ipConfig,portConfig));
        result.add(generateConfigStringDscp(ipConfig,portConfig));


        System.out.println("made reservation for site " + getNetwork());

        return result;
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

    public Map<ReservationData,Integer> getQueueReservationList() {
        return queueReservationList;
    }

    public void setQueueReservationList(Map<ReservationData,Integer> queueReservationList) {
        this.queueReservationList = queueReservationList;
    }

    public Integer getNetcatPort() {
        return netcatPort;
    }

    public void setNetcatPort(Integer netcatPort) {
        this.netcatPort = netcatPort;
    }

    private String generateConfigStringTc(){}

    private String generateConfigStringIpTables(Integer ipDest, Integer portDest){}

    private String generateConfigStringDscp(Integer ipDest, Integer portDest){}
}
