package impl;

import java.util.List;
import java.util.UUID;

import com.zeroc.Ice.Current;

import VotacionXYZ.Candidato;
import VotacionXYZ.Ciudadano;
import VotacionXYZ.Message;
import VotacionXYZ.RmSenderPrx;
import VotacionXYZ.VoteStation;
import VotacionXYZ.Voto;
import utils.Loader;

public class VoteStationI implements VoteStation {
    private List<Candidato> candidatos;
    private List<Ciudadano> ciudadanos;
    private List<String> votos_realizados;
    private RmSenderPrx rmSender;

    public VoteStationI(RmSenderPrx rmSender) {
        this.candidatos = new Loader<>("estacion/src/main/resources/candidatos.json", Candidato.class).getAll();
        this.ciudadanos = new Loader<>("estacion/src/main/resources/ciudadanos.json", Ciudadano.class).getAll();
        this.votos_realizados = new Loader<>("estacion/src/main/resources/votos_realizados.json", String.class).getAll();
        this.rmSender = rmSender;
    }

    @Override
    public Candidato[] obtenerCandidatos(Current current) {
        return candidatos.toArray(new Candidato[0]);
    }

    @Override
    public int consultarCiudadanoPorId(String documento, int mesaId, Current current) {
        if (documento == null || documento.isEmpty()) {
            throw new IllegalArgumentException("El documento no puede ser nulo o vacío");
        }

        for (Ciudadano ciudadano : ciudadanos) {
            if (ciudadano.documento.equals(documento)) {
                if (votos_realizados.contains(documento)) {
                    System.out.println("[VoteStationI] Ciudadano ya ha votado: " + documento);
                    return 2; 
                }
                if (ciudadano.mesaId != mesaId) {
                    System.out.println("[VoteStationI] Ciudadano pertenece a otra mesa: " + documento);
                    return 1; 
                }
                System.out.println("[VoteStationI] Ciudadano puede votar: " + documento);
                return 0; 
            }
        }

        System.out.println("[VoteStationI] Ciudadano no encontrado: " + documento);
        return 3; 
    }

    @Override
    public void registrarVoto(int candidato, String documento, Current current) {
        Voto voto = new Voto(candidato);
        String uuid = UUID.randomUUID().toString();
        Message msg = new Message(uuid, voto);

        rmSender.send(msg);
        votos_realizados.add(documento);
    }
    
}
