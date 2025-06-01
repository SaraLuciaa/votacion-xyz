import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.ObjectPrx;
import com.zeroc.Ice.Util;

import services.*;
import utils.GuardadoVotos;
import VotacionXYZ.*;

public class Reliable {
    public static void main(String[] args) {
        try {
            //System.out.println("[RELIABLE] Iniciando communicator...");
            Communicator communicator = Util.initialize(args, "reliable.cfg");

            //System.out.println("[RELIABLE] Inicializando servicios...");
            GuardadoVotos guardado = new GuardadoVotos();
            AckService ackService = new AckServiceI();

            // Crear adaptador para RMService (que incluye RMSender y AckService)
            //System.out.println("[RELIABLE] Creando adapter RMService...");
            ObjectAdapter rmAdapter = communicator.createObjectAdapter("RMService");

            //System.out.println("[RELIABLE] Registrando AckService...");
            ObjectPrx ackPrx = rmAdapter.add(ackService, Util.stringToIdentity("AckService"));
            AckServicePrx ackServicePrx = AckServicePrx.uncheckedCast(ackPrx);

            //System.out.println("[RELIABLE] Registrando RmSender...");
            RmSender sender = new RmSenderI(ackServicePrx, guardado);
            rmAdapter.add(sender, Util.stringToIdentity("RMSender"));

            //System.out.println("[RELIABLE] Activando adapter RMService...");
            rmAdapter.activate();

            // Crear adaptador separado para AckService si es necesario
            //System.out.println("[RELIABLE] Creando adapter AckService...");
            ObjectAdapter ackAdapter = communicator.createObjectAdapter("AckService");
            ackAdapter.add(ackService, Util.stringToIdentity("AckService"));
            
            //System.out.println("[RELIABLE] Activando adapter AckService...");
            ackAdapter.activate();

            //System.out.println("[RELIABLE] Adaptadores activados:");
            //System.out.println("  RMService endpoints: " + Arrays.toString(rmAdapter.getEndpoints()));
            System.out.println("  AckService endpoints: " + Arrays.toString(ackAdapter.getEndpoints()));

            System.out.println("[RELIABLE] Servicios disponibles:");
            System.out.println("  - RMSender en: RMSender:tcp -h localhost -p 10011");
            System.out.println("  - AckService en: AckService:tcp -h localhost -p 10013");

            //System.out.println("[RELIABLE] Todo listo. Esperando conexiones...");
            communicator.waitForShutdown();

            //System.out.println("[RELIABLE] Shutdown recibido. Cerrando...");

        } catch (Exception e) {
            System.err.println("[RELIABLE][ERROR] " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
