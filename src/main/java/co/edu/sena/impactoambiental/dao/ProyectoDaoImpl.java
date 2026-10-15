package co.edu.sena.impactoambiental.dao;

import co.edu.sena.impactoambiental.conexion.ConexionBD;
import co.edu.sena.impactoambiental.modelo.EstadoProyecto;
import co.edu.sena.impactoambiental.modelo.Proyecto;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación JDBC del CRUD de proyectos.
 *
 * Todas las consultas usan PreparedStatement: así evitamos inyección SQL y no
 * tenemos que estar pegando textos a mano. Las conexiones se cierran solas gracias
 * a try-with-resources.
 *
 * @author Roycer Cervantes
 * @author Sebastián Rodríguez
 */
public class ProyectoDaoImpl implements ProyectoDao {

    // Las sentencias SQL van en constantes para tenerlas ordenadas en un solo lugar
    private static final String SQL_INSERTAR =
            "INSERT INTO proyecto (nombre, descripcion, ubicacion, tipo_impacto, responsable, "
            + "fecha_inicio, fecha_fin, presupuesto, estado) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_CONSULTAR_TODOS =
            "SELECT id_proyecto, nombre, descripcion, ubicacion, tipo_impacto, responsable, "
            + "fecha_inicio, fecha_fin, presupuesto, estado FROM proyecto ORDER BY id_proyecto";

    private static final String SQL_CONSULTAR_POR_ID =
            "SELECT id_proyecto, nombre, descripcion, ubicacion, tipo_impacto, responsable, "
            + "fecha_inicio, fecha_fin, presupuesto, estado FROM proyecto WHERE id_proyecto = ?";

    private static final String SQL_CONSULTAR_POR_NOMBRE =
            "SELECT id_proyecto, nombre, descripcion, ubicacion, tipo_impacto, responsable, "
            + "fecha_inicio, fecha_fin, presupuesto, estado FROM proyecto WHERE nombre LIKE ? "
            + "ORDER BY nombre";

    private static final String SQL_ACTUALIZAR =
            "UPDATE proyecto SET nombre = ?, descripcion = ?, ubicacion = ?, tipo_impacto = ?, "
            + "responsable = ?, fecha_inicio = ?, fecha_fin = ?, presupuesto = ?, estado = ? "
            + "WHERE id_proyecto = ?";

    private static final String SQL_ELIMINAR =
            "DELETE FROM proyecto WHERE id_proyecto = ?";

    @Override
    public int insertar(Proyecto proyecto) throws SQLException {
        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_INSERTAR,
                     Statement.RETURN_GENERATED_KEYS)) {

            asignarParametros(sentencia, proyecto);
            sentencia.executeUpdate();

            // Recuperamos el id autoincremental para devolverlo al usuario
            try (ResultSet llavesGeneradas = sentencia.getGeneratedKeys()) {
                if (llavesGeneradas.next()) {
                    int idGenerado = llavesGeneradas.getInt(1);
                    proyecto.setIdProyecto(idGenerado);
                    return idGenerado;
                }
            }
            throw new SQLException("El proyecto se insertó pero no se obtuvo el id generado.");
        }
    }

    @Override
    public List<Proyecto> consultarTodos() throws SQLException {
        List<Proyecto> listaProyectos = new ArrayList<>();
        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_CONSULTAR_TODOS);
             ResultSet resultado = sentencia.executeQuery()) {

            while (resultado.next()) {
                listaProyectos.add(mapearProyecto(resultado));
            }
        }
        return listaProyectos;
    }

    @Override
    public Optional<Proyecto> consultarPorId(int idProyecto) throws SQLException {
        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_CONSULTAR_POR_ID)) {

            sentencia.setInt(1, idProyecto);
            try (ResultSet resultado = sentencia.executeQuery()) {
                if (resultado.next()) {
                    return Optional.of(mapearProyecto(resultado));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Proyecto> consultarPorNombre(String textoBusqueda) throws SQLException {
        List<Proyecto> listaProyectos = new ArrayList<>();
        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_CONSULTAR_POR_NOMBRE)) {

            // Los % permiten encontrar el texto en cualquier parte del nombre
            sentencia.setString(1, "%" + textoBusqueda + "%");
            try (ResultSet resultado = sentencia.executeQuery()) {
                while (resultado.next()) {
                    listaProyectos.add(mapearProyecto(resultado));
                }
            }
        }
        return listaProyectos;
    }

    @Override
    public boolean actualizar(Proyecto proyecto) throws SQLException {
        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_ACTUALIZAR)) {

            asignarParametros(sentencia, proyecto);
            sentencia.setInt(10, proyecto.getIdProyecto());
            return sentencia.executeUpdate() > 0;
        }
    }

    @Override
    public boolean eliminar(int idProyecto) throws SQLException {
        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_ELIMINAR)) {

            sentencia.setInt(1, idProyecto);
            return sentencia.executeUpdate() > 0;
        }
    }

    /**
     * Pasa los datos del objeto a los parámetros 1 a 9 de la sentencia.
     * Lo usan tanto insertar como actualizar, por eso lo sacamos a un método aparte.
     */
    private void asignarParametros(PreparedStatement sentencia, Proyecto proyecto) throws SQLException {
        sentencia.setString(1, proyecto.getNombre());
        sentencia.setString(2, proyecto.getDescripcion());
        sentencia.setString(3, proyecto.getUbicacion());
        sentencia.setString(4, proyecto.getTipoImpacto());
        sentencia.setString(5, proyecto.getResponsable());
        sentencia.setDate(6, Date.valueOf(proyecto.getFechaInicio()));

        // La fecha de fin es opcional: un proyecto en curso todavía no la tiene
        if (proyecto.getFechaFin() != null) {
            sentencia.setDate(7, Date.valueOf(proyecto.getFechaFin()));
        } else {
            sentencia.setNull(7, Types.DATE);
        }

        sentencia.setBigDecimal(8, proyecto.getPresupuesto());
        sentencia.setString(9, proyecto.getEstado().name());
    }

    /** Convierte la fila actual del ResultSet en un objeto Proyecto. */
    private Proyecto mapearProyecto(ResultSet resultado) throws SQLException {
        Date fechaFinSql = resultado.getDate("fecha_fin");
        return new Proyecto(
                resultado.getInt("id_proyecto"),
                resultado.getString("nombre"),
                resultado.getString("descripcion"),
                resultado.getString("ubicacion"),
                resultado.getString("tipo_impacto"),
                resultado.getString("responsable"),
                resultado.getDate("fecha_inicio").toLocalDate(),
                fechaFinSql != null ? fechaFinSql.toLocalDate() : null,
                resultado.getBigDecimal("presupuesto"),
                EstadoProyecto.valueOf(resultado.getString("estado")));
    }
}
