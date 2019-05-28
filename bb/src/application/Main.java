package application;

import dataStructs.ReservationData;
import dataStructs.Site;
import exceptions.EFCapacityReached;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import static utils.ipAddrConverter.*;

public class Main {

    public static void main(String[] args) {
        //setup all sites
        Site site1 = new Site(ipStringToInteger("255.255.255.0"),
                              ipStringToInteger("192.168.10.1"),
                              "eth0",
                              ipStringToInteger("202.205.205.1"),
                              "eth1",
                              4000, 3000);
        Site site2 = new Site(ipStringToInteger("255.255.255.0"),
                              ipStringToInteger("192.168.20.1"),
                              "eth0",
                              ipStringToInteger("206.206.206.1"),
                              "eth1",
                              4000, 3000);
        //        Site site1 = new Site(ipAddrConverter.ipStringToInteger("255.255.255.0"),
        //                              ipAddrConverter.ipStringToInteger("192.168.10.1"), 3000, 5000);
        //        Site site2 = new Site(ipAddrConverter.ipStringToInteger("255.255.255.0"),
        //                              ipAddrConverter.ipStringToInteger("192.168.20.1"), 3000, 5000);
        BandwidthBroker BB = BandwidthBroker.getInstance();

        BB.addSite(site1, 1);
        BB.addSite(site2, 2);

        //wait for requests from the phones and make reservation accordingly
        try {
            ServerSocket socket = new ServerSocket(4000);
            Socket client = null;

            // Accept client and create thread to handle com
            while (true) {
                //TODO : make different class than reservation data to differentiate requests for res and unres
                client = socket.accept();
                ObjectInputStream ois = new ObjectInputStream(client.getInputStream());
                ReservationData resData = readFromStream(ois);

                //TODO test if request for connection or disconnection
                try {
                    BB.makeReservation(resData);
                    //TODO send accept
                } catch (EFCapacityReached efCapacityReached) {
                    //TODO send refuse
                }
                //TODO remove reservation if request for disconnect
                BB.removeReservation(resData);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        System.out.println("Done");
    }

    /**
     * Reads reservation data from a serialized ReservationData
     *
     * @param ois
     * @return
     */
    private static ReservationData readFromStream(ObjectInputStream ois) {
        ReservationData resData = null;
        try {
            resData = (ReservationData) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        return resData;
    }
}
