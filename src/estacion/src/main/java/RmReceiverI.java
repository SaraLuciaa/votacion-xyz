import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.zeroc.Ice.Current;

import VotacionXYZ.AckServicePrx;
import VotacionXYZ.RmReceiver;

public class RmReceiverI implements RmReceiver {
    private final Set<String> seen = new HashSet<>();
    private final ExecutorService ackExecutor = Executors.newFixedThreadPool(2);

    public RmReceiverI() {    }

    @Override
    public void receiveMessage(VotacionXYZ.Message msg, AckServicePrx ackProxy, Current current) {
        {
            synchronized (seen) {
                if (seen.contains(msg.id)) {
                    System.out.println("[RECEIVER] Mensaje duplicado ignorado, ID: " + msg.id);
                    return;
                }
                seen.add(msg.id);
            }

            ackExecutor.submit(() -> {
                try {
                    ackProxy.confirm(msg.id);
                    System.out.println("Recibido: " + msg.voto.nombreCandidato);
                } catch (Exception e) {
                    System.err.println("[RECEIVER] Error al confirmar ACK para ID " + msg.id + ": " + e.getMessage());
                    e.printStackTrace();
                }
            });
        }

        
    }

    public void shutdown() {
        ackExecutor.shutdown();
    }
}
