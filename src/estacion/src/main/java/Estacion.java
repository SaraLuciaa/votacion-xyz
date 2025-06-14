import java.util.Scanner;

import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.Util;

import VotacionXYZ.*;

public class Estacion {

    public static void main(String[] args) {

        try  {
            Communicator communicator = Util.initialize(args, "estacion.cfg");

            RmSenderPrx sender = RmSenderPrx.checkedCast(
                communicator.propertyToProxy("Estacion.Sender")
            );
            RmReceiverPrx receiver = RmReceiverPrx.uncheckedCast(
                communicator.propertyToProxy("Estacion.Receiver")
            );
            System.out.println("Servidor de estacion de votacion escuchando en el puerto 10012...");

            sender.setServerProxy(receiver);
            
            Votacion service = new Votacion(sender);
            start(service);
        
            communicator.waitForShutdown();
        } catch (Exception e) {
            System.err.println("Error en la mesa: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void start(Votacion service) {
        System.out.println("Bienvenido al sistema de votacion.");
        System.out.println("Por favor, siga las instrucciones para votar.");
        System.out.println("Mesa de votacion lista en el puerto 10010");

        boolean votando = true;
        Scanner scanner = new Scanner(System.in);

        while (votando){
            System.out.println("\n=== Lista de candidatos ===");
            String[] lista = service.listarCandidatos(null);
            for (String item : lista) {
                System.out.println(item);
            }
            System.out.print("\nSeleccione el numero del candidato: ");
            int numero = scanner.nextInt();
            System.out.println("Desea registrar el voto? (s/n)");
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
