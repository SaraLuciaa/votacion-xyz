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

            // Documento a consultar
            String documento = "787277031"; // puedes pedirlo por consola si lo deseas
            String resultado = query.query(documento);

            System.out.println(resultado);

        } catch (Exception e) {
            System.err.println("Error en cliente de consulta: " + e.getMessage());
            e.printStackTrace();
        }
    }
}