import VotacionXYZ.EstacionVotacionPrx;
import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.Util;

import java.util.Scanner;

public class Mesa {

    public static void main(String[] args) {
        try (Communicator communicator = Util.initialize(args)) {
            ObjectAdapter adapter = communicator.createObjectAdapterWithEndpoints("mesaVotacion", "default -p 10010");

            AckServiceI ackService = new AckServiceI();
            adapter.add(ackService, Util.stringToIdentity("callback"));

            VotacionXYZ.AckServicePrx ackServicePrx = VotacionXYZ.AckServicePrx.uncheckedCast(
                adapter.createProxy(Util.stringToIdentity("callback"))
            );

            EstacionVotacionPrx proxy = EstacionVotacionPrx.uncheckedCast(
                communicator.stringToProxy("resultadosLocales:default -h localhost -p 10020")
            );

            ControladorMesaVotacion service = new ControladorMesaVotacion(proxy, ackServicePrx);

            adapter.add(service, Util.stringToIdentity("mesa"));
            adapter.activate();

            System.out.println("Mesa de votación lista en el puerto 10010");

            System.out.println("\n=== Lista de candidatos ===");
            String[] lista = service.listarCandidatos(null);
            for (String item : lista) {
                System.out.println(item);
            }

            Scanner scanner = new Scanner(System.in);
            System.out.print("\nSeleccione el número del candidato: ");
            int numero = scanner.nextInt();

            service.registrarVoto(numero, null);
            scanner.close();

            communicator.waitForShutdown();
        } catch (Exception e) {
            System.err.println("Error en la mesa: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
