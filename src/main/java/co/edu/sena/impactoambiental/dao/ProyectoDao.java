package co.edu.sena.impactoambiental.dao;

import co.edu.sena.impactoambiental.modelo.Proyecto;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Contrato con las operaciones CRUD del módulo de proyectos.
 * Separar la interfaz de la implementación nos permite cambiar de motor de base de
 * datos más adelante sin tocar la vista.
 *
 * @author Roycer Cervantes
 * @author Sebastián Rodríguez
 */
public interface ProyectoDao {

    /** Inserta un proyecto nuevo y devuelve el id que le asignó la base de datos. */
    int insertar(Proyecto proyecto) throws SQLException;

    /** Consulta todos los proyectos ordenados por id. */
    List<Proyecto> consultarTodos() throws SQLException;

    /** Busca un proyecto por su id. */
    Optional<Proyecto> consultarPorId(int idProyecto) throws SQLException;

    /** Busca proyectos cuyo nombre contenga el texto indicado. */
    List<Proyecto> consultarPorNombre(String textoBusqueda) throws SQLException;

    /** Actualiza todos los datos de un proyecto existente. Devuelve true si se modificó. */
    boolean actualizar(Proyecto proyecto) throws SQLException;

    /** Elimina un proyecto por su id. Devuelve true si se eliminó. */
    boolean eliminar(int idProyecto) throws SQLException;
}
