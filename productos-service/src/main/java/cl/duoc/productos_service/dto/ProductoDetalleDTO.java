package cl.duoc.productos_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * DTO enriquecido de Producto que reemplaza marcaId y categoriaId
 * por los nombres reales obtenidos desde marcas-service y categorias-service.
 * Se usa en el endpoint GET /productos/detalle/{id}
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Detalle de producto con nombres de marca y categoría resueltos")
public class ProductoDetalleDTO {

    @Schema(description = "Id del producto", example = "1")
    private Long id;

    @Schema(description = "Nombre del producto", example = "Polera Oversize")
    private String nombre;

    @Schema(description = "Descripción del producto", example = "Polera 100% algodón")
    private String descripcion;

    @Schema(description = "Precio de venta", example = "19990.0")
    private Double precio;

    @Schema(description = "Unidades disponibles", example = "40")
    private Integer stock;

    @Schema(description = "Nombre de la marca (resuelto desde marcas-service)", example = "UrbanWear")
    private String nombreMarca;

    @Schema(description = "Nombre de la categoría (resuelto desde categorias-service)", example = "Poleras")
    private String nombreCategoria;
}
