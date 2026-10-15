package co.edu.sena.impactoambiental.vista;

import co.edu.sena.impactoambiental.dao.ProyectoDao;
import co.edu.sena.impactoambiental.modelo.EstadoProyecto;
import co.edu.sena.impactoambiental.modelo.Proyecto;
import co.edu.sena.impactoambiental.util.Validador;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Menú por consola del módulo de Gestión de Proyectos.
 * Desde aquí el usuario puede insertar, consultar, actualizar y eliminar proyectos.
 *
 * @author Roycer Cervantes
 * @author Sebastián Rodríguez
 */
public class MenuConsola {

    private static final int OPCION_REGISTRAR = 1;
    private static final int OPCION_LISTAR = 2;
    private static final int OPCION_BUSCAR_ID = 3;
    private static final int OPCION_BUSCAR_NOMBRE = 4;
    private static final int OPCION_ACTUALIZAR = 5;
    private static final int OPCION_ELIMINAR = 6;
    private static final int OPCION_SALIR = 0;

    private static final String[] TIPOS_IMPACTO = {"AGUA", "AIRE", "SUELO", "RESIDUOS", "BIODIVERSIDAD", "RESTAURACION"};

    private final ProyectoDao proyectoDao;
    private final Scanner lector;

    public MenuConsola(ProyectoDao proyectoDao, Scanner lector) {
        this.proyectoDao = proyectoDao;
        this.lector = lector;
    }

    /** Ciclo principal: muestra el menú hasta que el usuario elija salir. */
    public void iniciar() {
        int opcionSeleccionada;
        do {
            mostrarMenu();
            opcionSeleccionada = leerOpcion();
            try {
                ejecutarOpcion(opcionSeleccionada);
            } catch (SQLIntegrityConstraintViolationException excepcion) {
                System.out.println("⚠ Ya existe un proyecto con ese nombre o un dato no cumple las reglas de la tabla.");
            } catch (SQLException excepcion) {
                System.out.println("⚠ Error de base de datos: " + excepcion.getMessage());
            } catch (IllegalArgumentException excepcion) {
                System.out.println("⚠ " + excepcion.getMessage());
            }
        } while (opcionSeleccionada != OPCION_SALIR);
    }

    private void mostrarMenu() {
        System.out.println();
        System.out.println("=====================================================");
        System.out.println("  PLATAFORMA DE PROYECTOS DE IMPACTO AMBIENTAL");
        System.out.println("  Módulo: Gestión de Proyectos");
        System.out.println("=====================================================");
        System.out.println("  1. Registrar proyecto");
        System.out.println("  2. Listar todos los proyectos");
        System.out.println("  3. Buscar proyecto por id");
        System.out.println("  4. Buscar proyecto por nombre");
        System.out.println("  5. Actualizar proyecto");
        System.out.println("  6. Eliminar proyecto");
        System.out.println("  0. Salir");
        System.out.print("Elija una opción: ");
    }

    private int leerOpcion() {
        try {
            return Validador.validarEntero(lector.nextLine(), "opción");
        } catch (IllegalArgumentException excepcion) {
            return -1;
        }
    }

    private void ejecutarOpcion(int opcion) throws SQLException {
        switch (opcion) {
            case OPCION_REGISTRAR -> registrarProyecto();
            case OPCION_LISTAR -> listarProyectos();
            case OPCION_BUSCAR_ID -> buscarPorId();
            case OPCION_BUSCAR_NOMBRE -> buscarPorNombre();
            case OPCION_ACTUALIZAR -> actualizarProyecto();
            case OPCION_ELIMINAR -> eliminarProyecto();
            case OPCION_SALIR -> System.out.println("Gracias por usar la plataforma. ¡Hasta pronto!");
            default -> System.out.println("Opción no válida, intente de nuevo.");
        }
    }

    // ------------------------------------------------------------------ INSERTAR
    private void registrarProyecto() throws SQLException {
        System.out.println("\n--- Registrar nuevo proyecto ---");
        Proyecto nuevoProyecto = new Proyecto();
        llenarDatosProyecto(nuevoProyecto, false);
        int idGenerado = proyectoDao.insertar(nuevoProyecto);
        System.out.println("✔ Proyecto registrado correctamente con id " + idGenerado + ".");
    }

    // ------------------------------------------------------------------ CONSULTAR
    private void listarProyectos() throws SQLException {
        List<Proyecto> listaProyectos = proyectoDao.consultarTodos();
        imprimirTabla(listaProyectos);
    }

    private void buscarPorId() throws SQLException {
        int idProyecto = pedirId();
        Optional<Proyecto> proyectoEncontrado = proyectoDao.consultarPorId(idProyecto);
        if (proyectoEncontrado.isPresent()) {
            imprimirDetalle(proyectoEncontrado.get());
        } else {
            System.out.println("No se encontró ningún proyecto con el id " + idProyecto + ".");
        }
    }

    private void buscarPorNombre() throws SQLException {
        System.out.print("Escriba parte del nombre: ");
        String textoBusqueda = Validador.validarTexto(lector.nextLine(), "búsqueda", 120);
        imprimirTabla(proyectoDao.consultarPorNombre(textoBusqueda));
    }

    // ------------------------------------------------------------------ ACTUALIZAR
    private void actualizarProyecto() throws SQLException {
        int idProyecto = pedirId();
        Optional<Proyecto> proyectoEncontrado = proyectoDao.consultarPorId(idProyecto);
        if (proyectoEncontrado.isEmpty()) {
            System.out.println("No existe el proyecto con id " + idProyecto + ".");
            return;
        }
        Proyecto proyectoActual = proyectoEncontrado.get();
        imprimirDetalle(proyectoActual);
        System.out.println("\nEscriba los nuevos datos (Enter para dejar el valor actual):");
        llenarDatosProyecto(proyectoActual, true);

        if (proyectoDao.actualizar(proyectoActual)) {
            System.out.println("✔ Proyecto actualizado correctamente.");
        } else {
            System.out.println("No se pudo actualizar el proyecto.");
        }
    }

    // ------------------------------------------------------------------ ELIMINAR
    private void eliminarProyecto() throws SQLException {
        int idProyecto = pedirId();
        Optional<Proyecto> proyectoEncontrado = proyectoDao.consultarPorId(idProyecto);
        if (proyectoEncontrado.isEmpty()) {
            System.out.println("No existe el proyecto con id " + idProyecto + ".");
            return;
        }
        System.out.print("¿Seguro que desea eliminar \"" + proyectoEncontrado.get().getNombre() + "\"? (s/n): ");
        String confirmacion = lector.nextLine().trim();
        if (!confirmacion.equalsIgnoreCase("s")) {
            System.out.println("Eliminación cancelada.");
            return;
        }
        if (proyectoDao.eliminar(idProyecto)) {
            System.out.println("✔ Proyecto eliminado.");
        }
    }

    // ------------------------------------------------------------------ AUXILIARES

    /**
     * Pide al usuario cada dato del proyecto. Si esEdicion es true, al presionar
     * Enter se conserva el valor que ya tenía.
     */
    private void llenarDatosProyecto(Proyecto proyecto, boolean esEdicion) {
        proyecto.setNombre(Validador.validarTexto(
                pedirValor("Nombre", proyecto.getNombre(), esEdicion), "nombre", 120));

        String descripcion = pedirValor("Descripción (opcional)", proyecto.getDescripcion(), esEdicion);
        proyecto.setDescripcion(descripcion == null || descripcion.isBlank() ? null : descripcion.trim());

        proyecto.setUbicacion(Validador.validarTexto(
                pedirValor("Ubicación", proyecto.getUbicacion(), esEdicion), "ubicación", 150));

        proyecto.setTipoImpacto(pedirTipoImpacto(proyecto.getTipoImpacto(), esEdicion));

        proyecto.setResponsable(Validador.validarTexto(
                pedirValor("Responsable", proyecto.getResponsable(), esEdicion), "responsable", 100));

        LocalDate fechaInicio = Validador.validarFecha(pedirValor("Fecha de inicio (AAAA-MM-DD)",
                textoFecha(proyecto.getFechaInicio()), esEdicion), "fecha de inicio");

        String textoFechaFin = pedirValor("Fecha de fin (AAAA-MM-DD, opcional)",
                textoFecha(proyecto.getFechaFin()), esEdicion);
        LocalDate fechaFin = (textoFechaFin == null || textoFechaFin.isBlank())
                ? null : Validador.validarFecha(textoFechaFin, "fecha de fin");

        Validador.validarRangoFechas(fechaInicio, fechaFin);
        proyecto.setFechaInicio(fechaInicio);
        proyecto.setFechaFin(fechaFin);

        BigDecimal presupuesto = Validador.validarPresupuesto(pedirValor("Presupuesto (COP)",
                proyecto.getPresupuesto() != null ? proyecto.getPresupuesto().toPlainString() : null, esEdicion));
        proyecto.setPresupuesto(presupuesto);

        proyecto.setEstado(pedirEstado(proyecto.getEstado(), esEdicion));
    }

    private String pedirValor(String etiqueta, String valorActual, boolean esEdicion) {
        if (esEdicion && valorActual != null) {
            System.out.print(etiqueta + " [" + valorActual + "]: ");
        } else {
            System.out.print(etiqueta + ": ");
        }
        String entrada = lector.nextLine();
        return (esEdicion && entrada.isBlank()) ? valorActual : entrada;
    }

    private String pedirTipoImpacto(String valorActual, boolean esEdicion) {
        System.out.println("Tipos de impacto disponibles:");
        for (int indice = 0; indice < TIPOS_IMPACTO.length; indice++) {
            System.out.println("   " + (indice + 1) + ". " + TIPOS_IMPACTO[indice]);
        }
        String entrada = pedirValor("Número del tipo de impacto", null, false);
        if (esEdicion && entrada.isBlank()) {
            return valorActual;
        }
        int posicion = Validador.validarEntero(entrada, "tipo de impacto");
        if (posicion < 1 || posicion > TIPOS_IMPACTO.length) {
            throw new IllegalArgumentException("Debe elegir un tipo de impacto entre 1 y " + TIPOS_IMPACTO.length + ".");
        }
        return TIPOS_IMPACTO[posicion - 1];
    }

    private EstadoProyecto pedirEstado(EstadoProyecto valorActual, boolean esEdicion) {
        EstadoProyecto[] estados = EstadoProyecto.values();
        System.out.println("Estados disponibles:");
        for (int indice = 0; indice < estados.length; indice++) {
            System.out.println("   " + (indice + 1) + ". " + estados[indice]);
        }
        String entrada = pedirValor("Número del estado", null, false);
        if (entrada.isBlank()) {
            // En registro nuevo, si no elige nada, queda PLANEADO
            return esEdicion ? valorActual : EstadoProyecto.PLANEADO;
        }
        int posicion = Validador.validarEntero(entrada, "estado");
        if (posicion < 1 || posicion > estados.length) {
            throw new IllegalArgumentException("Debe elegir un estado entre 1 y " + estados.length + ".");
        }
        return estados[posicion - 1];
    }

    private int pedirId() {
        System.out.print("Id del proyecto: ");
        return Validador.validarEntero(lector.nextLine(), "id");
    }

    private void imprimirTabla(List<Proyecto> listaProyectos) {
        if (listaProyectos.isEmpty()) {
            System.out.println("No hay proyectos para mostrar.");
            return;
        }
        System.out.printf("%n%-4s | %-40s | %-28s | %-12s | %-13s | %s%n",
                "ID", "NOMBRE", "UBICACIÓN", "IMPACTO", "ESTADO", "PRESUPUESTO");
        System.out.println("-".repeat(125));
        listaProyectos.forEach(System.out::println);
        System.out.println("Total: " + listaProyectos.size() + " proyecto(s).");
    }

    private void imprimirDetalle(Proyecto proyecto) {
        System.out.println("\n--- Proyecto #" + proyecto.getIdProyecto() + " ---");
        System.out.println("Nombre:       " + proyecto.getNombre());
        System.out.println("Descripción:  " + (proyecto.getDescripcion() == null ? "-" : proyecto.getDescripcion()));
        System.out.println("Ubicación:    " + proyecto.getUbicacion());
        System.out.println("Impacto:      " + proyecto.getTipoImpacto());
        System.out.println("Responsable:  " + proyecto.getResponsable());
        System.out.println("Inicio:       " + textoFecha(proyecto.getFechaInicio()));
        System.out.println("Fin:          " + (proyecto.getFechaFin() == null ? "En curso" : textoFecha(proyecto.getFechaFin())));
        System.out.printf("Presupuesto:  $%,.0f COP%n", proyecto.getPresupuesto());
        System.out.println("Estado:       " + proyecto.getEstado());
    }

    private String textoFecha(LocalDate fecha) {
        return fecha == null ? null : fecha.format(Validador.FORMATO_FECHA);
    }
}
