package cl.duoc.inventario_service.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class InventarioDTO {

    private Long productoId;

    private Integer cantidadDisponible;
}