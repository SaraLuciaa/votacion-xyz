package impl;

import VotacionXYZ.MesaService;
import VotacionXYZ.Candidato;
import VotacionXYZ.Ciudadano;

import com.zeroc.Ice.Current;


public class MesaServiceI implements MesaService {

    @Override
    public String obtenerCandidatos(Current current) {
        return "";
    }

    @Override
    public Ciudadano consultarCiudadanoPorId(String idCiudadano, Current current) {
        // Implementación del método para mostrar los resultados de la votación
        return null;
    }

    @Override
    public void registrarVoto(int candidato, Current current) {
        // Implementación del método para mostrar los resultados de la votación
        System.out.println("Mostrando resultados de la votación...");
    }   
    
}
