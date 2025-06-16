import java.util.Scanner;

import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectPrx;
import com.zeroc.Ice.Util;

import VotacionXYZ.Candidato;
import VotacionXYZ.VoteStationPrx;

public class Mesa {

    public static void main(String[] args) {
        try {
            Communicator communicator = Util.initialize(args, "mesa.cfg");
            // Obtener proxy remoto al servicio VoteStation (en la estación)
            String proxyString = communicator.getProperties().getProperty("VoteStation");

            ObjectPrx base = communicator.stringToProxy(proxyString);
            VoteStationPrx voteStation = VoteStationPrx.checkedCast(base);

            if (voteStation == null) {
                System.err.println("[MESA] Proxy inválido para VoteStation.");
                return;
            }

            Scanner scanner = new Scanner(System.in);
            int mesaId = pedirMesaId(scanner);

            while (true) {
                System.out.print("\nDocumento del ciudadano (o 'salir'): ");
                String documento = scanner.nextLine().trim();
                if (documento.equalsIgnoreCase("salir")) break;

                int resultado = voteStation.consultarCiudadanoPorId(documento, mesaId);
                switch (resultado) {
                    case 0:
                        System.out.println("Ciudadano habilitado para votar.");
                        votar(documento, voteStation, scanner);
                        break;
                    case 1:
                        System.out.println("Ciudadano pertenece a otra mesa.");
                        break;
                    case 2:
                        System.out.println("Ciudadano ya ha votado.");
                        break;
                    case 3:
                        System.out.println("Documento no registrado.");
                        break;
                    default:
                        System.out.println("Error desconocido.");
                        break;
                }
            }

            scanner.close();
        } catch (Exception e) {
            System.err.println("Error en la mesa de votación: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static int pedirMesaId(Scanner scanner) {
        System.out.print("Ingrese el ID numérico de esta mesa de votación: ");
        return Integer.parseInt(scanner.nextLine().trim());
    }

    private static void votar(String documento, VoteStationPrx voteStation, Scanner scanner) {
        Candidato[] candidatos = voteStation.obtenerCandidatos();

        System.out.println("\n=== Candidatos ===");
        for (int i = 0; i < candidatos.length; i++) {
            System.out.printf("[%d] %s (%s)%n", i + 1, candidatos[i].nombre, candidatos[i].nombrePartido);
        }

        System.out.print("Seleccione el número del candidato: ");
        int opcion = Integer.parseInt(scanner.nextLine().trim());

        if (opcion < 1 || opcion > candidatos.length) {
            System.out.println("Opción no válida.");
            return;
        }

        voteStation.registrarVoto(opcion, documento);
        System.out.println("Voto registrado exitosamente.");
    }
}