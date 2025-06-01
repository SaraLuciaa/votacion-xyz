import com.zeroc.Ice.Current;
import java.util.*;
import VotacionXYZ.*;

public class Votacion {
    private List<String> candidatos;
    private RmSenderPrx rmSender;
    private AckServicePrx ackProxy;

    public Votacion(RmSenderPrx rmSender, AckServicePrx ackProxy) {
    this.candidatos = Arrays.asList("Candidato A", "Candidato B", "Candidato C");
    this.rmSender = rmSender;
    this.ackProxy = ackProxy;
    }

    public String[] listarCandidatos(Current current) {
        String[] lista = new String[candidatos.size()];
        for (int i = 0; i < candidatos.size(); i++) {
            lista[i] = (i + 1) + ". " + candidatos.get(i);
        }
        return lista;
    }

    public void registrarVoto(long candidatoIndex) {
        if (candidatoIndex < 1 || candidatoIndex > candidatos.size()) {
            System.out.println("Indice de candidato fuera de rango: " + candidatoIndex);
            throw new IllegalArgumentException("Indice de candidato fuera de rango");
        }

        String nombreCandidato = getCandidatoPorNumero((int) candidatoIndex);
        if (nombreCandidato == null) {
            System.out.println("Candidato inválido: " + candidatoIndex);
            throw new IllegalArgumentException("Candidato no válido.");
        }

        Voto voto = new Voto(nombreCandidato);
        String uuid = UUID.randomUUID().toString();
        Message msg = new Message(uuid, voto);

        System.out.println("[VOTACION] Enviando mensaje con ID: " + uuid + " para " + nombreCandidato);
        rmSender.send(msg, ackProxy);  
    }


    public String getCandidatoPorNumero(int numero) {
        if (numero >= 1 && numero <= candidatos.size()) {
            return candidatos.get(numero - 1);
        }
        return null;
    }
}