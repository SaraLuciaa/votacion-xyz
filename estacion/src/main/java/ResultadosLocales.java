import java.util.HashSet;
import java.util.Set;

import com.zeroc.Ice.Current;

import VotacionXYZ.AckServicePrx;
import VotacionXYZ.EstacionVotacion;

public class ResultadosLocales implements EstacionVotacion {

     private final Set<String> seen = new HashSet<>();

    public ResultadosLocales() {}

    @Override
    public String[] obtenerAcumuladoVotos(Current current) {
        return new String[] {};
    }

    @Override
    public void obtenerVoto(VotacionXYZ.Message msg, AckServicePrx ackProxy, Current current) {
         {
        if (seen.contains(msg.id)) return;

        System.out.println("Recibido: " + msg.text);
        seen.add(msg.id);

        ackProxy.confirm(msg.id);
    }
    }
}
