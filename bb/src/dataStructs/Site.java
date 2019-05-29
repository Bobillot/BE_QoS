package dataStructs;

import com.sun.org.apache.xpath.internal.operations.Bool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static utils.ipAddrConverter.ipIntegerToString;
import static utils.networkUtils.*;

public class Site {
    //site number as collection index
    private Integer netmask;
    private Integer edgeRouterIPinside;
    private String edgeRouterInterfaceInside;
    private Integer edgeRouterIPoutisde;
    private String edgeRouterInterfaceOutside;
    private Integer netcatPort;
    private Integer totalEFCapacity; //modifiy with SLA class if more information
    private Integer usedEfCapacity;


    /**
     * Map storing a reservation data and the queue index associated  with it
     */
    private Map<ReservationData, Integer> queueReservationList;
    private Integer tcqueueIndexCounter;

    public Site(Integer netmask, Integer edgeRouterIPinside, String edgeRouterInterfaceInside,
                Integer edgeRouterIPoutisde, String edgeRouterInterfaceOutside, Integer netcatPort,
                Integer totalEFCapacity) {
        this.netmask = netmask;
        this.edgeRouterIPinside = edgeRouterIPinside;
        this.edgeRouterInterfaceInside = edgeRouterInterfaceInside;
        this.edgeRouterIPoutisde = edgeRouterIPoutisde;
        this.edgeRouterInterfaceOutside = edgeRouterInterfaceOutside;
        this.netcatPort = netcatPort;
        this.totalEFCapacity = totalEFCapacity;
        this.queueReservationList = new HashMap<>();
        this.usedEfCapacity = 0;
        this.tcqueueIndexCounter = 0;
    }

    public boolean isReservationPossible(Integer reqCapacity) {
        Integer remainingCapacity = totalEFCapacity - usedEfCapacity;
        return remainingCapacity >= reqCapacity;
    }

    /**
     * Returns list of string for the BB to configure the router.
     * Makes the necessary changes to the Site instance state.
     *
     * @param resData
     * @return
     */
    public List<String> makeReservation(ReservationData resData) {

        ArrayList<String> result = new ArrayList<>();

        //TODO maybe add capacity check?

        usedEfCapacity += resData.getDataRateReq();
        Boolean init = resData.getSrcIP().equals(
                findIpInNetwork(resData.getSrcIP(), resData.getDstIP(), this.getNetwork()));

        //get values for generating config strings
        Integer ipConfig = init ? resData.getDstIP() : resData.getSrcIP();
        Integer portConfig = init ? resData.getDstPort() : resData.getSrcPort();

        //increment queue counter
        tcqueueIndexCounter++;
        queueReservationList.put(resData,tcqueueIndexCounter);

        //generate strings
        result.add(generateConfigStringTc(resData.getDataRateReq()));
        result.add(generateConfigStringAssignTc());
        result.add(generateConfigStringIpTables(ipConfig, portConfig));
        result.add(generateConfigStringDscp(ipConfig, portConfig));


        System.out.println("made reservation for site " + ipIntegerToString(getNetwork()));

        return result;
    }

    public List<String> removeReservation(ReservationData resData) {
        ArrayList<String> result = new ArrayList<>();

        usedEfCapacity -= resData.getDataRateReq();

        Boolean init = resData.getSrcIP().equals(
                findIpInNetwork(resData.getSrcIP(), resData.getDstIP(), this.getNetwork()));

        //get values for generating config strings
        Integer ipConfig = init ? resData.getDstIP() : resData.getSrcIP();
        Integer portConfig = init ? resData.getDstPort() : resData.getSrcPort();

        //remove from list
        queueReservationList.remove(resData);

        //generate commands
        result.add(removeConfigAssignTc(resData));
        result.add(removeConfigTc(resData));
        result.add(removeConfigIpTables(ipConfig,portConfig));
        result.add(removeConfigDscp(ipConfig,portConfig));

        return result;

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

    public Integer getNetcatPort() {
        return netcatPort;
    }

    public void setNetcatPort(Integer netcatPort) {
        this.netcatPort = netcatPort;
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

    public Integer getTcqueueIndexCounter() {
        return tcqueueIndexCounter;
    }

    public void setTcqueueIndexCounter(Integer tcqueueIndexCounter) {
        this.tcqueueIndexCounter = tcqueueIndexCounter;
    }

    public Map<ReservationData, Integer> getQueueReservationList() {
        return queueReservationList;
    }

    private String generateConfigStringTc(Integer dataRateReq) {
        String s = "tc class add dev " + this.getEdgeRouterInterfaceOutside() + " parent 1:1 classid 1:1"
                   + this.getTcqueueIndexCounter() + " htb rate " + dataRateReq + "kbit ceil " + dataRateReq + "kbit";
        return s;
    }

    private String generateConfigStringAssignTc() {
        String s = "tc filter add dev " + this.getEdgeRouterInterfaceOutside() + " parent 1:0 protocol ip prio 1 handle "
                   + this.getTcqueueIndexCounter() + " fw flowid 1:1" + this.getTcqueueIndexCounter();
        return s;
    }

    private String generateConfigStringIpTables(Integer ipDest,
                                                Integer portDest) {
        String s = "iptables -I POSTROUTING -t mangle -d " + ipIntegerToString(
                ipDest) + " -p udp --dport " + portDest + " -j MARK --set-mark " + this.getTcqueueIndexCounter();
        return s;
    }

    private String generateConfigStringDscp(Integer ipDest,
                                            Integer portDest) {
        String s = "iptables -I POSTROUTING -t mangle -d " + ipIntegerToString(
                ipDest) + " -p udp --dport " + portDest + " -j DSCP --set-dscp-class EF";
        return s;
    }

    private String removeConfigTc(ReservationData data)
    {
        String s = "tc filter del dev " + this.getEdgeRouterInterfaceOutside() + " parent 1:1 classid 1:1"
                + this.queueReservationList.get(data) + " htb rate " + data.dataRateReq + "kbit ceil " + data.dataRateReq + "kbit";
        return s;
    }

    private String removeConfigAssignTc(ReservationData data)
    {
        String s = "tc filter add dev " + this.getEdgeRouterInterfaceOutside() + " parent 1:0 protocol ip prio 1 handle "
                + this.queueReservationList.get(data) + " fw flowid 1:1" + this.queueReservationList.get(data);
        return s;
    }
    private String removeConfigIpTables(Integer ipDest, Integer portDest)
    {
        String s = "sudo iptables -D POSTROUTING -t mangle -d " + ipIntegerToString(
                ipDest) + "-p udp --dport " + portDest + " -j MARK --set-mark " + this.getTcqueueIndexCounter();
        return s;
    }
    private String removeConfigDscp(Integer ipDest, Integer portDest)
    {
        String s = "sudo iptables -D POSTROUTING -t mangle -d " + ipIntegerToString(
                ipDest) + "-p udp --dport " + portDest + " -j DSCP --set-dscp-class EF";
        return s;
    }
}
