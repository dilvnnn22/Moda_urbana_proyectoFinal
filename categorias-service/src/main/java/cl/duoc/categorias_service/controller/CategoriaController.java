package cl.duoc.categorias_service.controller;

import cl.duoc.categorias_service.dto.CategoriaDTO;
import cl.duoc.categorias_service.model.Categoria;
import cl.duoc.categorias_service.service.CategoriaService;
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
@RequestMapping("/api/v1/categorias")
@Tag(name = "Categorías", description = "Gestión de categorías de productos de Moda Urbana")
public class CategoriaController {

    private final CategoriaService service;

    public CategoriaController(CategoriaService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar categorías", description = "Retorna todas las categorías registradas")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente")
    })
    public List<Categoria> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar categoría por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categoría encontrada"),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada", content = @Content)
    })
    public Categoria buscar(@Parameter(description = "Id de la categoría") @PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping
    @Operation(summary = "Crear categoría", description = "Registra una nueva categoría de productos")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categoría creada",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(
                            name = "Categoría creada",
                            value = "{ \"id\": 1, \"nombre\": \"Poleras\", \"descripcion\": \"Poleras y polerones urbanos\" }"
                    ))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
    })
    public Categoria guardar(@org.springframework.web.bind.annotation.RequestBody
                              @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                      description = "Categoría a crear",
                                      content = @Content(mediaType = "application/json", examples = @ExampleObject(
                                              value = "{ \"nombre\": \"Poleras\", \"descripcion\": \"Poleras y polerones urbanos\" }"
                                      ))) Categoria categoria) {
        return service.guardar(categoria);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar categoría existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categoría actualizada"),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada", content = @Content)
    })
    public Categoria actualizar(@Parameter(description = "Id de la categoría") @PathVariable Long id,
                                @RequestBody Categoria categoria) {
        categoria.setId(id);
        return service.guardar(categoria);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar categoría")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categoría eliminada")
    })
    public void eliminar(@Parameter(description = "Id de la categoría") @PathVariable Long id) {
        service.eliminar(id);
    }

    @GetMapping("/listado")
    @Operation(summary = "Listado resumido de categorías (DTO)",
            description = "Retorna solo el nombre de cada categoría, pensado para selects/combos en el frontend")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoriaDTO.class)))
    })
    public List<CategoriaDTO> listado() {
        return service.listadoDTO();
    }
}
