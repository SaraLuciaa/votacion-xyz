import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.Util;

import VotacionXYZ.RmReceiver;
import VotacionXYZ.queryStation;
import impl.QueryStationI;
import impl.RmReceiverI;
import impl.DataDistributionI;

public class Central {

    public static void main(String[] args) {
        try {
            Communicator communicator = Util.initialize(args, "central.cfg");

            ObjectAdapter queryAdapter = communicator.createObjectAdapter("QueryAdapter");
            ObjectAdapter rmAdapter = communicator.createObjectAdapter("RMAdapter");
            ObjectAdapter dataDistributionAdapter = communicator.createObjectAdapter("DistributionAdapter");

            queryStation query = new QueryStationI(communicator);
            queryAdapter.add(query, Util.stringToIdentity("QueryService"));

            RmReceiver receiver = new RmReceiverI();
            rmAdapter.add(receiver, Util.stringToIdentity("RMService"));

            DataDistributionI dataDistribution = new DataDistributionI();
            dataDistributionAdapter.add(dataDistribution, Util.stringToIdentity("DataDistributor"));

            

            queryAdapter.activate();
            rmAdapter.activate();
            dataDistributionAdapter.activate();

            System.out.println("Servidor central activo.");

            communicator.waitForShutdown();

        } catch (Exception e) {
            System.err.println("Error en el servidor central: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
