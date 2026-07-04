package cl.duoc.pedidos_service.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class PedidoDTO {

    private Long clienteId;

    private Long productoId;

    private Integer cantidad;

    private String estado;
}
