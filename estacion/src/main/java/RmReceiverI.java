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

    public RmReceiverI() {
        System.out.println("[RECEIVER] Inicializado con pool de ACKs");    
    }

    @Override
    public void receiveMessage(VotacionXYZ.Message msg, AckServicePrx ackProxy, Current current) {
        try {
            // 1. Validar entrada
            if (msg == null || msg.id == null) {
                System.err.println("[RECEIVER] Mensaje inválido (null)");
                return;
            }

            // 2. Evitar duplicados
            synchronized (seen) {
                if (seen.contains(msg.id)) {
                    System.out.println("[RECEIVER] Mensaje duplicado ignorado, ID: " + msg.id);
                    return;
                }
                seen.add(msg.id);
            }

            // 3. Validar proxy
            if (ackProxy == null) {
                System.err.println("[RECEIVER] Error: AckProxy es null");
                return;
            }

            // 4. Procesar mensaje
            System.out.println("[RECEIVER] Procesando voto para: " + msg.voto.nombreCandidato + " (ID: " + msg.id + ")");
            
            // 5. Enviar ACK en hilo separado para no bloquear
            ackExecutor.submit(() -> {
                try {
                    System.out.println("[RECEIVER] Enviando ACK al proxy: " + ackProxy.ice_getIdentity().name);
                    ackProxy.confirm(msg.id);
                    System.out.println("[RECEIVER] ACK enviado exitosamente para ID: " + msg.id);
                } catch (Exception e) {
                    System.err.println("[RECEIVER] Error al confirmar ACK para ID " + msg.id + ": " + e.getMessage());
                    e.printStackTrace();
                }
            });
            
            // 6. Simular procesamiento del voto (opcional)
            System.out.println("[RECEIVER] Voto registrado para: " + msg.voto.nombreCandidato);
            
        } catch (Exception e) {
            System.err.println("[RECEIVER] Error general en receiveMessage: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void shutdown() {
        ackExecutor.shutdown();
        //System.out.println("[RECEIVER] Executor de ACKs detenido");
    }
}