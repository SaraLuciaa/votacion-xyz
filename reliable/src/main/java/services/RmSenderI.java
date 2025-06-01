package services;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.zeroc.Ice.Current;

import VotacionXYZ.AckServicePrx;
import VotacionXYZ.Message;
import VotacionXYZ.RmReceiverPrx;
import VotacionXYZ.RmSender;
import utils.AppLogger;
import utils.GuardadoVotos;

public class RmSenderI implements RmSender {
    private RmReceiverPrx service;
    private VotacionXYZ.AckServicePrx ackServicePrx;
    private boolean running;
    private final Logger log = AppLogger.get();
    private final GuardadoVotos guardado;
    private final ExecutorService executor;

    public RmSenderI(AckServicePrx ackServicePrx, GuardadoVotos guardado) {
        this.ackServicePrx = ackServicePrx;
        this.guardado = guardado;
        this.running = true;
        this.executor = Executors.newFixedThreadPool(3); // Pool de 3 hilos
        retry();
    }

    @Override
    public void setServerProxy(RmReceiverPrx service, Current current) {
        this.service = service;
        log.info("Proxy del receptor configurado exitosamente");
    }

    @Override
    public void send(Message msg, AckServicePrx ack, Current current) {
        log.info("Procesando solicitud de envío para: " + msg.voto.nombreCandidato + ", ID: " + msg.id);
        
        // Enviar en hilo separado para no bloquear la interfaz
        executor.submit(() -> {
            try {
                if (service != null && ack != null) {
                    log.info("Enviando voto de forma asíncrona...");
                    service.receiveMessage(msg, ack);
                    log.info(" Voto enviado exitosamente para: " + msg.voto.nombreCandidato + ", ID: " + msg.id);
                } else {
                    log.warning("Proxy no disponible. Guardando localmente");
                    guardado.add(msg);
                }
            } catch (Exception e) {
                log.warning("Error al enviar voto. Guardando localmente: " + e.getMessage());
                guardado.add(msg);
            }
        });
        
        // Retornar inmediatamente
        log.info("Solicitud de envío aceptada para: " + msg.voto.nombreCandidato);
    }

    private void resend() {
        List<Message> pendientes = guardado.getAll();
        if (pendientes.isEmpty()) {
            return;
        }

        log.info("🔄 Reenviando " + pendientes.size() + " votos pendientes...");
        for (Message msg : pendientes) {
            executor.submit(() -> {
                try {
                    if (service != null && ackServicePrx != null) {
                        service.receiveMessage(msg, ackServicePrx);
                        guardado.remove(msg.id);
                        log.info(" Voto reenviado exitosamente: " + msg.id);
                    } else {
                        log.warning(" Sin conexión. Voto retenido: " + msg.id);
                    }
                } catch (Exception e) {
                    log.warning(" Error al reenviar voto: " + msg.id + " - " + e.getMessage());
                }
            });
        }
    }

    public void retry() {
        Thread reintentoThread = new Thread(() -> {
            while (running) {
                try {
                    Thread.sleep(5000); // Cada 5 segundos
                    resend();
                } catch (InterruptedException e) {
                    log.warning("Reintento interrumpido: " + e.getMessage());
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        reintentoThread.setDaemon(true);
        reintentoThread.start();
        log.info("🔄 Hilo de reintento iniciado (cada 5 segundos).");
    }

    public void stop() {
        running = false;
        executor.shutdown();
        log.info("Servicio RmSender detenido");
    }
}