# Evidencias APF3 - LOGISTECH

## Datos Del Proyecto

- Proyecto: LOGISTECH.
- Nombre: Sistema de Automatizacion y Trazabilidad de Rutas GPS para el sector retail.
- Tipo: prototipo web academico funcional.
- Alcance del avance: login, dashboard, CRUD principales, rutas, asignaciones, GPS simulado, incidencias y reportes Excel.

## Resumen De Cumplimiento APF3

El avance implementa una aplicacion web con Spring Boot y arquitectura MVC por capas. Se evidencia uso de frameworks, librerias Java, persistencia con MySQL/JPA, pruebas unitarias, seguridad basica, control de versiones y documentacion del sistema.

## Arquitectura Implementada

La arquitectura usada es MVC + DAO + Service + Repository:

- `controller`: recibe solicitudes HTTP, prepara modelos y redirige vistas.
- `service`: concentra reglas de negocio, validaciones y transacciones.
- `dao`: define contratos de acceso a datos.
- `dao.impl`: implementa los contratos usando repositorios JPA.
- `repository`: usa Spring Data JPA para persistencia.
- `entity`: representa tablas del dominio.
- `dto`: evita exponer entidades directamente en formularios y reportes.
- `util`: concentra utilidades como la generacion de Excel.
- `security`: configuracion de login, roles y BCrypt.
- `templates`: vistas Thymeleaf.
- `static/css`: estilos del prototipo.

Esta separacion aplica responsabilidad unica y facilita la inversion de dependencias, porque los controladores dependen de servicios y los servicios dependen de contratos DAO.

## Frameworks Utilizados

- Spring Boot: base del proyecto y configuracion automatica.
- Spring MVC: controladores y vistas web.
- Spring Data JPA: persistencia con repositorios.
- Spring Security: autenticacion, autorizacion y logout.
- Thymeleaf: renderizado de vistas.
- Bootstrap: estilos base y componentes visuales.

## Librerias Java Utilizadas

- Google Guava: `Preconditions` y colecciones inmutables para reglas de negocio.
- Apache Commons Lang: validacion y limpieza de textos con `StringUtils`.
- Apache POI: generacion del reporte Excel `.xlsx`.
- Logback: registro de eventos importantes.
- JUnit 5: pruebas unitarias.
- Mockito: simulacion de dependencias en servicios.

## Herramientas

- Java 17.
- Maven.
- MySQL.
- Git.
- GitHub como repositorio remoto sugerido.
- Navegador web para validacion funcional.

## Cronograma De Trabajo Por Integrantes

| Periodo | Responsable | Entrega |
| --- | --- | --- |
| 28-30 mayo | GonzaChristian | Base del proyecto, arquitectura, Maven, MySQL y layout inicial. |
| 31 mayo - 1 junio | GonzaChristian | Login, roles, BCrypt, seguridad y dashboard. |
| 2-4 junio | Denis Rivera Limache | Gestion de conductores y vehiculos, validaciones y pruebas. |
| 5-6 junio | Moises Juarez | Registro de rutas y asignaciones con reglas de disponibilidad. |
| 7 junio | Moises Juarez | GPS simulado, control de recorrido e incidencias. |
| 8-9 junio | GonzaChristian | Reportes, filtros y exportacion Excel con Apache POI. |
| 10 junio | Equipo LOGISTECH | Pruebas finales, README, evidencias APF3 y preparacion de entrega. |

## Seguridad

La aplicacion implementa:

- Login en `/login`.
- Roles `ADMIN` y `SUPERVISOR`.
- Autorizacion diferenciada: el administrador gestiona maestros y asignaciones; el supervisor realiza consultas, control GPS, incidencias y reportes.
- Logout seguro.
- Proteccion CSRF de Spring Security.
- Contrasenas cifradas con BCrypt.
- Usuarios demo academicos iniciales:
  - `admin@logistech.local`
  - `supervisor@logistech.local`

No se incluyen contrasenas reales ni datos sensibles.

## Modulos Entregados

### Login Y Dashboard

- Login con Spring Security.
- Roles `ADMIN` y `SUPERVISOR`.
- Dashboard con indicadores de rutas, incidencias y GPS.

### Conductores

- Listar conductores.
- Registrar conductor.
- Editar conductor.
- Cambiar estado `ACTIVO` / `INACTIVO`.
- Validaciones con Commons Lang y Guava.

### Vehiculos

- Listar vehiculos.
- Registrar vehiculo.
- Editar vehiculo.
- Cambiar estado `DISPONIBLE`, `MANTENIMIENTO` o `INACTIVO`.
- Estado interno `ASIGNADO` para rutas activas.

### Rutas Y Asignaciones

- Registro de rutas.
- Edicion de rutas.
- Estados `PROGRAMADA`, `EN_CURSO`, `FINALIZADA`, `CANCELADA`.
- Asignacion de ruta a conductor y vehiculo.
- Validacion de conductor activo.
- Validacion de vehiculo disponible.
- Liberacion del vehiculo al finalizar o cancelar.
- Flujo controlado `PROGRAMADA -> EN_CURSO -> FINALIZADA`, con cancelacion durante estados activos.
- Bloqueo de reactivacion de asignaciones finalizadas o canceladas.
- Sincronizacion del estado de ruta desde la asignacion.
- Bloqueo de inactivacion de conductores y cambios de vehiculos con asignacion activa.

### GPS Simulado E Incidencias

- Registro de latitud y longitud.
- Fecha/hora automatica.
- Inicio automatico del recorrido al registrar GPS o incidencia.
- Registro de incidencias con estado `PENDIENTE`.
- Logs importantes para GPS e incidencias.

### Reportes

- Reporte de rutas.
- Filtros por estado, conductor y fecha programada.
- Exportacion Excel con Apache POI.
- Registro en Logback al exportar.

## Pruebas Unitarias

Se implementaron pruebas con JUnit 5 y Mockito para servicios principales:

- `ConductorServiceImplTest`
- `VehiculoServiceImplTest`
- `RutaServiceImplTest`
- `AsignacionRutaServiceImplTest`
- `RecorridoGpsServiceImplTest`
- `IncidenciaServiceImplTest`
- `ReporteServiceImplTest`
- `DashboardServiceImplTest`
- `LogistechUserDetailsServiceTest`
- `DataInitializerTest`
- `DemoDataInitializerTest`
- `TextValidatorTest`
- `MvcControllerTest`
- Pruebas de repositorios JPA con H2.

Resultado de verificacion:

```text
Tests run: 84, Failures: 0, Errors: 0
```

## Evidencia De SOLID

- Responsabilidad unica: controladores no contienen reglas de negocio; los servicios validan y ejecutan operaciones.
- Abierto/cerrado: nuevos modulos se agregan con nuevos DTO, servicios y vistas sin reescribir los modulos previos.
- Sustitucion de Liskov: las implementaciones DAO cumplen contratos definidos por interfaces.
- Segregacion de interfaces: cada DAO y service expone metodos del modulo correspondiente.
- Inversion de dependencias: servicios dependen de interfaces DAO, no directamente de detalles de persistencia.

## Evidencia De MVC

- Modelo: entidades y DTO.
- Vista: plantillas Thymeleaf en `src/main/resources/templates`.
- Controlador: clases en `controller`.
- Las vistas muestran datos y formularios, pero la logica de negocio esta en `service`.

## Evidencia De DAO

Cada modulo usa contratos de acceso:

- `ConductorDao`
- `VehiculoDao`
- `RutaDao`
- `AsignacionRutaDao`
- `RecorridoGpsDao`
- `IncidenciaDao`
- `ReporteDao`

Las implementaciones estan en `dao.impl` y delegan en `repository`.

## Evidencia De TDD

El desarrollo de las fases incluyo pruebas unitarias para validar reglas antes de cerrar cada modulo. Las pruebas verifican:

- Validaciones obligatorias.
- Duplicados.
- Cambios de estado.
- Transiciones validas e invalidas de asignaciones.
- Disponibilidad de conductor y vehiculo.
- Separacion de permisos entre administrador y supervisor.
- Registro GPS.
- Registro de incidencias.
- Filtros de reporte.
- Generacion de Excel.

## Git Y GitHub

El proyecto esta preparado para versionarse con Git. Archivos como `target/`, logs, configuraciones locales e IDE estan excluidos en `.gitignore`.

Comandos sugeridos:

```powershell
git add .
git commit -m "Avance APF3 funcional de LOGISTECH"
git branch -M main
git remote add origin https://github.com/USUARIO/REPOSITORIO.git
git push -u origin main
```

## Evaluacion De Temas Desarrollados En Clase

- Programacion orientada a objetos: entidades, DTO y servicios.
- Arquitectura MVC: separacion clara entre controlador, vista y modelo.
- Capas de negocio y persistencia: service, DAO y repository.
- Persistencia relacional: JPA sobre MySQL.
- Seguridad web: login, roles, BCrypt y logout.
- Pruebas unitarias: JUnit 5 y Mockito.
- Librerias externas: Guava, Commons Lang, POI y Logback.
- Reportes: exportacion Excel.
- Control de versiones: estructura lista para Git/GitHub.

## Como Verificar

1. Ejecutar MySQL.
2. Configurar variables de entorno `DB_USERNAME` y `DB_PASSWORD`.
3. Ejecutar:

```powershell
mvn spring-boot:run
```

4. Ingresar a:

```text
http://localhost:8080/login
```

5. Usar credenciales demo:

```text
admin@logistech.local / admin123
```

6. Ejecutar pruebas:

```powershell
mvn test
```
