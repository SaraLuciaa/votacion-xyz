import java.util.ArrayList;
import java.util.List;

import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.Util;

public class Estacion {

     public static void main(String[] args) {

         try {
            List<String> extPar = new ArrayList<>();
            Communicator communicator = Util.initialize(args, "resultadosLocales.cfg", extPar);

            ObjectAdapter adapter = communicator.createObjectAdapter("resultadosLocales");

            ResultadosLocales object = new ResultadosLocales();

            adapter.add(object, Util.stringToIdentity("resultadosLocales"));

            adapter.activate();

            System.out.println("Servidor de estacion de votacion escuchando en el puerto 10020...");

            communicator.waitForShutdown();
        } catch (Exception e) {
            System.err.println("Error en la estacion: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }    
}
