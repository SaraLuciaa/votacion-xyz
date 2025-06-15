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
}