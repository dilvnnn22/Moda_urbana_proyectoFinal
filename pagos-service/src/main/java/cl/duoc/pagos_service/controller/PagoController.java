package cl.duoc.pagos_service.controller;

import cl.duoc.pagos_service.model.Pago;
import cl.duoc.pagos_service.service.PagoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pagos")
@Tag(name = "Pagos", description = "Gestión de pagos de pedidos de Moda Urbana")
public class PagoController {

    private final PagoService service;

    public PagoController(PagoService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar pagos")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente") })
    public List<Pago> listar() {
        return service.listar();
    }

    @PostMapping
    @Operation(summary = "Registrar pago",
            description = "Registra un nuevo pago. El monto debe ser mayor a cero y el método de pago "
                        + "debe ser uno de: WEBPAY, TRANSFERENCIA, EFECTIVO, TARJETA_CREDITO. "
                        + "Si no se envía estado, se asigna PENDIENTE automáticamente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pago registrado",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(
                            value = "{ \"id\": 1, \"pedidoId\": 100, \"monto\": 19990.0, "
                                  + "\"metodoPago\": \"WEBPAY\", \"estado\": \"PENDIENTE\" }"
                    ))),
            @ApiResponse(responseCode = "400", description = "Monto inválido o método de pago no permitido",
                    content = @Content)
    })
    public ResponseEntity<?> guardar(@RequestBody Pago pago) {
        try {
            return ResponseEntity.ok(service.guardar(pago));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar pago por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pago encontrado"),
            @ApiResponse(responseCode = "404", description = "Pago no encontrado", content = @Content)
    })
    public ResponseEntity<Pago> buscar(
            @Parameter(description = "Id del pago") @PathVariable Long id) {
        Pago pago = service.buscar(id);
        return pago != null ? ResponseEntity.ok(pago) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar pago")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Pago eliminado") })
    public void eliminar(@Parameter(description = "Id del pago") @PathVariable Long id) {
        service.eliminar(id);
    }

    @PatchMapping("/{id}/estado")
    @Operation(summary = "Cambiar estado de un pago",
            description = "Cambia el estado de un pago. Transiciones permitidas: "
                        + "PENDIENTE → APROBADO | RECHAZADO. Un pago ANULADO no puede cambiar de estado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estado actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Transición de estado no permitida",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Pago no encontrado", content = @Content)
    })
    public ResponseEntity<?> cambiarEstado(
            @Parameter(description = "Id del pago") @PathVariable Long id,
            @Parameter(description = "Nuevo estado: APROBADO, RECHAZADO o ANULADO")
            @RequestParam String estado) {
        try {
            return ResponseEntity.ok(service.cambiarEstado(id, estado));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/pedido/{pedidoId}")
    @Operation(summary = "Obtener pagos de un pedido")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Pagos del pedido obtenidos") })
    public List<Pago> buscarPorPedido(
            @Parameter(description = "Id del pedido") @PathVariable Long pedidoId) {
        return service.buscarPorPedido(pedidoId);
    }
}
