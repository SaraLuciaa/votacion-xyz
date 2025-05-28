import com.zeroc.Ice.Current;
import java.util.*;
import VotacionXYZ.MesaVotacion;

public class ControladorMesaVotacion implements Runnable, MesaVotacion {
    private List<String> candidatos;
    private List<Integer> votos;

    public ControladorMesaVotacion() {
        candidatos = Arrays.asList("Candidato A", "Candidato B", "Candidato C");
        
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
        int index = (int) candidatoIndex - 1;
        if (index >= 0 && index < votos.size()) {
            votos.set(index, votos.get(index) + 1);
            System.out.println("Voto registrado para " + candidatos.get(index));
        } else {
            throw new IllegalArgumentException("Indice de candidato no válido: " + candidatoIndex);
        }
    }

    @Override
    public String[] obtenerResultados(Current current) {
        String[] resultados = new String[candidatos.size()];
        for (int i = 0; i < candidatos.size(); i++) {
            resultados[i] = candidatos.get(i) + ": " + votos.get(i) + " votos";
        }
        return resultados;
    }
}