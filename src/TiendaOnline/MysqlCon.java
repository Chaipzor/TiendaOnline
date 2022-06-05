package TiendaOnline;
import java.sql.*;

import com.mysql.cj.Query;

class MysqlCon {
    public static void main(String args[]) {
        // menu();
        // conectar();

    }

    public static void agregarUsuario() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            java.sql.Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/tiendaonline",
                    "root", "");
            if (conexion != null) {
                System.out.println("conectado correctamente");
                String nombre = "Raul";

                String query = "INSERT INTO usuarios (nombre) values ('" + nombre + "')";

                Statement stmt = conexion.createStatement();

                stmt.executeUpdate(query);
                System.out.println("Usuario añadido correctamente: " + nombre);
            }
        } catch (Exception e) {
            System.out.println(e);
            System.out.println("Error de conexión");
        }
    }

    public static void eliminarUsuario() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            java.sql.Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/tiendaonline",
                    "root", "");
            if (conexion != null) {
                System.out.println("conectado correctamente");
                String nombre = "Raul";

                String query = "DELETE FROM usuarios (nombre) values ('" + nombre + "')";

                Statement stmt = conexion.createStatement();

                stmt.executeUpdate(query);
                System.out.println("Usuario añadido correctamente: " + nombre);
            }
        } catch (Exception e) {
            System.out.println(e);
            System.out.println("Error de conexión");
        }
    }
}