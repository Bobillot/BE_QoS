package application;

import dataStructs.QoSRequest;
import dataStructs.QoSResponse;
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
                              ipStringToInteger("192.168.10.1"),
                              "eth0",
                              ipStringToInteger("202.205.205.1"),
                              "eth1",
                              4444, 3000);
        Site site2 = new Site(ipStringToInteger("255.255.255.0"),
                              ipStringToInteger("192.168.20.1"),
                              "eth0",
                              ipStringToInteger("206.206.206.1"),
                              "eth1",
                              4444, 3000);
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
                client = socket.accept();
                ObjectInputStream ois = new ObjectInputStream(client.getInputStream());
                QoSRequest qoSRequest = readFromStream(ois);
                ReservationData resData = new ReservationData(qoSRequest);

                //check if co or deco request
                if (qoSRequest.statusConnexion) {
                    ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
                    QoSResponse resp;
                    try {

                        BB.makeReservation(resData);
                        resp = new QoSResponse(true);


                    } catch (EFCapacityReached efCapacityReached) {
                        resp = new QoSResponse(false);
                    }

                    //write response and close stream
                    oos.writeObject(resp);
                    oos.flush();
                    oos.close();

                }
                else {
                    BB.removeReservation(resData);
                }
                client.close();
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
