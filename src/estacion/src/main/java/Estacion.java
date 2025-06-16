import java.io.File;
import java.util.Scanner;

import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.ObjectPrx;
import com.zeroc.Ice.Util;

import VotacionXYZ.Candidato;
import VotacionXYZ.Ciudadano;
import VotacionXYZ.DataDistributionPrx;
import VotacionXYZ.DatosMesa;
import VotacionXYZ.RmSenderPrx;
import impl.VoteStationI;
import utils.Loader;

public class Estacion {
    private static RmSenderPrx rmSender;

    public static void main(String[] args) {
        try {
            Communicator communicator = Util.initialize(args, "estacion.cfg");

            ObjectAdapter adapter = communicator.createObjectAdapter("EstacionAdapter");

            // Proxy RMSender
            String proxyString = communicator.getProperties().getProperty("RMSender");
            rmSender = RmSenderPrx.checkedCast(communicator.stringToProxy(proxyString));
            if (rmSender == null) {
                System.err.println("[ESTACION] No se pudo obtener el proxy del RMSender.");
                return;
            }

            // Proxy DataDistributor (replica-group de ICEGrid)
            ObjectPrx base = communicator.stringToProxy("DataDistributor");
            DataDistributionPrx distributor = DataDistributionPrx.checkedCast(base);
            if (distributor == null) {
                System.err.println("[ESTACION] No se pudo obtener el proxy del DataDistributor.");
                return;
            }

            // Registrar la estación de votación
            VoteStationI voteStation = new VoteStationI(rmSender);
            adapter.add(voteStation, Util.stringToIdentity("VoteStation"));
            adapter.activate();

            System.out.println("[ESTACION] Estación de votación iniciada.");

            cargarDatosDesdeDistribuidor(distributor);
            // mostrarMenuVotacion();

            communicator.waitForShutdown();

        } catch (Exception e) {
            System.err.println("Error en la estación de votación: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void cargarDatosDesdeDistribuidor(DataDistributionPrx distributor) {
        File fileCandidatos = new File("estacion/src/main/resources/candidatos.json");
        File fileCiudadanos = new File("estacion/src/main/resources/ciudadanos.json");

        if (fileCandidatos.exists() && fileCiudadanos.exists()) {
            System.out.println("Los archivos locales ya existen. No se solicitarán datos al distribuidor.");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese el ID de la estación de votación: ");
        String idEstacion = scanner.nextLine();

        DatosMesa data = distributor.sendData(idEstacion);

        Loader<Candidato> candidatosLoader = new Loader<>("estacion/src/main/resources/candidatos.json", Candidato.class);
        Loader<Ciudadano> ciudadanosLoader = new Loader<>("estacion/src/main/resources/ciudadanos.json", Ciudadano.class);

        for (Candidato c : data.candidatos) candidatosLoader.add(c);
        for (Ciudadano c : data.ciudadanos) ciudadanosLoader.add(c);

        System.out.println("Candidatos y ciudadanos cargados desde el distribuidor.\n");
    }


    // private static void mostrarMenuVotacion() {
    //     Scanner scanner = new Scanner(System.in);
    //     while (true) {
    //         System.out.println("\n=== MENU DE VOTACIÓN ===");
    //         for (int i = 0; i < candidatos.size(); i++) {
    //             System.out.printf("[%d] %s%n", i + 1, candidatos.get(i).nombre);
    //         }
    //         System.out.print("Seleccione el número del candidato (0 para salir): ");

    //         long index = scanner.nextLong();
    //         if (index == 0) break;

    //         try {
    //             registrarVoto(index);
    //         } catch (IllegalArgumentException e) {
    //             System.out.println("Error: " + e.getMessage());
    //         }
    //     }
    //     scanner.close();
    // }

    // private static void registrarVoto(long candidatoIndex) {
    //     if (candidatoIndex < 1 || candidatoIndex > candidatos.size()) {
    //         throw new IllegalArgumentException("Índice de candidato fuera de rango.");
    //     }

    //     String nombreCandidato = candidatos.get((int) candidatoIndex - 1).nombre;
    //     Voto voto = new Voto(nombreCandidato);
    //     String uuid = UUID.randomUUID().toString();
    //     Message msg = new Message(uuid, voto);

    //     rmSender.send(msg);
    //     System.out.println("Voto enviado correctamente para: " + nombreCandidato);
    // }
}