package cl.duoc.carrito_service.service;

import cl.duoc.carrito_service.model.Carrito;
import cl.duoc.carrito_service.repository.CarritoRepository;
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
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias de CarritoService.
 * Se mockea CarritoRepository para aislar la lógica de negocio.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CarritoService - pruebas unitarias")
class CarritoServiceTest {

    @Mock
    private CarritoRepository repository;

    @InjectMocks
    private CarritoService service;

    private Carrito carrito;

    @BeforeEach
    void setUp() {
        carrito = new Carrito(1L, 10L, 5L, 2);
    }

    @Test
    @DisplayName("listar() debe retornar todos los ítems de carrito del repositorio")
    void listar_devuelveTodosLosItems() {
        // Given
        when(repository.findAll()).thenReturn(List.of(carrito));

        // When
        List<Carrito> resultado = service.listar();

        // Then
        assertThat(resultado).containsExactly(carrito);
        verify(repository).findAll();
    }

    @Test
    @DisplayName("guardar() debe delegar en repository.save() y retornar el ítem persistido")
    void guardar_persisteYRetornaElItem() {
        // Given
        when(repository.save(carrito)).thenReturn(carrito);

        // When
        Carrito resultado = service.guardar(carrito);

        // Then
        assertThat(resultado).isEqualTo(carrito);
        assertThat(resultado.getCantidad()).isEqualTo(2);
        verify(repository).save(carrito);
    }

    @Test
    @DisplayName("buscar() con id existente debe retornar el ítem de carrito")
    void buscar_idExistente_retornaItem() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(carrito));

        // When
        Carrito resultado = service.buscar(1L);

        // Then
        assertThat(resultado).isEqualTo(carrito);
    }

    @Test
    @DisplayName("buscar() con id inexistente debe retornar null (comportamiento actual del servicio)")
    void buscar_idInexistente_retornaNull() {
        // Given
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // When
        Carrito resultado = service.buscar(99L);

        // Then
        assertThat(resultado).isNull();
    }

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
}
