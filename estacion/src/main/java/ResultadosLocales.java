import com.zeroc.Ice.Current;
import VotacionXYZ.EstacionVotacion;

public class ResultadosLocales implements EstacionVotacion {

    public ResultadosLocales() {}

    @Override
    public String[] obtenerAcumuladoVotos(Current current) {
        return new String[] { "Candidato1: 12", "Candidato2: 7" };
    }

    @Override
    public void obtenerVoto(Current current) {
        System.out.println("Voto recibido.");
    }
}
