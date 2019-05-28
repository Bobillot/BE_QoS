package dataStructs;

import java.io.Serializable;
import java.net.InetAddress;

public class QoSRequest implements Serializable {
    public int srcPort;
    public int destPort;
    public InetAddress srcIP;
    public InetAddress destIP;
    public int rate;
    public boolean statusConnexion; //Deconnexion ou connexion
}
