package co.edu.sena.impactoambiental;

import co.edu.sena.impactoambiental.conexion.ConexionBD;
import co.edu.sena.impactoambiental.dao.ProyectoDaoImpl;
import co.edu.sena.impactoambiental.vista.MenuConsola;

import java.util.Scanner;

/**
 * Punto de entrada del módulo de Gestión de Proyectos.
 * Primero comprobamos que la base de datos responda y luego abrimos el menú.
 *
 * Evidencia GA7-220501096-AA2-EV01 - Codificación de módulos del software.
 *
 * @author Roycer Cervantes
 * @author Sebastián Rodríguez
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("Conectando con la base de datos...");
        if (!ConexionBD.probarConexion()) {
            System.out.println("Revise que MySQL esté encendido, que haya ejecutado database/script_bd.sql");
            System.out.println("y que los datos de src/main/resources/db.properties sean correctos.");
            return;
        }
        System.out.println("Conexión exitosa ✔");

        try (Scanner lector = new Scanner(System.in)) {
            MenuConsola menuConsola = new MenuConsola(new ProyectoDaoImpl(), lector);
            menuConsola.iniciar();
        }
    }
}
