package cl.duoc.productos_service.service;

import cl.duoc.productos_service.client.CategoriaClient;
import cl.duoc.productos_service.client.MarcaClient;
import cl.duoc.productos_service.dto.ProductoDTO;
import cl.duoc.productos_service.dto.ProductoDetalleDTO;
import cl.duoc.productos_service.model.Producto;
import cl.duoc.productos_service.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias de ProductoService.
 * Se mockean ProductoRepository, MarcaClient y CategoriaClient para aislar
 * completamente la lógica de negocio del acceso a datos y comunicación remota.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ProductoService - pruebas unitarias")
class ProductoServiceTest {

    @Mock
    private ProductoRepository repository;

    @Mock
    private MarcaClient marcaClient;

    @Mock
    private CategoriaClient categoriaClient;

    @InjectMocks
    private ProductoService service;

    private Producto producto;

    @BeforeEach
    void setUp() {
        producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Polera Oversize");
        producto.setDescripcion("Polera oversize algodón");
        producto.setPrecio(19990.0);
        producto.setStock(15);
        producto.setMarcaId(1L);
        producto.setCategoriaId(2L);
    }

    // ── listarConDetalle ─────────────────────────────────────────────────────

    @Test
    @DisplayName("listarConDetalle() debe retornar productos con nombre de marca y categoría resueltos")
    void listarConDetalle_retornaProductosConNombreDeMarcaYCategoria() {
        // Given
        when(repository.findAll()).thenReturn(List.of(producto));
        when(marcaClient.obtenerMarca(1L)).thenReturn(Map.of("nombre", "UrbanWear"));
        when(categoriaClient.obtenerCategoria(2L)).thenReturn(Map.of("nombre", "Poleras"));

        // When
        List<ProductoDetalleDTO> resultado = service.listarConDetalle();

        // Then
        assertThat(resultado).hasSize(1);
        ProductoDetalleDTO dto = resultado.get(0);
        assertThat(dto.getNombreMarca()).isEqualTo("UrbanWear");
        assertThat(dto.getNombreCategoria()).isEqualTo("Poleras");
        assertThat(dto.getNombre()).isEqualTo("Polera Oversize");

        verify(marcaClient).obtenerMarca(1L);
        verify(categoriaClient).obtenerCategoria(2L);
    }

    // ── buscarConDetalle ─────────────────────────────────────────────────────

    @Test
    @DisplayName("buscarConDetalle() con id existente debe retornar el DTO con nombre de marca y categoría")
    void buscarConDetalle_idExistente_retornaDTOEnriquecido() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(producto));
        when(marcaClient.obtenerMarca(1L)).thenReturn(Map.of("nombre", "UrbanWear"));
        when(categoriaClient.obtenerCategoria(2L)).thenReturn(Map.of("nombre", "Poleras"));

        // When
        ProductoDetalleDTO resultado = service.buscarConDetalle(1L);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getNombreMarca()).isEqualTo("UrbanWear");
        assertThat(resultado.getNombreCategoria()).isEqualTo("Poleras");
    }

    @Test
    @DisplayName("buscarConDetalle() con id inexistente debe retornar null sin llamar a los clients remotos")
    void buscarConDetalle_idInexistente_retornaNull() {
        // Given
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // When
        ProductoDetalleDTO resultado = service.buscarConDetalle(99L);

        // Then
        assertThat(resultado).isNull();
        verify(marcaClient, never()).obtenerMarca(any());
        verify(categoriaClient, never()).obtenerCategoria(any());
    }

    // ── guardar ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("guardar() debe delegar en repository.save() y retornar el producto persistido")
    void guardar_persisteYRetornaElProducto() {
        // Given
        when(repository.save(producto)).thenReturn(producto);

        // When
        Producto resultado = service.guardar(producto);

        // Then
        assertThat(resultado).isEqualTo(producto);
        verify(repository).save(producto);
    }

    // ── eliminar ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("eliminar() debe invocar deleteById() en el repositorio")
    void eliminar_invocaDeleteByIdEnRepository() {
        // Given
        doNothing().when(repository).deleteById(1L);

        // When
        service.eliminar(1L);

        // Then
        verify(repository, times(1)).deleteById(1L);
    }

    // ── listadoDTO ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("listadoDTO() debe mapear correctamente Producto a ProductoDTO (solo nombre y precio)")
    void listadoDTO_mapeaCorrectamenteAProductoDTO() {
        // Given
        when(repository.findAll()).thenReturn(List.of(producto));

        // When
        List<ProductoDTO> resultado = service.listadoDTO();

        // Then
        assertThat(resultado).hasSize(1);
        ProductoDTO dto = resultado.get(0);
        assertThat(dto.getNombreProducto()).isEqualTo("Polera Oversize");
        assertThat(dto.getPrecio()).isEqualTo(19990.0);
    }

    // ── buscarPorPrecio ───────────────────────────────────────────────────────

    @Test
    @DisplayName("buscarPorPrecio() debe delegar en findByPrecioLessThan() con el valor recibido")
    void buscarPorPrecio_delegaEnRepositoryConElPrecioCorrecto() {
        // Given
        when(repository.findByPrecioLessThan(20000.0)).thenReturn(List.of(producto));

        // When
        List<Producto> resultado = service.buscarPorPrecio(20000.0);

        // Then
        assertThat(resultado).containsExactly(producto);
        verify(repository).findByPrecioLessThan(20000.0);
    }

    // ── stockBajo ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("stockBajo() debe delegar en findByStockLessThan() con el umbral recibido")
    void stockBajo_delegaEnRepositoryConElUmbralCorrecto() {
        // Given
        when(repository.findByStockLessThan(10)).thenReturn(List.of());

        // When
        List<Producto> resultado = service.stockBajo(10);

        // Then
        assertThat(resultado).isEmpty();
        verify(repository).findByStockLessThan(10);
    }

    // ── buscarPorMarca ────────────────────────────────────────────────────────

    @Test
    @DisplayName("buscarPorMarca() debe delegar en findByMarcaId() con el id de marca recibido")
    void buscarPorMarca_delegaEnRepositoryConIdDeMarca() {
        // Given
        when(repository.findByMarcaId(1L)).thenReturn(List.of(producto));

        // When
        List<Producto> resultado = service.buscarPorMarca(1L);

        // Then
        assertThat(resultado).containsExactly(producto);
        verify(repository).findByMarcaId(1L);
    }

    // ── buscarPorCategoria ────────────────────────────────────────────────────

    @Test
    @DisplayName("buscarPorCategoria() debe delegar en findByCategoriaId() con el id de categoría recibido")
    void buscarPorCategoria_delegaEnRepositoryConIdDeCategoria() {
        // Given
        when(repository.findByCategoriaId(2L)).thenReturn(List.of(producto));

        // When
        List<Producto> resultado = service.buscarPorCategoria(2L);

        // Then
        assertThat(resultado).containsExactly(producto);
        verify(repository).findByCategoriaId(2L);
    }
}
