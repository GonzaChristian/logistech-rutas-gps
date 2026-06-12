# LOGISTECH

Sistema de Automatizacion y Trazabilidad de Rutas GPS para el sector retail.

LOGISTECH es un prototipo web academico desarrollado con Java y Spring Boot. El avance cubre login por roles, dashboard, CRUD operativos, gestion de rutas, GPS simulado, incidencias y reportes exportables a Excel.

## Integrantes

- GonzaChristian
- Denis Rivera Limache
- Moises Juarez

## Tecnologias

- Java 17
- Spring Boot 3.3
- Spring MVC
- Spring Data JPA
- Spring Security
- Thymeleaf
- MySQL
- Maven
- JUnit 5
- Mockito
- Google Guava
- Apache Commons Lang
- Apache POI
- Logback
- Bootstrap, HTML y CSS

## Modulos Implementados

- Login con roles `ADMIN` y `SUPERVISOR`.
- Dashboard con indicadores principales.
- CRUD de conductores.
- CRUD de vehiculos.
- Registro de rutas.
- Asignacion de rutas a conductor y vehiculo.
- GPS simulado con latitud, longitud y fecha/hora automatica.
- Registro de incidencias.
- Reporte de rutas con filtros y exportacion Excel.

## Versionamiento Academico Por Fases

| Periodo | Responsable | Fase / Entrega |
| --- | --- | --- |
| 28-30 mayo | GonzaChristian | Base del proyecto, arquitectura MVC + DAO + SOLID, Maven, MySQL y layout inicial. |
| 31 mayo - 1 junio | GonzaChristian | Login con roles, BCrypt, cierre de sesion y dashboard. |
| 2-4 junio | Denis Rivera Limache | CRUD de conductores y vehiculos, validaciones con Guava y Commons Lang, pruebas unitarias. |
| 5-6 junio | Moises Juarez | Registro de rutas y asignaciones con validacion de disponibilidad. |
| 7 junio | Moises Juarez | GPS simulado, control de recorrido, incidencias y eventos con Logback. |
| 8-9 junio | GonzaChristian | Reportes con filtros y exportacion Excel usando Apache POI. |
| 10 junio | Equipo LOGISTECH | Pruebas finales, documentacion APF3 y preparacion para GitHub. |

## Rutas Principales

- `/login`: inicio de sesion.
- `/dashboard`: indicadores del sistema.
- `/conductores`: gestion de conductores.
- `/vehiculos`: gestion de vehiculos.
- `/rutas`: registro y edicion de rutas.
- `/asignaciones`: asignacion de rutas.
- `/recorridos`: GPS simulado y control de recorrido.
- `/incidencias`: control de incidencias.
- `/reportes`: reporte de rutas.
- `/reportes/exportar`: descarga Excel del reporte filtrado.

## Arquitectura

El proyecto usa una arquitectura MVC con separacion por capas:

- `controller`: controladores web MVC.
- `service`: logica de negocio y reglas de validacion.
- `dao`: contratos de acceso a datos.
- `dao.impl`: implementaciones DAO apoyadas en repositorios JPA.
- `repository`: persistencia con Spring Data JPA.
- `entity`: entidades del dominio.
- `entity.enums`: estados y roles del sistema.
- `dto`: objetos para formularios, filtros y reportes.
- `util`: clases de apoyo, incluida exportacion Excel.
- `security`: configuracion de seguridad y carga de usuarios.
- `templates`: vistas Thymeleaf.
- `static/css`: estilos del sistema.

## Ejecucion Local

1. Tener MySQL activo.
2. Crear o permitir crear la base de datos `logistech_rutas`.
3. Configurar credenciales por variables de entorno:

```powershell
$env:DB_USERNAME="root"
$env:DB_PASSWORD="tu_password_local"
```

4. Ejecutar el proyecto:

```powershell
mvn spring-boot:run
```

5. Abrir:

```text
http://localhost:8080/login
```

## Perfil Demo Sin MySQL

Si solo necesitas revisar la aplicacion sin conectar MySQL:

```powershell
mvn spring-boot:run "-Dspring-boot.run.profiles=demo"
```

## Usuarios Demo

Usuarios solo para entorno academico/local:

- Administrador: `admin@logistech.local` / `admin123`
- Supervisor: `supervisor@logistech.local` / `supervisor123`

Las contrasenas se almacenan cifradas con BCrypt.

## Pruebas

Ejecutar:

```powershell
mvn test
```

Si Maven muestra un error SSL tipo `PKIX path building failed` en Windows, ejecutar antes:

```powershell
$env:MAVEN_OPTS="-Djavax.net.ssl.trustStoreType=Windows-ROOT"
```

Ultima verificacion del avance:

```text
Tests run: 43, Failures: 0, Errors: 0
```

## Git y GitHub

Comandos sugeridos para el primer commit:

```powershell
git add .
git commit -m "Avance APF3 funcional de LOGISTECH"
```

Para subir a GitHub:

```powershell
git branch -M main
git remote add origin https://github.com/USUARIO/REPOSITORIO.git
git push -u origin main
```

No se deben subir credenciales reales. Usa variables de entorno o un archivo local ignorado por Git.

## Nota Sobre MySQL

Si se usa una base creada previamente con tipos `INT` y el proyecto usa IDs `Long`, Hibernate puede mostrar advertencias al intentar ajustar claves foraneas. Para una ejecucion limpia, se recomienda usar una base nueva `logistech_rutas` o alinear los tipos de columnas.
