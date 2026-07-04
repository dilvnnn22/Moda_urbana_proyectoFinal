package cl.duoc.pedidos_service.controller;

import cl.duoc.pedidos_service.model.Pedido;
import cl.duoc.pedidos_service.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/pedidos")
@Tag(name = "Pedidos", description = "Gestión de pedidos de Moda Urbana. " +
        "El endpoint de detalle consume productos-service y clientes-service vía Feign Client.")
public class PedidoController {

    private final PedidoService service;

    public PedidoController(PedidoService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar pedidos")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente") })
    public List<Pedido> listar() {
        return service.listar();
    }

    @PostMapping
    @Operation(summary = "Crear pedido")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido creado",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(
                            value = "{ \"id\": 1, \"clienteId\": 10, \"productoId\": 20, \"cantidad\": 2, "
                                  + "\"fecha\": \"2026-06-01\", \"estado\": \"PENDIENTE\" }"
                    ))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
    })
    public Pedido guardar(@RequestBody Pedido pedido) {
        return service.guardar(pedido);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar pedido por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido encontrado"),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado", content = @Content)
    })
    public Pedido buscar(@Parameter(description = "Id del pedido") @PathVariable Long id) {
        return service.buscar(id);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar pedido")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Pedido eliminado") })
    public void eliminar(@Parameter(description = "Id del pedido") @PathVariable Long id) {
        service.eliminar(id);
    }

    @GetMapping("/detalle/{id}")
    @Operation(summary = "Obtener detalle de pedido enriquecido",
            description = "Combina el pedido con los datos del cliente (clientes-service) y del producto "
                    + "(productos-service), consultados en tiempo real vía Feign Client.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Detalle obtenido correctamente",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(
                            value = "{ \"pedido\": { \"id\": 1, \"clienteId\": 10, \"productoId\": 20 }, "
                                  + "\"cliente\": { \"id\": 10, \"nombre\": \"Camila\" }, "
                                  + "\"producto\": { \"id\": 20, \"nombre\": \"Polera Oversize\" } }"
                    ))),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error al consultar productos-service o clientes-service",
                    content = @Content)
    })
    public Map<String, Object> detalle(@Parameter(description = "Id del pedido") @PathVariable Long id) {
        return service.obtenerDetalle(id);
    }
}
