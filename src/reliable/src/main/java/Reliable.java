import java.util.ArrayList;
import java.util.List;

import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.ObjectPrx;
import com.zeroc.Ice.Util;

import services.*;
import utils.GuardadoVotos;
import VotacionXYZ.*;

public class Reliable {
    public static void main(String[] args) {
        try {
            Communicator communicator = Util.initialize(args, "realiable.cfg");

            GuardadoVotos guardado = new GuardadoVotos();
            AckService ackService = new AckServiceI();
                        
            ObjectAdapter adapter = communicator.createObjectAdapter("RMService");
            ObjectPrx prx = adapter.add(ackService, Util.stringToIdentity("AckCallback"));
            RmSender sender = new RmSenderI(AckServicePrx.uncheckedCast(prx), guardado);
            adapter.add(sender, Util.stringToIdentity("RMSender"));

            adapter.activate();

            communicator.waitForShutdown();

            
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
