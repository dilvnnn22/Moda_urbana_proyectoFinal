package cl.duoc.inventario_service.controller;

import cl.duoc.inventario_service.dto.InventarioDTO;
import cl.duoc.inventario_service.model.Inventario;
import cl.duoc.inventario_service.service.InventarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventario")
@Tag(name = "Inventario", description = "Gestión de stock por bodega de Moda Urbana")
public class InventarioController {

    private final InventarioService service;

    public InventarioController(InventarioService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar registros de inventario")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente") })
    public List<Inventario> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar registro de inventario por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Registro encontrado"),
            @ApiResponse(responseCode = "404", description = "Registro no encontrado", content = @Content)
    })
    public Inventario buscar(@Parameter(description = "Id del registro de inventario") @PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping
    @Operation(summary = "Crear registro de inventario")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Registro creado",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(
                            value = "{ \"id\": 1, \"productoId\": 5, \"cantidadDisponible\": 40, "
                                  + "\"ubicacionBodega\": \"Bodega Central\" }"
                    ))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
    })
    public Inventario guardar(@RequestBody Inventario inventario) {
        return service.guardar(inventario);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar registro de inventario existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Registro actualizado"),
            @ApiResponse(responseCode = "404", description = "Registro no encontrado", content = @Content)
    })
    public Inventario actualizar(@Parameter(description = "Id del registro") @PathVariable Long id,
                                 @RequestBody Inventario inventario) {
        inventario.setId(id);
        return service.guardar(inventario);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar registro de inventario")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Registro eliminado") })
    public void eliminar(@Parameter(description = "Id del registro") @PathVariable Long id) {
        service.eliminar(id);
    }

    @GetMapping("/listado")
    @Operation(summary = "Listado resumido de inventario (DTO)",
            description = "Retorna productoId y cantidadDisponible de cada registro")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = InventarioDTO.class)))
    })
    public List<InventarioDTO> listado() {
        return service.listadoDTO();
    }
}
