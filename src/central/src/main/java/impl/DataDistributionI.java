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
    public DatosMesa sendData(String puestoId, Current current) {
        System.out.println("Distribuyendo datos del puesto de votación: " + puestoId);

        DataManager dm = DataManager.getInstance(current.adapter.getCommunicator());

        List<Ciudadano> ciudadanos = new ArrayList<>();
        try (ResultSet rs = dm.obtenerCiudadanosPorPuesto(puestoId)) {
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
                    rs.getString("partido")
                );
                candidatos.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new DatosMesa(
            ciudadanos.toArray(new Ciudadano[0]),
            candidatos.toArray(new Candidato[0])
        );
    }
} 