import java.util.Scanner;
import java.util.UUID;

import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.ObjectPrx;
import com.zeroc.Ice.Util;

import VotacionXYZ.*;
import impl.MesaServiceI;

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

            // 5.Crear el servicio mesa
            MesaServiceI mesaService = new MesaServiceI();
            adapter.add(mesaService, Util.stringToIdentity("MesaService")); 

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
        Scanner scanner = new Scanner(System.in);
        System.out.println("Bienvenido al sistema de votación.");
        System.out.println("Por favor, ingrese el numero de identificacion de la estacion de votacion.");
        String idEstacion = scanner.next();


        
        DatosMesa data = distributor.sendData(idEstacion);
        Ciudadano[] ciudadanos = data.ciudadanos;
        Candidato[] candidatos = data.candidatos;

       


        scanner.close();
    }

     public void registrarVoto(long candidatoIndex) {
        if (candidatoIndex < 1 || candidatoIndex > candidatos.size()) {
            System.out.println("Indice de candidato fuera de rango: " + candidatoIndex);
            throw new IllegalArgumentException("Indice de candidato fuera de rango");
        }

        String nombreCandidato = getCandidatoPorNumero((int) candidatoIndex);
        if (nombreCandidato == null) {
            System.out.println("Candidato inválido: " + candidatoIndex);
            throw new IllegalArgumentException("Candidato no válido.");
        }

        Voto voto = new Voto(nombreCandidato);
        String uuid = UUID.randomUUID().toString();
        Message msg = new Message(uuid, voto);

        rmSender.send(msg);
    }
}
