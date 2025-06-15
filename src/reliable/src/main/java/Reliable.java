import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.ObjectPrx;
import com.zeroc.Ice.Util;

import VotacionXYZ.AckService;
import VotacionXYZ.AckServicePrx;
import VotacionXYZ.RmReceiverPrx;
import VotacionXYZ.RmSender;
import services.AckServiceI;
import services.RmSenderI;
import utils.GuardadoVotos;

public class Reliable {
    public static void main(String[] args) {
        try {
            // 1. Inicializar comunicador con IceGrid
            Communicator communicator = Util.initialize(args, "reliable.cfg");

            // 2. Crear el objeto de persistencia (para reintentos)
            GuardadoVotos guardado = new GuardadoVotos();

            // 3. Crear y publicar el servicio de ACK
            AckService ackService = new AckServiceI();
            ObjectAdapter adapter = communicator.createObjectAdapter("RMSenderAdapter");

            ObjectPrx ackPrx = adapter.add(ackService, Util.stringToIdentity("AckCallback"));
            AckServicePrx ackServicePrx = AckServicePrx.uncheckedCast(ackPrx);

            // 4. Obtener proxy del RMService remoto (expuesto por el servidor central)
            RmReceiverPrx receptor = RmReceiverPrx.checkedCast(
                communicator.stringToProxy("RMService") // este es el nombre lógico, registrado en RMGroup
            );

            if (receptor == null) {
                System.err.println("[RELIABLE] No se pudo obtener el proxy de RMService desde el broker.");
                return;
            }

            // 5. Crear y exponer el RMSender (lo consumen las estaciones)
            RmSender sender = new RmSenderI(ackServicePrx, guardado);
            sender.setServerProxy(receptor, null); // conexión al central

            adapter.add(sender, Util.stringToIdentity("RMSender")); // este es el nombre que verá la estación

            // 6. Activar el adaptador
            adapter.activate();
            System.out.println("[RELIABLE] RMSender listo y conectado a RMService.");

            communicator.waitForShutdown();

        } catch (Exception e) {
            System.err.println("[RELIABLE] Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}