package cl.duoc.pagos_service.service;

import cl.duoc.pagos_service.model.Pago;
import cl.duoc.pagos_service.repository.PagoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias de PagoService.
 * Se mockea PagoRepository para aislar la lógica de negocio del acceso a datos.
 * Se validan las reglas de dominio: monto mínimo, método de pago aceptado,
 * estado por defecto y transiciones de estado permitidas.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PagoService - pruebas unitarias")
class PagoServiceTest {

    @Mock
    private PagoRepository repository;

    @InjectMocks
    private PagoService service;

    private Pago pagoValido;

    @BeforeEach
    void setUp() {
        pagoValido = new Pago(1L, 100L, 19990.0, "WEBPAY", "APROBADO");
    }

    // ── listar ──────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("listar() debe retornar todos los pagos del repositorio")
    void listar_devuelveTodosLosPagos() {
        // Given
        when(repository.findAll()).thenReturn(List.of(pagoValido));

        // When
        List<Pago> resultado = service.listar();

        // Then
        assertThat(resultado).containsExactly(pagoValido);
        verify(repository).findAll();
    }

    // ── guardar ─────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("guardar() con datos válidos debe persistir el pago y retornarlo")
    void guardar_datosValidos_persisteYRetornaElPago() {
        // Given
        when(repository.save(pagoValido)).thenReturn(pagoValido);

        // When
        Pago resultado = service.guardar(pagoValido);

        // Then
        assertThat(resultado).isEqualTo(pagoValido);
        assertThat(resultado.getEstado()).isEqualTo("APROBADO");
        verify(repository).save(pagoValido);
    }

    @Test
    @DisplayName("guardar() sin estado debe asignar 'PENDIENTE' como estado por defecto")
    void guardar_sinEstado_asignaPendientePorDefecto() {
        // Given
        Pago sinEstado = new Pago(null, 50L, 5000.0, "TRANSFERENCIA", null);
        Pago persistido = new Pago(2L, 50L, 5000.0, "TRANSFERENCIA", "PENDIENTE");
        when(repository.save(any())).thenReturn(persistido);

        // When
        Pago resultado = service.guardar(sinEstado);

        // Then
        // El servicio asigna PENDIENTE antes de llamar a save
        assertThat(sinEstado.getEstado()).isEqualTo("PENDIENTE");
        verify(repository).save(sinEstado);
    }

    @Test
    @DisplayName("guardar() con monto nulo debe lanzar IllegalArgumentException")
    void guardar_montoNulo_lanzaExcepcion() {
        // Given
        Pago sinMonto = new Pago(null, 100L, null, "WEBPAY", null);

        // When / Then
        assertThatThrownBy(() -> service.guardar(sinMonto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("monto");

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("guardar() con monto cero debe lanzar IllegalArgumentException")
    void guardar_montoCero_lanzaExcepcion() {
        // Given
        Pago montoCero = new Pago(null, 100L, 0.0, "WEBPAY", null);

        // When / Then
        assertThatThrownBy(() -> service.guardar(montoCero))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("monto");

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("guardar() con monto negativo debe lanzar IllegalArgumentException")
    void guardar_montoNegativo_lanzaExcepcion() {
        // Given
        Pago montoNegativo = new Pago(null, 100L, -500.0, "WEBPAY", null);

        // When / Then
        assertThatThrownBy(() -> service.guardar(montoNegativo))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("monto");

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("guardar() con método de pago inválido debe lanzar IllegalArgumentException")
    void guardar_metodoPagoInvalido_lanzaExcepcion() {
        // Given
        Pago metodoInvalido = new Pago(null, 100L, 10000.0, "BITCOIN", null);

        // When / Then
        assertThatThrownBy(() -> service.guardar(metodoInvalido))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Método de pago inválido");

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("guardar() con método de pago nulo debe lanzar IllegalArgumentException")
    void guardar_metodoPagoNulo_lanzaExcepcion() {
        // Given
        Pago sinMetodo = new Pago(null, 100L, 10000.0, null, null);

        // When / Then
        assertThatThrownBy(() -> service.guardar(sinMetodo))
                .isInstanceOf(IllegalArgumentException.class);

        verify(repository, never()).save(any());
    }

    // ── buscar ──────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("buscar() con id existente debe retornar el pago")
    void buscar_idExistente_retornaPago() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(pagoValido));

        // When
        Pago resultado = service.buscar(1L);

        // Then
        assertThat(resultado).isEqualTo(pagoValido);
    }

    @Test
    @DisplayName("buscar() con id inexistente debe retornar null")
    void buscar_idInexistente_retornaNull() {
        // Given
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // When
        Pago resultado = service.buscar(99L);

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

    // ── cambiarEstado ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("cambiarEstado() de PENDIENTE a APROBADO debe ser válido y persistir el cambio")
    void cambiarEstado_pendienteAAprobado_transicionValida() {
        // Given
        Pago pendiente = new Pago(1L, 100L, 5000.0, "WEBPAY", "PENDIENTE");
        Pago aprobado  = new Pago(1L, 100L, 5000.0, "WEBPAY", "APROBADO");
        when(repository.findById(1L)).thenReturn(Optional.of(pendiente));
        when(repository.save(pendiente)).thenReturn(aprobado);

        // When
        Pago resultado = service.cambiarEstado(1L, "APROBADO");

        // Then
        assertThat(resultado.getEstado()).isEqualTo("APROBADO");
        verify(repository).save(pendiente);
    }

    @Test
    @DisplayName("cambiarEstado() de PENDIENTE a RECHAZADO debe ser válido")
    void cambiarEstado_pendienteARechazado_transicionValida() {
        // Given
        Pago pendiente  = new Pago(1L, 100L, 5000.0, "WEBPAY", "PENDIENTE");
        Pago rechazado  = new Pago(1L, 100L, 5000.0, "WEBPAY", "RECHAZADO");
        when(repository.findById(1L)).thenReturn(Optional.of(pendiente));
        when(repository.save(pendiente)).thenReturn(rechazado);

        // When
        Pago resultado = service.cambiarEstado(1L, "RECHAZADO");

        // Then
        assertThat(resultado.getEstado()).isEqualTo("RECHAZADO");
    }

    @Test
    @DisplayName("cambiarEstado() de ANULADO a cualquier estado debe lanzar IllegalArgumentException")
    void cambiarEstado_desdeAnulado_lanzaExcepcion() {
        // Given
        Pago anulado = new Pago(1L, 100L, 5000.0, "WEBPAY", "ANULADO");
        when(repository.findById(1L)).thenReturn(Optional.of(anulado));

        // When / Then
        assertThatThrownBy(() -> service.cambiarEstado(1L, "APROBADO"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("anulado");

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("cambiarEstado() con estado inválido debe lanzar IllegalArgumentException")
    void cambiarEstado_estadoInvalido_lanzaExcepcion() {
        // Given
        Pago pendiente = new Pago(1L, 100L, 5000.0, "WEBPAY", "PENDIENTE");
        when(repository.findById(1L)).thenReturn(Optional.of(pendiente));

        // When / Then
        assertThatThrownBy(() -> service.cambiarEstado(1L, "PROCESANDO"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Estado inválido");

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("cambiarEstado() con id inexistente debe lanzar IllegalStateException")
    void cambiarEstado_idInexistente_lanzaIllegalStateException() {
        // Given
        when(repository.findById(999L)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> service.cambiarEstado(999L, "APROBADO"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("999");

        verify(repository, never()).save(any());
    }

    // ── buscarPorPedido ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("buscarPorPedido() debe delegar en findByPedidoId() con el id de pedido recibido")
    void buscarPorPedido_delegaEnRepositoryConIdDePedido() {
        // Given
        when(repository.findByPedidoId(100L)).thenReturn(List.of(pagoValido));

        // When
        List<Pago> resultado = service.buscarPorPedido(100L);

        // Then
        assertThat(resultado).containsExactly(pagoValido);
        verify(repository).findByPedidoId(100L);
    }
}
