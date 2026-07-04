package cl.duoc.marcas_service.controller;

import cl.duoc.marcas_service.dto.MarcaDTO;
import cl.duoc.marcas_service.model.Marca;
import cl.duoc.marcas_service.service.MarcaService;
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
@RequestMapping("/api/v1/marcas")
@Tag(name = "Marcas", description = "Gestión de marcas de productos de Moda Urbana")
public class MarcaController {

    private final MarcaService service;

    public MarcaController(MarcaService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar marcas")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente") })
    public List<Marca> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar marca por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Marca encontrada"),
            @ApiResponse(responseCode = "404", description = "Marca no encontrada", content = @Content)
    })
    public Marca buscar(@Parameter(description = "Id de la marca") @PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping
    @Operation(summary = "Crear marca")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Marca creada",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(
                            value = "{ \"id\": 1, \"nombre\": \"UrbanWear\", \"paisOrigen\": \"Chile\" }"
                    ))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
    })
    public Marca guardar(@RequestBody Marca marca) {
        return service.guardar(marca);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar marca existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Marca actualizada"),
            @ApiResponse(responseCode = "404", description = "Marca no encontrada", content = @Content)
    })
    public Marca actualizar(@Parameter(description = "Id de la marca") @PathVariable Long id,
                            @RequestBody Marca marca) {
        marca.setId(id);
        return service.guardar(marca);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar marca")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Marca eliminada") })
    public void eliminar(@Parameter(description = "Id de la marca") @PathVariable Long id) {
        service.eliminar(id);
    }

    @GetMapping("/listado")
    @Operation(summary = "Listado resumido de marcas (DTO)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MarcaDTO.class)))
    })
    public List<MarcaDTO> listado() {
        return service.listadoDTO();
    }
}
