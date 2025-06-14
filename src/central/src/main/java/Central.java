import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.Util;

import VotacionXYZ.*;
public class Central {

     public static void main(String[] args) {

         try {
            Communicator communicator = Util.initialize(args, "central.cfg");

            RmReceiver receiver = new RmReceiverI();

            ObjectAdapter adapter = communicator.createObjectAdapter("Server");
            adapter.add(receiver, Util.stringToIdentity("RMService"));
            adapter.activate();
            System.out.println("Servidor central escuchando en el puerto 10012...");

            communicator.waitForShutdown();
        } catch (Exception e) {
            System.err.println("Error en la estacion: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }  
}