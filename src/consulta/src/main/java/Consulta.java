import java.util.Scanner;

import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectPrx;
import com.zeroc.Ice.Util;

import VotacionXYZ.queryStation;
import VotacionXYZ.queryStationPrx;

public class Consulta {

    public static void main(String[] args) {
        try (Communicator communicator = Util.initialize(args, "consulta.cfg")) {

            // Obtenemos el proxy del servicio remoto
            ObjectPrx base = communicator.stringToProxy("QueryService");
            queryStationPrx query = queryStationPrx.checkedCast(base);

            if (query == null) {
                System.err.println("Proxy inválido para QueryService");
                return;
            }

            consultar(query);
            
        } catch (Exception e) {
            System.err.println("Error en cliente de consulta: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void consultar(queryStationPrx query) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("=== CONSULTA DE PUESTO DE VOTACIÓN ===");
        System.out.println("Escriba el número de documento o 'salir' para terminar.");

        while (true) {
            System.out.print("\nDocumento: ");
            String documento = scanner.nextLine().trim();

            if (documento.equalsIgnoreCase("salir")) {
                System.out.println("Saliendo del sistema...");
                break;
            }

            try {
                String resultado = query.query(documento);
                System.out.println("Resultado: " + resultado);
            } catch (Exception e) {
                System.out.println("Error al consultar: " + e.getMessage());
            }
        }

        scanner.close();
    }
}