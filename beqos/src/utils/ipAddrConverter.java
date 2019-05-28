package utils;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteOrder;

public class ipAddrConverter {
    public static Integer ipStringToInteger(String ipStr){
        // Parse IP parts into an int array
        int[] ip = new int[4];
        String[] parts = ipStr.split("\\.");

        for (int i = 0; i < 4; i++) {
            ip[i] = Integer.parseInt(parts[i]);
        }

        Integer result = 0;
        for (int i = 0; i < 4; i++) {
            result += ip[i] << (24 - (8 * i));
        }

        return result;
    }

    public static String ipIntegerToString(int ipInt) {

        String ipString = String.format(
                "%d.%d.%d.%d",
                (ipInt >> 24 & 0xff),
                (ipInt >> 16 & 0xff),
                (ipInt >> 8 & 0xff),
                (ipInt & 0xff));

        return ipString;
    }
}
