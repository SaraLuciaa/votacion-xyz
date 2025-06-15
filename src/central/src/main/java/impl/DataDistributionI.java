package impl;

import VotacionXYZ.*;
import com.zeroc.Ice.Current;
import utils.DataManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DataDistributionI implements DataDistribution {

    @Override
    public DatosMesa getData(String mesaId, Current current) {
        System.out.println("Distribuyendo datos de la mesa: " + mesaId);

        DataManager dm = DataManager.getInstance(current.adapter.getCommunicator());

        List<Ciudadano> ciudadanos = new ArrayList<>();
        try (ResultSet rs = dm.obtenerCiudadanosPorPuesto(mesaId)) {
            while (rs != null && rs.next()) {
                Ciudadano c = new Ciudadano(rs.getString("documento"));
                ciudadanos.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        List<Candidato> candidatos = new ArrayList<>();
        try (ResultSet rs = dm.obtenerCandidatosConPartido()) {
            while (rs != null && rs.next()) {
                Candidato c = new Candidato(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("apellido"),
                    rs.getString("nombre_partido")
                );
                candidatos.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new DatosMesa(ciudadanos, candidatos);
    }
}
