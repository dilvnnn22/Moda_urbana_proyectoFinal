package cl.duoc.clientes_service.repository;


import cl.duoc.clientes_service.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}
