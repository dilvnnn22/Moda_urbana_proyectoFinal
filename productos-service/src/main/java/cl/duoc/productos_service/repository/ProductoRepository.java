package cl.duoc.productos_service.repository;

import cl.duoc.productos_service.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByPrecioLessThan(Double precio);

    List<Producto> findByStockLessThan(Integer stock);

    List<Producto> findByMarcaId(Long marcaId);

    List<Producto> findByCategoriaId(Long categoriaId);
}

