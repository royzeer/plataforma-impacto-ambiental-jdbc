package co.edu.sena.impactoambiental.modelo;

/**
 * Estados por los que pasa un proyecto ambiental dentro de la plataforma.
 * Usamos un enum para evitar errores de escritura (por ejemplo "en ejecucion" vs "EN_EJECUCION").
 *
 * @author Roycer Cervantes
 * @author Sebastián Rodríguez
 */
public enum EstadoProyecto {
    PLANEADO,
    EN_EJECUCION,
    FINALIZADO,
    CANCELADO
}
