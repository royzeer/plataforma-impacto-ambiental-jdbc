# Plataforma Digital Integral para la Gestión de Proyectos de Impacto Ambiental
## Módulo: Gestión de Proyectos (Java + JDBC + MySQL)

**Evidencia:** GA7-220501096-AA2-EV01 — Codificación de módulos del software
**Programa:** Análisis y Desarrollo de Software (228118) — SENA
**Aprendices:** Roycer Cervantes · Sebastián Rodríguez

---

### ¿Qué hace este módulo?

Es el corazón de nuestra plataforma: permite llevar el registro de los proyectos ambientales
(restauración de manglares, monitoreo de agua, limpieza de playas, etc.). Desde el menú por
consola se puede:

| Operación | Opción del menú | Método del DAO |
|---|---|---|
| **Inserción** | 1. Registrar proyecto | `insertar(Proyecto)` |
| **Consulta** | 2. Listar · 3. Buscar por id · 4. Buscar por nombre | `consultarTodos()`, `consultarPorId(int)`, `consultarPorNombre(String)` |
| **Actualización** | 5. Actualizar proyecto | `actualizar(Proyecto)` |
| **Eliminación** | 6. Eliminar proyecto (pide confirmación) | `eliminar(int)` |

La conexión con la base de datos se hace con **JDBC** (`DriverManager`, `Connection`,
`PreparedStatement`, `ResultSet`) y el driver oficial `mysql-connector-j`.

### Relación con los artefactos anteriores

- **Diagrama de clases:** la clase `Proyecto` y sus atributos (nombre, ubicación, tipo de impacto, responsable, fechas, presupuesto, estado) salen directamente del diagrama.
- **Historias de usuario / casos de uso:** "Como gestor ambiental quiero registrar, consultar, editar y eliminar proyectos para llevar control de su avance" → cada opción del menú cubre uno de esos casos.
- **Informe técnico (AA1-EV01) y estándares (AA1-EV02):** Git + GitHub como versionamiento y el estándar de codificación descrito abajo.

### Estructura del proyecto (arquitectura por capas)

```
plataforma-impacto-ambiental-jdbc/
├── pom.xml                              ← dependencias (Maven)
├── database/script_bd.sql               ← crea la BD, la tabla y datos de ejemplo
└── src/main/
    ├── resources/db.properties          ← url, usuario y clave de MySQL
    └── java/co/edu/sena/impactoambiental/
        ├── Main.java                    ← punto de entrada
        ├── conexion/ConexionBD.java     ← abre la conexión JDBC
        ├── modelo/Proyecto.java         ← entidad (POJO)
        ├── modelo/EstadoProyecto.java   ← enum de estados
        ├── dao/ProyectoDao.java         ← interfaz con el CRUD
        ├── dao/ProyectoDaoImpl.java     ← implementación con PreparedStatement
        ├── util/Validador.java          ← validaciones de datos
        └── vista/MenuConsola.java       ← menú para el usuario
```

### Estándares de codificación aplicados

| Elemento | Convención | Ejemplos en el código |
|---|---|---|
| **Paquetes** | minúsculas, dominio invertido | `co.edu.sena.impactoambiental.dao` |
| **Clases e interfaces** | PascalCase, sustantivos | `Proyecto`, `ConexionBD`, `ProyectoDaoImpl` |
| **Métodos** | camelCase, empiezan con verbo | `insertar()`, `consultarPorId()`, `validarFecha()` |
| **Variables** | camelCase, nombres descriptivos en español | `idProyecto`, `fechaInicio`, `listaProyectos` |
| **Constantes** | MAYÚSCULAS_CON_GUION_BAJO | `SQL_INSERTAR`, `OPCION_SALIR` |
| **Enum** | PascalCase y valores en mayúscula | `EstadoProyecto.EN_EJECUCION` |
| **Tablas/columnas SQL** | snake_case en minúscula | `proyecto`, `id_proyecto`, `tipo_impacto` |

Otras buenas prácticas: atributos privados con get/set, una clase por archivo, indentación de 4
espacios, comentarios Javadoc en cada clase, `try-with-resources` para cerrar conexiones y
`PreparedStatement` para evitar inyección SQL.

### Cómo ejecutarlo

1. Tener **Java 17+** y **MySQL** (o XAMPP) encendido.
2. Ejecutar `database/script_bd.sql` en MySQL Workbench o phpMyAdmin.
3. Revisar usuario/clave en `src/main/resources/db.properties`.
4. Abrir la carpeta en NetBeans, IntelliJ o VS Code (se detecta como proyecto Maven) y ejecutar `Main.java`.
   Por terminal: `mvn compile exec:java`

### Validaciones incluidas

- Campos obligatorios y longitud máxima (igual a la de la tabla).
- Fechas en formato AAAA-MM-DD y fecha de fin no anterior a la de inicio.
- Presupuesto numérico y no negativo.
- Nombre de proyecto único (lo controla la BD y el programa muestra un mensaje amable).
- Confirmación antes de eliminar.
