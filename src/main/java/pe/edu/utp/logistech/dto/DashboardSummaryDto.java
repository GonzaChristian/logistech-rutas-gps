package pe.edu.utp.logistech.dto;

public record DashboardSummaryDto(
        long rutasRegistradas,
        long rutasEnCurso,
        long incidenciasRegistradas,
        long rutasFinalizadas,
        long gpsRegistrados
) {
}
