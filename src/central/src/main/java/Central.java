import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.Util;

import VotacionXYZ.*;
import impl.RmReceiverI;
import impl.QueryStationI;

public class Central {

    public static void main(String[] args) {
        try {
            Communicator communicator = Util.initialize(args, "central.cfg");

            ObjectAdapter adapter = communicator.createObjectAdapter("Server");

            RmReceiver receiver = new RmReceiverI();
            adapter.add(receiver, Util.stringToIdentity("RMService"));

            queryStation query = new QueryStationI(communicator);
            adapter.add(query, Util.stringToIdentity("QueryService"));

            adapter.activate();
            System.out.println("Servidor central escuchando en el puerto 10012...");

            communicator.waitForShutdown();

        } catch (Exception e) {
            System.err.println("Error en el servidor central: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}