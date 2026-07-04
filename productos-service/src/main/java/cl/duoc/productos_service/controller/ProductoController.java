package cl.duoc.productos_service.controller;

import cl.duoc.productos_service.dto.ProductoDTO;
import cl.duoc.productos_service.dto.ProductoDetalleDTO;
import cl.duoc.productos_service.exception.ProductoNotFoundException;
import cl.duoc.productos_service.model.Producto;
import cl.duoc.productos_service.service.ProductoService;
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
@RequestMapping("/api/v1/productos")
@Tag(name = "Productos", description = "Catálogo de productos de Moda Urbana")
public class ProductoController {

    private final ProductoService service;

    public ProductoController(ProductoService service) {
        this.service = service;
    }

    @GetMapping(produces = "application/json")
    @Operation(summary = "Listar productos con nombre de marca y categoría",
            description = "Retorna todos los productos enriquecidos con el nombre real de marca y categoría.")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente",
            content = @Content(mediaType = "application/json",
                    array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @Schema(implementation = ProductoDetalleDTO.class)))) })
    public List<ProductoDetalleDTO> listar() {
        return service.listarConDetalle();
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    @Operation(summary = "Crear producto")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Producto a crear",
            required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Producto.class),
                    examples = @ExampleObject(
                            value = "{ \"nombre\": \"Polera Oversize\", \"descripcion\": \"Polera oversize algodón\", "
                                  + "\"precio\": 19990.0, \"stock\": 15, \"marcaId\": 1, \"categoriaId\": 2 }"
                    ))
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto creado",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(
                            value = "{ \"id\": 1, \"nombre\": \"Polera Oversize\", \"descripcion\": \"Polera oversize algodón\", "
                                  + "\"precio\": 19990.0, \"stock\": 15, \"marcaId\": 1, \"categoriaId\": 2 }"
                    ))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
    })
    public Producto guardar(@org.springframework.web.bind.annotation.RequestBody Producto producto) {
        return service.guardar(producto);
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    @Operation(summary = "Buscar producto por id con nombre de marca y categoría",
            description = "Retorna el producto enriquecido con los nombres reales de marca y categoría.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductoDetalleDTO.class))),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado", content = @Content)
    })
    public ProductoDetalleDTO buscar(@Parameter(description = "Id del producto",example = "1") @PathVariable Long id) {
        ProductoDetalleDTO detalle = service.buscarConDetalle(id);
        if (detalle == null) throw new ProductoNotFoundException("Producto no encontrado con id: " + id);
        return detalle;
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar producto")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Producto eliminado") })
    public void eliminar(@Parameter(description = "Id del producto") @PathVariable Long id) {
        service.eliminar(id);
    }

    @GetMapping(value = "/listado", produces = "application/json")
    @Operation(summary = "Listado resumido de productos (DTO)", description = "Retorna solo nombre y precio")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductoDTO.class)))
    })
    public List<ProductoDTO> listadoDTO() {
        return service.listadoDTO();
    }

    @GetMapping(value = "/precio/{precio}", produces = "application/json")
    @Operation(summary = "Buscar productos bajo un precio máximo",
            description = "Retorna los productos cuyo precio es menor al valor indicado")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente",
            content = @Content(mediaType = "application/json",
                    array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @Schema(implementation = Producto.class)))) })
    public List<Producto> precio(@Parameter(description = "Precio máximo de referencia") @PathVariable Double precio) {
        return service.buscarPorPrecio(precio);
    }

    @GetMapping(value = "/stock/{stock}", produces = "application/json")
    @Operation(summary = "Buscar productos con stock bajo",
            description = "Retorna los productos cuyo stock es menor al umbral indicado")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente",
            content = @Content(mediaType = "application/json",
                    array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @Schema(implementation = Producto.class)))) })
    public List<Producto> stock(@Parameter(description = "Umbral de stock") @PathVariable Integer stock) {
        return service.stockBajo(stock);
    }

    @GetMapping(value = "/marca/{marcaId}", produces = "application/json")
    @Operation(summary = "Buscar productos por marca")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente",
            content = @Content(mediaType = "application/json",
                    array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @Schema(implementation = Producto.class)))) })
    public List<Producto> marca(@Parameter(description = "Id de la marca") @PathVariable Long marcaId) {
        return service.buscarPorMarca(marcaId);
    }

    @GetMapping(value = "/categoria/{categoriaId}", produces = "application/json")
    @Operation(summary = "Buscar productos por categoría")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente",
            content = @Content(mediaType = "application/json",
                    array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @Schema(implementation = Producto.class)))) })
    public List<Producto> categoria(@Parameter(description = "Id de la categoría") @PathVariable Long categoriaId) {
        return service.buscarPorCategoria(categoriaId);
    }

    @GetMapping(value = "/detalle/{id}", produces = "application/json")
    @Operation(summary = "Detalle de producto con nombre de marca y categoría",
            description = "Retorna el producto con los nombres reales de marca y categoría, " +
                        "obtenidos desde marcas-service y categorias-service via Feign Client.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Detalle obtenido correctamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductoDetalleDTO.class))),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error al consultar marcas-service o categorias-service",
                    content = @Content)
    })
    public ProductoDetalleDTO detalle(@Parameter(description = "Id del producto") @PathVariable Long id) {
        ProductoDetalleDTO detalle = service.buscarConDetalle(id);
        if (detalle == null) throw new ProductoNotFoundException("Producto no encontrado con id: " + id);
        return detalle;
    }
}
