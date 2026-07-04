package cl.duoc.clientes_service.exception;

import java.time.LocalDateTime;

public class ErrorResponse {
    private int status;
    private String mensaje;
    private LocalDateTime timestamp;

    public ErrorResponse(int status, String mensaje, LocalDateTime timestamp) {
        this.status = status;
        this.mensaje = mensaje;
        this.timestamp = timestamp;
    }
    public int getStatus() { return status; }
    public String getMensaje() { return mensaje; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
