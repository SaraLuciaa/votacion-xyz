import java.util.HashSet;
import java.util.Set;

import com.zeroc.Ice.Current;

import VotacionXYZ.AckServicePrx;
import VotacionXYZ.RmReceiver;

public class RmReceiverI implements RmReceiver {
    private final Set<String> seen = new HashSet<>();

    public RmReceiverI() {    }

    @Override
    public void receiveMessage(VotacionXYZ.Message msg, AckServicePrx ackProxy, Current current) {
        {
            if (seen.contains(msg.id))
                return;

            System.out.println("Recibido: " + msg.voto.nombreCandidato);
            seen.add(msg.id);

            ackProxy.confirm(msg.id);
        }
    }
}
