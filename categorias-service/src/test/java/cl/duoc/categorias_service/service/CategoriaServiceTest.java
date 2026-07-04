package cl.duoc.categorias_service.service;

import cl.duoc.categorias_service.dto.CategoriaDTO;
import cl.duoc.categorias_service.model.Categoria;
import cl.duoc.categorias_service.repository.CategoriaRepository;
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
 * Pruebas unitarias de CategoriaService.
 * Se mockea CategoriaRepository para aislar la lógica de negocio.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CategoriaService - pruebas unitarias")
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository repository;

    @InjectMocks
    private CategoriaService service;

    private Categoria categoria;

    @BeforeEach
    void setUp() {
        categoria = new Categoria(1L, "Poleras", "Poleras y polerones urbanos");
    }

    @Test
    @DisplayName("listar() debe retornar todas las categorías del repositorio")
    void listar_devuelveTodasLasCategorias() {
        // Given
        when(repository.findAll()).thenReturn(List.of(categoria));

        // When
        List<Categoria> resultado = service.listar();

        // Then
        assertThat(resultado).containsExactly(categoria);
        verify(repository).findAll();
    }

    @Test
    @DisplayName("guardar() debe delegar en repository.save() y retornar la categoría persistida")
    void guardar_persisteYRetornaLaCategoria() {
        // Given
        when(repository.save(categoria)).thenReturn(categoria);

        // When
        Categoria resultado = service.guardar(categoria);

        // Then
        assertThat(resultado).isEqualTo(categoria);
        verify(repository).save(categoria);
    }

    @Test
    @DisplayName("buscar() con id existente debe retornar la categoría")
    void buscar_idExistente_retornaCategoria() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(categoria));

        // When
        Categoria resultado = service.buscar(1L);

        // Then
        assertThat(resultado).isEqualTo(categoria);
    }

    @Test
    @DisplayName("buscar() con id inexistente debe retornar null (comportamiento actual del servicio)")
    void buscar_idInexistente_retornaNull() {
        // Given
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // When
        Categoria resultado = service.buscar(99L);

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
    @DisplayName("listadoDTO() debe mapear correctamente Categoria a CategoriaDTO")
    void listadoDTO_mapeaCorrectamenteACategoriaDTO() {
        // Given
        when(repository.findAll()).thenReturn(List.of(categoria));

        // When
        List<CategoriaDTO> resultado = service.listadoDTO();

        // Then
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNombreCategoria()).isEqualTo("Poleras");
    }
}
