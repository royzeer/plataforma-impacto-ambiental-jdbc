package co.edu.sena.impactoambiental.conexion;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Se encarga de abrir conexiones JDBC contra la base de datos MySQL.
 *
 * Decidimos leer los datos de conexión desde el archivo db.properties para no
 * dejar usuario y clave "quemados" en el código. Así cada uno de nosotros puede
 * usar su propia configuración local sin tocar las clases.
 *
 * @author Roycer Cervantes
 * @author Sebastián Rodríguez
 */
public final class ConexionBD {

    private static final String ARCHIVO_CONFIGURACION = "/db.properties";

    // Valores por defecto por si no se encuentra el archivo de propiedades
    private static final String URL_POR_DEFECTO =
            "jdbc:mysql://localhost:3306/bd_impacto_ambiental?useSSL=false&serverTimezone=America/Bogota";
    private static final String USUARIO_POR_DEFECTO = "root";
    private static final String CLAVE_POR_DEFECTO = "";

    private static final Properties PROPIEDADES = cargarPropiedades();

    // Constructor privado: esta clase es utilitaria, no tiene sentido instanciarla
    private ConexionBD() {
    }

    /**
     * Devuelve una conexión nueva a la base de datos.
     * Quien la pida es responsable de cerrarla (lo hacemos con try-with-resources).
     *
     * @return conexión abierta
     * @throws SQLException si el servidor no está disponible o los datos son incorrectos
     */
    public static Connection obtenerConexion() throws SQLException {
        String url = PROPIEDADES.getProperty("db.url", URL_POR_DEFECTO);
        String usuario = PROPIEDADES.getProperty("db.usuario", USUARIO_POR_DEFECTO);
        String clave = PROPIEDADES.getProperty("db.clave", CLAVE_POR_DEFECTO);
        return DriverManager.getConnection(url, usuario, clave);
    }

    /**
     * Prueba rápida para saber si la base de datos responde.
     *
     * @return true si se pudo conectar
     */
    public static boolean probarConexion() {
        try (Connection conexion = obtenerConexion()) {
            return conexion.isValid(3);
        } catch (SQLException excepcion) {
            System.err.println("No fue posible conectar con la base de datos: " + excepcion.getMessage());
            return false;
        }
    }

    private static Properties cargarPropiedades() {
        Properties propiedades = new Properties();
        try (InputStream entrada = ConexionBD.class.getResourceAsStream(ARCHIVO_CONFIGURACION)) {
            if (entrada != null) {
                propiedades.load(entrada);
            }
        } catch (IOException excepcion) {
            System.err.println("No se pudo leer db.properties, se usarán valores por defecto.");
        }
        return propiedades;
    }
}
