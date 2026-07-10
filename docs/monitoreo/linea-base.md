# Linea base de monitoreo LOGISTECH

Fecha de medicion: 2026-07-10  
Rama: `feature/monitoreo`  
Checkpoint Git previo: `e9638ee chore: checkpoint before monitoring baseline`  
Perfil usado: `demo` con H2 en memoria  
Aplicacion: `http://localhost:8080`  
Proceso Java medido: PID `9660`

## Objetivo

Establecer una linea base local para la seccion 2.1 del Plan de Monitoreo de Aplicaciones Web de LOGISTECH, usando Spring Boot Actuator, Micrometer Prometheus y una carga controlada contra la ruta publica `/login`.

## Configuracion aplicada

- Dependencias agregadas: `spring-boot-starter-actuator` y `micrometer-registry-prometheus`.
- Endpoints expuestos: `/actuator/health`, `/actuator/info`, `/actuator/metrics`, `/actuator/prometheus`.
- Seguridad: solo los endpoints anteriores quedaron permitidos sin autenticacion; las rutas funcionales como `/dashboard` siguen protegidas.
- Prometheus/Grafana local: configuracion reproducible en `docker-compose.yml` y `monitoring/`.

Docker no estaba instalado en esta maquina (`docker` no fue reconocido como comando), por eso no se levanto Prometheus/Grafana durante esta medicion y no se instalo nada.

## Prueba controlada

Duracion: 30 minutos  
Intervalo de registro: 5 minutos  
Carga: 12 solicitudes por minuto a `GET /login`  
Fuente de metricas:

- CPU del proceso: `process.cpu.usage` de Actuator.
- RAM del proceso: `Get-Process -Id 9660`.
- Heap JVM: `jvm.memory.used` con tag `area:heap`.
- Tiempo promedio de respuesta: `http.server.requests` para `uri:/login`.
- Latencia p95: buckets `http_server_requests_seconds_bucket` desde `/actuator/prometheus`.
- Peticiones por minuto: contador `http.server.requests` para `uri:/login`.
- Errores 5xx: contador `http.server.requests` con `uri:/login` y `status:500`.
- Disco usado: `disk.total - disk.free`.

## Resultados

| Metrica | Promedio |
| --- | ---: |
| CPU del proceso (%) | 0.046433 |
| RAM del proceso (MB) | 272.130581 |
| Heap JVM usado (MB) | 72.457690 |
| Tiempo de respuesta promedio `/login` (ms) | 4.693710 |
| Latencia p95 `/login` (ms) | 8.155590 |
| Peticiones por minuto `/login` | 11.859916 |
| Tasa de errores HTTP 5xx `/login` (%) | 0 |
| Disco usado (MB) | 81350.719308 |

Los promedios de CPU, RAM, heap y disco usan todas las muestras registradas. Los promedios de trafico excluyen el minuto 0 porque aun no habia solicitudes acumuladas dentro de la ventana de prueba.

## Datos registrados

Los datos completos estan en `docs/monitoreo/linea-base.csv`.

## Verificaciones

- `mvn test`: 79 pruebas ejecutadas, 0 fallos, 0 errores.
- `/actuator/health`: `{"status":"UP"}`.
- `/actuator/info`: HTTP 200.
- `/actuator/metrics`: HTTP 200.
- `/actuator/prometheus`: HTTP 200.
- `/dashboard` sin autenticacion: HTTP 302, se mantiene protegido.

## Repetir la medicion

1. Ejecutar pruebas:

```powershell
mvn test
```

2. Empaquetar la aplicacion:

```powershell
mvn package -DskipTests
```

3. Levantar LOGISTECH con perfil demo:

```powershell
java -jar target\logistech-0.0.1-SNAPSHOT.jar --spring.profiles.active=demo
```

4. En otra terminal, obtener el PID Java de la aplicacion:

```powershell
Get-Process java | Select-Object Id,ProcessName,StartTime,Path
```

5. Ejecutar la medicion, reemplazando `<PID>` por el proceso Java real:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\scripts\measure-linea-base.ps1 -BaseUrl http://localhost:8080 -AppProcessId <PID> -DurationMinutes 30 -IntervalMinutes 5 -RequestsPerMinute 12 -OutputCsv docs\monitoreo\linea-base.csv
```

6. Opcionalmente levantar Prometheus y Grafana si Docker esta instalado:

```powershell
docker compose up -d
```

Prometheus quedara en `http://localhost:9090` y Grafana en `http://localhost:3000` con usuario `admin` y contrasena `admin`.
