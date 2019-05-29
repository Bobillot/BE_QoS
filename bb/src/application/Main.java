package application;

import openjsip.proxy.QoSRequest;
import openjsip.proxy.QoSResponse;
import dataStructs.ReservationData;
import dataStructs.Site;
import exceptions.EFCapacityReached;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import static utils.ipAddrConverter.*;

public class Main {

    public static void main(String[] args) {
        //setup all sites
        Site site1 = new Site(ipStringToInteger("255.255.255.0"),
                              ipStringToInteger("192.168.1.1"),
                              "eth1",
                              ipStringToInteger("193.168.1.1"),
                              "eth0",
                              4444, 3000);
        Site site2 = new Site(ipStringToInteger("255.255.255.0"),
                              ipStringToInteger("192.168.2.1"),
                              "eth2",
                              ipStringToInteger("193.168.2.1"),
                              "eth0",
                              4444, 3000);
        Site site3 = new Site(ipStringToInteger("255.255.255.0"),
                              ipStringToInteger("192.168.3.1"),
                              "eth2",
                              ipStringToInteger("193.168.3.1"),
                              "eth0",
                              4444, 3000);
        BandwidthBroker BB = BandwidthBroker.getInstance();

        BB.addSite(site1, 1);
        BB.addSite(site2, 2);
        BB.addSite(site3, 3);


//        //TODO Testing, to remove
//        ReservationData reservationData = new ReservationData(5000,6000,
//                                                                                  ipStringToInteger("192.168.10.5"),
//                                                                                  ipStringToInteger("192.168.10.6"),
//                                                                                  64);
////        try {
//            BB.makeReservation(reservationData);
//            System.out.println("Statut sites :\n"+site1 + "\n" + site2);
//            BB.removeReservation(reservationData);
//            System.out.println("Statut sites :\n"+site1 + "\n" + site2);
//        } catch (EFCapacityReached efCapacityReached) {
//            efCapacityReached.printStackTrace();
//        }


        //wait for requests from the phones and make reservation accordingly
        try {
            ServerSocket socket = new ServerSocket(4000);
            Socket client = null;

            // Accept client and create thread to handle com
            while (true) {
                client = socket.accept();
                ObjectInputStream ois = new ObjectInputStream(client.getInputStream());
                QoSRequest qoSRequest = readFromStream(ois);
                System.out.println("Received request : " + qoSRequest);
                ReservationData resData = new ReservationData(qoSRequest);
                System.out.println("Built resData");

                //check if co or deco request
                if (qoSRequest.statusConnexion) {
                    ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
                    QoSResponse resp;
                    try {

                        System.out.println("Making reservation");
                        BB.makeReservation(resData);
                        resp = new QoSResponse(true);


                    } catch (EFCapacityReached efCapacityReached) {
                        resp = new QoSResponse(false);
                    }

                    System.out.println("req status" + resp.accept);
                    //write response and close stream
                    oos.writeObject(resp);
                    oos.flush();
                    oos.close();
                    System.out.println("Object sent: " + resp);

                }
                else {
                    BB.removeReservation(resData);
                }
                client.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Print status of all sites
        System.out.println("Status site :");
        for (Site s: BB.getSiteIndexer().values()) {
            System.out.println(s);
        }


        System.out.println("Done");
    }

    /**
     * Reads reservation data from a serialized ReservationData
     *
     * @param ois
     * @return
     */
    private static QoSRequest readFromStream(ObjectInputStream ois) {
        QoSRequest qoSRequest = null;
        try {
            qoSRequest = (QoSRequest) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        return qoSRequest;
    }
}
