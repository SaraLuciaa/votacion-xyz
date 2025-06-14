import java.util.Scanner;

import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.Util;

import VotacionXYZ.*;

public class Estacion {

    public static void main(String[] args) {
        try {
            Communicator communicator = Util.initialize(args);

            ObjectAdapter adapter = communicator.createObjectAdapter("EstacionAdapter");

            RmSenderPrx sender = RmSenderPrx.checkedCast(
                communicator.stringToProxy("RMService")
            );

            if (sender == null) {
                System.err.println("No se pudo obtener el proxy del servidor central.");
                return;
            }

            RmReceiverImpl receptor = new RmReceiverImpl();
            adapter.add(receptor, Util.stringToIdentity("EstacionReceiver"));

            sender.setServerProxy(RmReceiverPrx.uncheckedCast(
                adapter.createProxy(Util.stringToIdentity("EstacionReceiver"))
            ));

            adapter.activate();
            System.out.println("Estación de votación iniciada y registrada.");

            Votacion service = new Votacion(sender);
            start(service);

            communicator.waitForShutdown();

        } catch (Exception e) {
            System.err.println("Error en la estación de votación: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void start(Votacion service) {
        System.out.println("Bienvenido al sistema de votación.");
        System.out.println("Por favor, siga las instrucciones para votar.");
        System.out.println("Mesa de votación lista.");

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
