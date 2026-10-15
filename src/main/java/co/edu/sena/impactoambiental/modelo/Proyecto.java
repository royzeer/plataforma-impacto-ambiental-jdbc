package co.edu.sena.impactoambiental.modelo;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Representa un proyecto de impacto ambiental (clase del diagrama de clases).
 * Es un POJO sencillo: atributos privados, constructores y métodos get/set.
 *
 * @author Roycer Cervantes
 * @author Sebastián Rodríguez
 */
public class Proyecto {

    private int idProyecto;
    private String nombre;
    private String descripcion;
    private String ubicacion;
    private String tipoImpacto;
    private String responsable;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private BigDecimal presupuesto;
    private EstadoProyecto estado;

    /** Constructor vacío, útil cuando vamos llenando los datos poco a poco. */
    public Proyecto() {
        this.presupuesto = BigDecimal.ZERO;
        this.estado = EstadoProyecto.PLANEADO;
    }

    /** Constructor completo, lo usamos sobre todo al leer registros de la base de datos. */
    public Proyecto(int idProyecto, String nombre, String descripcion, String ubicacion,
                    String tipoImpacto, String responsable, LocalDate fechaInicio,
                    LocalDate fechaFin, BigDecimal presupuesto, EstadoProyecto estado) {
        this.idProyecto = idProyecto;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.ubicacion = ubicacion;
        this.tipoImpacto = tipoImpacto;
        this.responsable = responsable;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.presupuesto = presupuesto;
        this.estado = estado;
    }

    public int getIdProyecto() {
        return idProyecto;
    }

    public void setIdProyecto(int idProyecto) {
        this.idProyecto = idProyecto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getTipoImpacto() {
        return tipoImpacto;
    }

    public void setTipoImpacto(String tipoImpacto) {
        this.tipoImpacto = tipoImpacto;
    }

    public String getResponsable() {
        return responsable;
    }

    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public BigDecimal getPresupuesto() {
        return presupuesto;
    }

    public void setPresupuesto(BigDecimal presupuesto) {
        this.presupuesto = presupuesto;
    }

    public EstadoProyecto getEstado() {
        return estado;
    }

    public void setEstado(EstadoProyecto estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return String.format("%-4d | %-40s | %-28s | %-12s | %-13s | $%,.0f",
                idProyecto,
                recortar(nombre, 40),
                recortar(ubicacion, 28),
                tipoImpacto,
                estado,
                presupuesto);
    }

    // Recorta textos largos para que la tabla de consola no se desacomode
    private static String recortar(String texto, int maximo) {
        if (texto == null) {
            return "";
        }
        return texto.length() <= maximo ? texto : texto.substring(0, maximo - 3) + "...";
    }
}
