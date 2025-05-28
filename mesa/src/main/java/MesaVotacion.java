import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.Util;

public class MesaVotacion {
    public static void main(String[] args) {
        try (Communicator communicator = Util.initialize(args)) {
            ObjectAdapter adapter = communicator.createObjectAdapterWithEndpoints("mesaVotacion", "default -p 10010");

            ControladorMesaVotacion service = new ControladorMesaVotacion();

            adapter.add(service, Util.stringToIdentity("mesa"));

            adapter.activate();

            System.out.println("Servidor de mesa de votación escuchando en el puerto 10010...");

            new Thread(service).start(); 

            communicator.waitForShutdown();
        } catch (Exception e) {
            System.err.println("Error en la mesa: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
