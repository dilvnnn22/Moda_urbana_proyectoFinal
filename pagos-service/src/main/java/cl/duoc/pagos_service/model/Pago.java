package cl.duoc.pagos_service.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pagos")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    private Long pedidoId;

    private Double monto;

    private String metodoPago;

    private String estado;
}