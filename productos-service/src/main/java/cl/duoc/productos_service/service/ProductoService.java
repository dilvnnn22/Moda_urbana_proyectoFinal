package cl.duoc.productos_service.service;

import cl.duoc.productos_service.client.CategoriaClient;
import cl.duoc.productos_service.client.MarcaClient;
import cl.duoc.productos_service.dto.ProductoDTO;
import cl.duoc.productos_service.dto.ProductoDetalleDTO;
import cl.duoc.productos_service.model.Producto;
import cl.duoc.productos_service.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductoService {

    private final ProductoRepository repository;
    private final MarcaClient marcaClient;
    private final CategoriaClient categoriaClient;

    public ProductoService(ProductoRepository repository,
                           MarcaClient marcaClient,
                           CategoriaClient categoriaClient) {
        this.repository = repository;
        this.marcaClient = marcaClient;
        this.categoriaClient = categoriaClient;
    }

    public List<Producto> listar() {
        return repository.findAll();
    }

    /**
     * Retorna todos los productos enriquecidos con nombre de marca y categoría,
     * consultando a marcas-service y categorias-service via Feign Client.
     */
    public List<ProductoDetalleDTO> listarConDetalle() {
        return repository.findAll()
                .stream()
                .map(this::toDetalle)
                .collect(Collectors.toList());
    }

    public Producto guardar(Producto producto) {
        return repository.save(producto);
    }

    public Producto buscar(Long id) {
        return repository.findById(id).orElse(null);
    }

    /**
     * Busca un producto por id y enriquece la respuesta con nombre de marca y categoría.
     */
    public ProductoDetalleDTO buscarConDetalle(Long id) {
        Producto p = repository.findById(id).orElse(null);
        if (p == null) return null;
        return toDetalle(p);
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }

    public List<ProductoDTO> listadoDTO() {
        return repository.findAll()
                .stream()
                .map(producto -> new ProductoDTO(
                        producto.getNombre(),
                        producto.getPrecio()
                ))
                .collect(Collectors.toList());
    }

    public List<Producto> buscarPorPrecio(Double precio) {
        return repository.findByPrecioLessThan(precio);
    }

    public List<Producto> stockBajo(Integer stock) {
        return repository.findByStockLessThan(stock);
    }

    public List<Producto> buscarPorMarca(Long marcaId) {
        return repository.findByMarcaId(marcaId);
    }

    public List<Producto> buscarPorCategoria(Long categoriaId) {
        return repository.findByCategoriaId(categoriaId);
    }

    // ── helper privado ────────────────────────────────────────────────────────

    private ProductoDetalleDTO toDetalle(Producto p) {
        String nombreMarca = "Marca no disponible";
        String nombreCategoria = "Categoría no disponible";

        try {
            Map<String, Object> marca = marcaClient.obtenerMarca(p.getMarcaId());
            if (marca != null && marca.get("nombre") != null) {
                nombreMarca = (String) marca.get("nombre");
            }
        } catch (Exception ex) {
            // marcas-service no disponible o marcaId inexistente: se deja el valor por defecto
        }

        try {
            Map<String, Object> categoria = categoriaClient.obtenerCategoria(p.getCategoriaId());
            if (categoria != null && categoria.get("nombre") != null) {
                nombreCategoria = (String) categoria.get("nombre");
            }
        } catch (Exception ex) {
            // categorias-service no disponible o categoriaId inexistente: se deja el valor por defecto
        }

        return new ProductoDetalleDTO(
                p.getId(),
                p.getNombre(),
                p.getDescripcion(),
                p.getPrecio(),
                p.getStock(),
                nombreMarca,
                nombreCategoria
        );
    }
}
