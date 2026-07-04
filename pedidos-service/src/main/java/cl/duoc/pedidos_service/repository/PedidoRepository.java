package cl.duoc.pedidos_service.repository;


import cl.duoc.pedidos_service.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
}
