package openjsip.proxy;

import java.net.*;
import java.io.*;

public class QoSRequest implements Serializable {
			private static final long serialVersionUID = 1L;

        public int srcPort;
        public int destPort;
        public InetAddress srcIP;
        public InetAddress destIP;
        public int rate;
        public boolean statusConnexion; //Deconnexion ou connexion

        public QoSRequest(int srcPort, int destPort, InetAddress srcIP, InetAddress destIP, int rate, boolean statusConnexion) {
            this.srcPort = srcPort;
						this.destPort = destPort;
						this.srcIP=srcIP;
						this.destIP=destIP;
						this.rate=rate;
						this.statusConnexion = statusConnexion;
        }
    
}
