import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.Util;

import VotacionXYZ.*;
public class Estacion {

     public static void main(String[] args) {
        List<String> params = new ArrayList<>();
         try (Communicator communicator = Util.initialize(args, "estacion.cfg", params)) {

            System.out.println("[ESTACION] Communicator inicializado con archivo de configuracion: estacion.cfg");

            //System.out.println("[ESTACION] Creando instancia de RmReceiver...");
            RmReceiver receiver = new RmReceiverI();

            //System.out.println("[ESTACION] Creando adapter 'Server'...");
            ObjectAdapter adapter = communicator.createObjectAdapter("Server");

            //System.out.println("[ESTACION] Registrando RMService...");
            adapter.add(receiver, Util.stringToIdentity("RMService"));

            //System.out.println("[ESTACION] Activando adapter...");
            adapter.activate();

            //System.out.println("[ESTACION] Servidor de estación de votación escuchando en el puerto 10012...");
            //System.out.println("[ESTACION] Endpoints configurados: " + Arrays.toString(adapter.getEndpoints()));

            communicator.waitForShutdown();
            //System.out.println("[ESTACION] Shutdown recibido. Finalizando...");

        } catch (Exception e) {
            System.err.println("[ESTACION][ERROR] " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }    
}
