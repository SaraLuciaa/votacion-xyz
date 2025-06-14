package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.Properties;

public class ConexionBD {

    private Communicator com;
    private Connection conexion;

    public ConexionBD(Communicator com) {
        this.com = com;
    }

    public Connection conectarBaseDatos() {
        try {
            Properties prop = com.getProperties();
            Class.forName("org.postgresql.Driver");

            String cadenaConexion = prop.getProperty("ConexionDB");
            String usuario = prop.getProperty("usuarioDB");
            String password = prop.getProperty("passwordDB");

            conexion = DriverManager.getConnection(cadenaConexion, usuario, password);
            return conexion;

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void cerrarConexion() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}