import java.util.Scanner;

import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.ObjectPrx;
import com.zeroc.Ice.Util;

import VotacionXYZ.*;

public class Estacion {

    public static void main(String[] args) {
        try {
            // 1. Inicializar ICE con archivo de configuración
            Communicator communicator = Util.initialize(args, "estacion.cfg");

            // 2. Crear el adaptador local (no registramos ningún objeto, pero es obligatorio)
            ObjectAdapter adapter = communicator.createObjectAdapter("EstacionAdapter");

            // 3. Obtener el proxy al RMSender (expuesto localmente por el Reliable en el mismo nodo)
            String proxyString = communicator.getProperties().getProperty("RMSender");

            RmSenderPrx sender = RmSenderPrx.checkedCast(
                communicator.stringToProxy(proxyString)
            );

            if (sender == null) {
                System.err.println("[ESTACION] No se pudo obtener el proxy del RMSender.");
                return;
            }

             // 4. Obtener proxy al servicio DataDistribution (usando replica-group de ICEGrid)
            ObjectPrx base = communicator.stringToProxy("DataDistributor");
            DataDistributionPrx distributor = DataDistributionPrx.checkedCast(base);

            if (distributor == null) {
                System.err.println("[ESTACION] No se pudo obtener el proxy del DataDistributor.");
                return;
            }

            // 5. Activar adaptador local
            adapter.activate();
            System.out.println("[ESTACION] Estación de votación iniciada y conectada al RMSender.");

            // 6. Iniciar el flujo de votación
            Votacion service = new Votacion(sender);
            start(service, distributor);

            communicator.waitForShutdown();

        } catch (Exception e) {
            System.err.println("Error en la estación de votación: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void start(Votacion service, DataDistributionPrx distributor) {
        System.out.println("Bienvenido al sistema de votación.");
        System.out.println("Por favor, siga las instrucciones para votar.");
        System.out.println("Mesa de votación lista.");

        // Llamar al servicio de distribución de datos al iniciar
        
        distributor.sendData("mesa-123");

        Scanner scanner = new Scanner(System.in);
        boolean votando = true;

        while (votando) {
            System.out.println("\n=== Lista de candidatos ===");
            String[] lista = service.listarCandidatos(null);
            for (String item : lista) {
                System.out.println(item);
            }

            System.out.print("\nSeleccione el número del candidato: ");
            int numero = scanner.nextInt();

            System.out.print("¿Desea registrar el voto? (s/n): ");
            String respuesta = scanner.next();

            if (respuesta.equalsIgnoreCase("n")) {
                System.out.println("Voto no registrado.");
            } else {
                service.registrarVoto(numero);
                System.out.println("Voto registrado exitosamente.");
            }
        }

        scanner.close();
    }
}
