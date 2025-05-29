import VotacionXYZ.EstacionVotacionPrx;
import ackService.AckServiceI;

import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Mesa {

    public static void main(String[] args) {

        try  {
            List<String> extPar = new ArrayList<>();
            Communicator communicator = Util.initialize(args, "mesaVotacion.cfg", extPar);

            ObjectAdapter adapter = communicator.createObjectAdapter("mesaVotacion");

            AckServiceI ackService = new AckServiceI();
            adapter.add(ackService, Util.stringToIdentity("callback"));

            VotacionXYZ.AckServicePrx ackServicePrx = VotacionXYZ.AckServicePrx.uncheckedCast(
                adapter.createProxy(Util.stringToIdentity("callback"))
            );

            EstacionVotacionPrx proxy = EstacionVotacionPrx.uncheckedCast(
                communicator.propertyToProxy("estacionVotacion.Proxy")
            );


            ControladorMesaVotacion service = new ControladorMesaVotacion(proxy, ackServicePrx);

            adapter.add(service, Util.stringToIdentity("mesa"));
            adapter.activate();
            
            service.reenviarVotosPendientes();
            service.iniciarReintentoPeriodico();
            iniciarPrograma(service);
        
            communicator.waitForShutdown();
        } catch (Exception e) {
            System.err.println("Error en la mesa: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void iniciarPrograma(ControladorMesaVotacion service) {
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
                service.registrarVoto(numero, null);
                System.out.println("Voto registrado exitosamente.");
            }
            System.out.println("Desea registrar otro voto? (s/n)");
            String respuesta_salida = scanner.next();
            if(respuesta_salida.equalsIgnoreCase("n")) {
                votando = false;
                System.out.println("Gracias por participar en la votacion.");
            } else {
                System.out.println("Continuando con el registro de votos...");
            }

        }
        scanner.close();

    }
}
