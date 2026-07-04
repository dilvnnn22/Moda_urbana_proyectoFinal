package cl.duoc.pedidos_service.service;

import cl.duoc.pedidos_service.client.ClienteClient;
import cl.duoc.pedidos_service.client.ProductoClient;
import cl.duoc.pedidos_service.model.Pedido;
import cl.duoc.pedidos_service.repository.PedidoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias de PedidoService.
 * Se mockean PedidoRepository y los Feign Clients (ClienteClient, ProductoClient)
 * para aislar la lógica de negocio y validar la orquestación de comunicación
 * remota entre microservicios sin depender de que esos servicios estén activos.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PedidoService - pruebas unitarias")
class PedidoServiceTest {

    @Mock
    private PedidoRepository repository;

    @Mock
    private ClienteClient clienteClient;

    @Mock
    private ProductoClient productoClient;

    @InjectMocks
    private PedidoService service;

    private Pedido pedido;

    @BeforeEach
    void setUp() {
        pedido = new Pedido(1L, 10L, 20L, 2, LocalDate.of(2026, 6, 1), "PENDIENTE");
    }

    // ── listar ──────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("listar() debe retornar todos los pedidos del repositorio")
    void listar_devuelveTodosLosPedidos() {
        // Given
        when(repository.findAll()).thenReturn(List.of(pedido));

        // When
        List<Pedido> resultado = service.listar();

        // Then
        assertThat(resultado).containsExactly(pedido);
        verify(repository).findAll();
    }

    // ── guardar ─────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("guardar() debe delegar en repository.save() y retornar el pedido persistido")
    void guardar_persisteYRetornaElPedido() {
        // Given
        when(repository.save(pedido)).thenReturn(pedido);

        // When
        Pedido resultado = service.guardar(pedido);

        // Then
        assertThat(resultado).isEqualTo(pedido);
        verify(repository).save(pedido);
    }

    // ── buscar ──────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("buscar() con id existente debe retornar el pedido")
    void buscar_idExistente_retornaPedido() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(pedido));

        // When
        Pedido resultado = service.buscar(1L);

        // Then
        assertThat(resultado).isEqualTo(pedido);
    }

    @Test
    @DisplayName("buscar() con id inexistente debe retornar null (comportamiento actual del servicio)")
    void buscar_idInexistente_retornaNull() {
        // Given
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // When
        Pedido resultado = service.buscar(99L);

        // Then
        assertThat(resultado).isNull();
    }

    // ── eliminar ─────────────────────────────────────────────────────────────────

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

    // ── obtenerDetalle ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("obtenerDetalle() con pedido existente debe combinar datos de cliente y producto vía Feign")
    void obtenerDetalle_pedidoExistente_combinaClienteYProductoRemotos() {
        // Given
        Map<String, Object> clienteRemoto  = Map.of("id", 10L, "nombre", "Camila");
        Map<String, Object> productoRemoto = Map.of("id", 20L, "nombre", "Polera Oversize");

        when(repository.findById(1L)).thenReturn(Optional.of(pedido));
        when(clienteClient.obtenerCliente(10L)).thenReturn(clienteRemoto);
        when(productoClient.obtenerProducto(20L)).thenReturn(productoRemoto);

        // When
        Map<String, Object> resultado = service.obtenerDetalle(1L);

        // Then
        assertThat(resultado).containsEntry("pedido",   pedido);
        assertThat(resultado).containsEntry("cliente",  clienteRemoto);
        assertThat(resultado).containsEntry("producto", productoRemoto);
        verify(clienteClient).obtenerCliente(10L);
        verify(productoClient).obtenerProducto(20L);
    }

    @Test
    @DisplayName("obtenerDetalle() con pedido inexistente debe lanzar IllegalStateException " +
            "(el servicio valida la existencia antes de llamar a los servicios remotos)")
    void obtenerDetalle_pedidoInexistente_lanzaIllegalStateException() {
        // Given
        when(repository.findById(404L)).thenReturn(Optional.empty());

        // When / Then
        // El servicio lanza IllegalStateException de forma controlada (orElseThrow),
        // lo que evita un NullPointerException y permite manejar el error con un 404.
        assertThatThrownBy(() -> service.obtenerDetalle(404L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("404");

        // Verificación: los clientes remotos NO deben ser invocados si el pedido no existe
        verify(clienteClient, never()).obtenerCliente(any());
        verify(productoClient, never()).obtenerProducto(any());
    }
}
