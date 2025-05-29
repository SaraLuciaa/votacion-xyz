import com.zeroc.Ice.Current;
import java.util.*;
import VotacionXYZ.MesaVotacion;
import VotacionXYZ.Message;
import logger.GuardadoVotos;
import VotacionXYZ.AckServicePrx;
import VotacionXYZ.EstacionVotacionPrx;
import java.util.logging.Logger;
import logger.AppLogger;
import VotacionXYZ.Voto;

public class ControladorMesaVotacion implements MesaVotacion {
    private List<String> candidatos;
    private EstacionVotacionPrx resultadosProxy;
    private AckServicePrx ackServicePrx;
    private final GuardadoVotos guardado;
    private final Logger log;
    private volatile boolean running = true;

    public ControladorMesaVotacion(EstacionVotacionPrx resultadosProxy, AckServicePrx ackServicePrx) {
        this.candidatos = Arrays.asList("Candidato A", "Candidato B", "Candidato C");
        this.resultadosProxy = resultadosProxy;
        this.ackServicePrx = ackServicePrx;
        this.guardado = new GuardadoVotos();
        this.log = AppLogger.get();
    }

    @Override
    public String[] listarCandidatos(Current current) {
        String[] lista = new String[candidatos.size()];
        for (int i = 0; i < candidatos.size(); i++) {
            lista[i] = (i + 1) + ". " + candidatos.get(i);
        }
        return lista;
    }

    @Override
    public void registrarVoto(long candidatoIndex, Current current) {
        if (candidatoIndex < 1 || candidatoIndex > candidatos.size()) {
            log.warning("Indice de candidato fuera de rango: " + candidatoIndex);
            throw new IllegalArgumentException("Indice de candidato fuera de rango");
        }

        String nombreCandidato = getCandidatoPorNumero((int) candidatoIndex);
        if (nombreCandidato == null) {
            log.warning("Candidato inválido: " + candidatoIndex);
            throw new IllegalArgumentException("Candidato no válido.");
        }

        Voto voto = new Voto(nombreCandidato);
        String uuid = UUID.randomUUID().toString();
        Message msg = new Message(uuid, voto);

        try {
            if (resultadosProxy != null && ackServicePrx != null) {
                resultadosProxy.obtenerVoto(msg, ackServicePrx);
                log.info("Voto enviado para: " + nombreCandidato + ", ID: " + uuid);
            } else {
                throw new IllegalStateException("Proxy no disponible");
            }
        } catch (Exception e) {
            log.warning("Error al enviar voto. Guardando localmente: " + e.getMessage());
            guardado.add(msg);
        }
    }

    public String getCandidatoPorNumero(int numero) {
        if (numero >= 1 && numero <= candidatos.size()) {
            return candidatos.get(numero - 1);
        }
        return null;
    }

    public void reenviarVotosPendientes() {
        List<Message> pendientes = guardado.getAll();
        if (pendientes.isEmpty()) {
            log.info("No hay votos pendientes para reenviar.");
            return;
        }

        for (Message msg : pendientes) {
            try {
                if (resultadosProxy != null && ackServicePrx != null) {
                    resultadosProxy.obtenerVoto(msg, ackServicePrx);
                    guardado.remove(msg.id);
                    log.info("Voto reenviado: " + msg.id);
                } else {
                    log.warning("Sin conexión. Voto retenido: " + msg.id);
                }
            } catch (Exception e) {
                log.warning("Error al reenviar voto: " + msg.id + " - " + e.getMessage());
            }
        }
    }

    public void iniciarReintentoPeriodico() {
        Thread reintentoThread = new Thread(() -> {
            while (running) {
                try {
                    Thread.sleep(3000);
                    reenviarVotosPendientes();
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

    @Override
    public String[] obtenerResultados(Current current) {
        return null;
    }
}