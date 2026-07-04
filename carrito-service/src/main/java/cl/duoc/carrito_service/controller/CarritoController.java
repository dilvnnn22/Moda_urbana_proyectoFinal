package cl.duoc.carrito_service.controller;

import cl.duoc.carrito_service.model.Carrito;
import cl.duoc.carrito_service.service.CarritoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/carrito")
@Tag(name = "Carrito", description = "Gestión del carrito de compras de Moda Urbana")
public class CarritoController {

    private final CarritoService service;

    public CarritoController(CarritoService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar ítems de carrito")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente") })
    public List<Carrito> listar() {
        return service.listar();
    }

    @PostMapping
    @Operation(summary = "Agregar ítem al carrito")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ítem agregado",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(
                            value = "{ \"id\": 1, \"clienteId\": 10, \"productoId\": 5, \"cantidad\": 2 }"
                    ))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
    })
    public Carrito guardar(@RequestBody Carrito carrito) {
        return service.guardar(carrito);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar ítem de carrito por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ítem encontrado"),
            @ApiResponse(responseCode = "404", description = "Ítem no encontrado", content = @Content)
    })
    public Carrito buscar(@Parameter(description = "Id del ítem de carrito") @PathVariable Long id) {
        return service.buscar(id);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar ítem de carrito")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Ítem eliminado") })
    public void eliminar(@Parameter(description = "Id del ítem de carrito") @PathVariable Long id) {
        service.eliminar(id);
    }
}
