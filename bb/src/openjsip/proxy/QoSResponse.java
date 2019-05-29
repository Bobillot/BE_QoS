package openjsip.proxy;

import java.net.*;
import java.io.*;

public class QoSResponse implements Serializable {
			private static final long serialVersionUID = 2L;

        public boolean accept;

        public QoSResponse(boolean b) {
            accept = b;
        }


}
