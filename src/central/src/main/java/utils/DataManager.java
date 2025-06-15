package utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.zeroc.Ice.Communicator;

public class DataManager {

	private static DataManager instance;
	

	private Connection conexion;
	private final ConexionBD conexionBD;

	// Constructor privado
	private DataManager(Communicator com) {
		this.conexionBD = new ConexionBD(com);
		this.conexion = conexionBD.conectarBaseDatos();
	}

	// Método estático para obtener la única instancia
	public static synchronized DataManager getInstance(Communicator com) {
		if (instance == null) {
			instance = new DataManager(com);
		}
		return instance;
	}

	public String obtenerInfoCompletaPorCedula(String cedula) {
		if (conexion == null) {
			conexion = conexionBD.conectarBaseDatos();
		}

		String sql = "SELECT * FROM info_votante WHERE documento = ?";

		try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
			stmt.setString(1, cedula);
			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				String nombrePuesto = rs.getString("nombre_puesto");
				String direccion = rs.getString("direccion");
				String ciudad = rs.getString("municipio");
				String departamento = rs.getString("departamento");
				int mesa = rs.getInt("numero_mesa");

				return String.format("Usted debe votar en %s ubicado en %s en %s, %s en la mesa %d.",
						nombrePuesto, direccion, ciudad, departamento, mesa);
			} else {
				return "Ciudadano no registrado para votar.";
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return "Error al consultar el puesto de votación.";
		}
	}

	public ResultSet obtenerCiudadanosPorPuesto(String idPuesto) {
		if (conexion == null) {
			conexion = conexionBD.conectarBaseDatos();
		}

		try {
			String sqlVotantes = "SELECT c.documento " +
								"FROM ciudadano c " +
								"JOIN mesa_votacion mv ON c.mesa_id = mv.id " +
								"WHERE mv.puesto_id = ?";
			PreparedStatement stmtVotantes = conexion.prepareStatement(sqlVotantes,
												ResultSet.TYPE_SCROLL_INSENSITIVE,
												ResultSet.CONCUR_READ_ONLY);
			stmtVotantes.setInt(1, Integer.parseInt(idPuesto));
			return stmtVotantes.executeQuery();

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public ResultSet obtenerCandidatosConPartido() {
		if (conexion == null) {
			conexion = conexionBD.conectarBaseDatos();
		}

		try {
			String sqlCandidatos = "SELECT c.id, c.nombre, c.apellido, p.nombre AS nombre_partido " +
								"FROM Candidato c " +
								"JOIN partido p ON c.partido_id = p.id";
			PreparedStatement stmtCandidatos = conexion.prepareStatement(sqlCandidatos,
												ResultSet.TYPE_SCROLL_INSENSITIVE,
												ResultSet.CONCUR_READ_ONLY);
			return stmtCandidatos.executeQuery();

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

}