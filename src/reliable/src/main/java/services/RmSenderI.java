package services;
import java.util.List;
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

    public RmSenderI(AckServicePrx ackServicePrx, GuardadoVotos guardado) {
        this.ackServicePrx = ackServicePrx;
        this.guardado = guardado;
        this.running = true;
        retry();
    }

    @Override
    public void setServerProxy(RmReceiverPrx service, Current current) {
        this.service = service;
    }

    public void setServerProxy(RmReceiverPrx service) {
        this.service = service;
    }

    @Override
    public void send(Message msg, Current current) {
        try {
            if (service != null && ackServicePrx != null) {
                service.receiveMessage(msg, ackServicePrx);
                log.info("Voto enviado para: " + msg.voto.idCandidato + ", ID: " + msg.id);
            } else {
                throw new IllegalStateException("Proxy no disponible");
            }
        } catch (Exception e) {
            log.warning("Error al enviar voto. Guardando localmente: " + e.getMessage());
            guardado.add(msg);
        }
    }

    private void resend() {
        List<Message> pendientes = guardado.getAll();
        if (pendientes.isEmpty()) {
            log.info("No hay votos pendientes para reenviar.");
            return;
        }

        for (Message msg : pendientes) {
            try {
                if (service != null && ackServicePrx != null) {
                    service.receiveMessage(msg, ackServicePrx);
                    guardado.remove(msg.id);
                    log.info("Voto reenviado: " + msg.id);
                } else {
                    log.warning("Sin conexión. Voto retenido: " + msg.id);
                }
            } catch (Exception e) {
                log.log(Level.SEVERE, "Error al reenviar voto: ", e);
                //log.warning("Error al reenviar voto: " + msg.id + " - " + e.getMessage());
            }
        }
    }

    public void retry() {
        Thread reintentoThread = new Thread(() -> {
            while (running) {
                try {
                    Thread.sleep(3000);
                    resend();
                } catch (InterruptedException e) {
                    log.warning("Reintento interrumpido: " + e.getMessage());
                    break;
                }
            }
        });
        reintentoThread.setDaemon(true);
        reintentoThread.start();
        log.info("Hilo de reintento iniciado.");
    }

}
