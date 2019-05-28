package utils;

public class networkUtils {

    /**
     * Returns ip which fits the most into the network
     * @param ip1
     * @param ip2
     * @param network
     * @return
     */
    static public Integer findIpInNetwork(Integer ip1, Integer ip2, Integer network){
            return Integer.compareUnsigned(ip1 & network, ip2 & network) > 0 ? ip1 : ip2;
    }
}
