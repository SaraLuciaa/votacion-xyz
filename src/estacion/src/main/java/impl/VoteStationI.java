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
import utils.VotosRealizadosStore;

public class VoteStationI implements VoteStation {
    private List<Candidato> candidatos;
    private List<Ciudadano> ciudadanos;
    private VotosRealizadosStore store;
    private RmSenderPrx rmSender;

    public VoteStationI(RmSenderPrx rmSender) {
        this.candidatos = new Loader<>("estacion/src/main/resources/candidatos.json", Candidato.class).getAll();
        this.ciudadanos = new Loader<>("estacion/src/main/resources/ciudadanos.json", Ciudadano.class).getAll();
        this.store = new VotosRealizadosStore();
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
                if (store.yaVoto(documento)) {
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
    public void registrarVoto(int candidato, String documento, int mesaId, Current current) {
        Voto voto = new Voto(candidato, mesaId);
        String uuid = UUID.randomUUID().toString();
        Message msg = new Message(uuid, voto);

        rmSender.send(msg);
        store.registrar(documento, mesaId);
        System.out.println("✔️ Voto registrado para " + documento + " en mesa " + mesaId);
    }
    
}
