package cl.duoc.categorias_service.repository;



import cl.duoc.categorias_service.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}
