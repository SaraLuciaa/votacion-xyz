import com.zeroc.Ice.Current;
import java.util.*;
import VotacionXYZ.MesaVotacion;
import VotacionXYZ.Message;
import VotacionXYZ.AckServicePrx;
import VotacionXYZ.EstacionVotacionPrx;

public class ControladorMesaVotacion implements Runnable, MesaVotacion {
    private List<String> candidatos;
    private EstacionVotacionPrx resultadosProxy;
    private AckServicePrx ackServicePrx; 

    public ControladorMesaVotacion(EstacionVotacionPrx resultadosProxy, AckServicePrx ackServicePrx) {
        candidatos = Arrays.asList("Candidato A", "Candidato B", "Candidato C");
        this.resultadosProxy = resultadosProxy;
        this.ackServicePrx = ackServicePrx;
        
    }


    @Override
    public void run() {
        System.out.println("Servidor de mesa de votación iniciado...");
        System.out.println(listarCandidatos(null));
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
        String nombreCandidato = getCandidatoPorNumero((int) candidatoIndex);
        if (nombreCandidato == null) {
            System.out.println("Candidato no válido.");
            return;
        }

        if (resultadosProxy != null && ackServicePrx != null) {
            System.out.println("Voto enviado por: " + nombreCandidato);
            String uuid = UUID.randomUUID().toString();
            VotacionXYZ.Message msg = new Message(uuid, nombreCandidato);
            resultadosProxy.obtenerVoto(msg, ackServicePrx);
        } else {
            System.err.println("No hay conexión con la estación o el callback.");
        }
    }

    public String getCandidatoPorNumero(int numero) {
        if (numero >= 1 && numero <= candidatos.size()) {
            return candidatos.get(numero - 1);
        }
        return null;
    }

    @Override
    public String[] obtenerResultados(Current current) {

        return null;
    }
}