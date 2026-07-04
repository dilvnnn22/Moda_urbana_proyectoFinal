package cl.duoc.inventario_service.service;

import cl.duoc.inventario_service.dto.InventarioDTO;
import cl.duoc.inventario_service.model.Inventario;
import cl.duoc.inventario_service.repository.InventarioRepository;
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
 * Pruebas unitarias de InventarioService.
 * Se mockea InventarioRepository para aislar la lógica de negocio.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("InventarioService - pruebas unitarias")
class InventarioServiceTest {

    @Mock
    private InventarioRepository repository;

    @InjectMocks
    private InventarioService service;

    private Inventario inventario;

    @BeforeEach
    void setUp() {
        inventario = new Inventario(1L, 5L, 40, "Bodega Central");
    }

    @Test
    @DisplayName("listar() debe retornar todos los registros de inventario")
    void listar_devuelveTodoElInventario() {
        // Given
        when(repository.findAll()).thenReturn(List.of(inventario));

        // When
        List<Inventario> resultado = service.listar();

        // Then
        assertThat(resultado).containsExactly(inventario);
        verify(repository).findAll();
    }

    @Test
    @DisplayName("guardar() debe delegar en repository.save() y retornar el registro persistido")
    void guardar_persisteYRetornaElRegistro() {
        // Given
        when(repository.save(inventario)).thenReturn(inventario);

        // When
        Inventario resultado = service.guardar(inventario);

        // Then
        assertThat(resultado).isEqualTo(inventario);
        verify(repository).save(inventario);
    }

    @Test
    @DisplayName("buscar() con id existente debe retornar el registro de inventario")
    void buscar_idExistente_retornaRegistro() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(inventario));

        // When
        Inventario resultado = service.buscar(1L);

        // Then
        assertThat(resultado).isEqualTo(inventario);
    }

    @Test
    @DisplayName("buscar() con id inexistente debe retornar null (comportamiento actual del servicio)")
    void buscar_idInexistente_retornaNull() {
        // Given
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // When
        Inventario resultado = service.buscar(99L);

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

    @Test
    @DisplayName("listadoDTO() debe mapear productoId y cantidadDisponible correctamente")
    void listadoDTO_mapeaCorrectamenteAInventarioDTO() {
        // Given
        when(repository.findAll()).thenReturn(List.of(inventario));

        // When
        List<InventarioDTO> resultado = service.listadoDTO();

        // Then
        assertThat(resultado).hasSize(1);
        InventarioDTO dto = resultado.get(0);
        assertThat(dto.getProductoId()).isEqualTo(5L);
        assertThat(dto.getCantidadDisponible()).isEqualTo(40);
    }
}
