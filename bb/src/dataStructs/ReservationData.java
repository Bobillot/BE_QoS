package dataStructs;

import java.io.Serializable;
import java.util.Objects;

public class ReservationData implements Serializable {
    Integer srcPort, dstPort;
    Integer srcIP, dstIP;
    Integer dataRateReq;

    /**
     *
     * @param srcPort
     * @param dstPort
     * @param srcIP
     * @param dstIP
     * @param dataRateReq
     */
    public ReservationData(Integer srcPort, Integer dstPort, Integer srcIP, Integer dstIP, Integer dataRateReq) {


            this.srcPort = srcPort;
            this.dstPort = dstPort;
            this.srcIP = srcIP;
            this.dstIP = dstIP;

        this.dataRateReq = dataRateReq;
    }

    public ReservationData(ReservationData resDat) {
        this(resDat.srcPort, resDat.dstPort, resDat.srcIP, resDat.dstIP, resDat.dataRateReq);
    }

    /**
     * Override equals to not take into account dataRateReq
     *
     * @param obj
     * @return
     */
    public boolean equals(Object obj) {
        if (obj == this)
            return true;

        if (!(obj instanceof ReservationData))
            return false;

        ReservationData rd = (ReservationData) obj;
        return srcPort.equals(rd.srcPort) && dstPort.equals(rd.dstPort)
                   && srcIP.equals(rd.srcIP) && dstIP.equals(rd.dstIP);
    }

    @Override
    public int hashCode() {
        return Objects.hash(srcPort, dstPort, srcIP, dstIP);
    }

    public Integer getSrcPort() {
        return srcPort;
    }

    public void setSrcPort(Integer srcPort) {
        this.srcPort = srcPort;
    }

    public Integer getDstPort() {
        return dstPort;
    }

    public void setDstPort(Integer dstPort) {
        this.dstPort = dstPort;
    }

    public Integer getSrcIP() {
        return srcIP;
    }

    public void setSrcIP(Integer srcIP) {
        this.srcIP = srcIP;
    }

    public Integer getDstIP() {
        return dstIP;
    }

    public void setDstIP(Integer dstIP) {
        this.dstIP = dstIP;
    }

    public Integer getDataRateReq() {
        return dataRateReq;
    }

    public void setDataRateReq(Integer dataRateReq) {
        this.dataRateReq = dataRateReq;
    }
}
