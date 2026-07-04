package cl.duoc.carrito_service.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "carrito")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Carrito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    private Long clienteId;

    private Long productoId;

    private Integer cantidad;
}
