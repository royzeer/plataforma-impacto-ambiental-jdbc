package co.edu.sena.impactoambiental.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Validaciones reutilizables para los datos que escribe el usuario en consola.
 * Si algo no cumple, lanzamos IllegalArgumentException con un mensaje claro,
 * para que la vista se lo muestre tal cual.
 *
 * @author Roycer Cervantes
 * @author Sebastián Rodríguez
 */
public final class Validador {

    public static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private Validador() {
    }

    /** Verifica que el texto no esté vacío y no supere la longitud permitida en la tabla. */
    public static String validarTexto(String valor, String nombreCampo, int longitudMaxima) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("El campo " + nombreCampo + " es obligatorio.");
        }
        String valorLimpio = valor.trim();
        if (valorLimpio.length() > longitudMaxima) {
            throw new IllegalArgumentException("El campo " + nombreCampo + " admite máximo "
                    + longitudMaxima + " caracteres.");
        }
        return valorLimpio;
    }

    /** Convierte un texto con formato AAAA-MM-DD en fecha. */
    public static LocalDate validarFecha(String valor, String nombreCampo) {
        try {
            return LocalDate.parse(valor.trim(), FORMATO_FECHA);
        } catch (DateTimeParseException | NullPointerException excepcion) {
            throw new IllegalArgumentException("La " + nombreCampo
                    + " debe tener el formato AAAA-MM-DD (ejemplo: 2026-10-14).");
        }
    }

    /** La fecha de fin no puede quedar antes de la fecha de inicio. */
    public static void validarRangoFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        if (fechaFin != null && fechaFin.isBefore(fechaInicio)) {
            throw new IllegalArgumentException("La fecha de fin no puede ser anterior a la fecha de inicio.");
        }
    }

    /** El presupuesto debe ser un número positivo o cero. */
    public static BigDecimal validarPresupuesto(String valor) {
        try {
            BigDecimal presupuesto = new BigDecimal(valor.trim().replace(",", ""));
            if (presupuesto.signum() < 0) {
                throw new IllegalArgumentException("El presupuesto no puede ser negativo.");
            }
            return presupuesto;
        } catch (NumberFormatException | NullPointerException excepcion) {
            throw new IllegalArgumentException("El presupuesto debe ser un número (ejemplo: 1500000).");
        }
    }

    /** Convierte el texto a entero; útil para ids y opciones del menú. */
    public static int validarEntero(String valor, String nombreCampo) {
        try {
            return Integer.parseInt(valor.trim());
        } catch (NumberFormatException | NullPointerException excepcion) {
            throw new IllegalArgumentException("El campo " + nombreCampo + " debe ser un número entero.");
        }
    }
}
