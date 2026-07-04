package cl.duoc.marcas_service.service;

import cl.duoc.marcas_service.dto.MarcaDTO;
import cl.duoc.marcas_service.model.Marca;
import cl.duoc.marcas_service.repository.MarcaRepository;
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
 * Pruebas unitarias de MarcaService.
 * Se mockea MarcaRepository para aislar la lógica de negocio.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("MarcaService - pruebas unitarias")
class MarcaServiceTest {

    @Mock
    private MarcaRepository repository;

    @InjectMocks
    private MarcaService service;

    private Marca marca;

    @BeforeEach
    void setUp() {
        marca = new Marca(1L, "UrbanWear", "Chile");
    }

    @Test
    @DisplayName("listar() debe retornar todas las marcas del repositorio")
    void listar_devuelveTodasLasMarcas() {
        // Given
        when(repository.findAll()).thenReturn(List.of(marca));

        // When
        List<Marca> resultado = service.listar();

        // Then
        assertThat(resultado).containsExactly(marca);
        verify(repository).findAll();
    }

    @Test
    @DisplayName("guardar() debe delegar en repository.save() y retornar la marca persistida")
    void guardar_persisteYRetornaLaMarca() {
        // Given
        when(repository.save(marca)).thenReturn(marca);

        // When
        Marca resultado = service.guardar(marca);

        // Then
        assertThat(resultado).isEqualTo(marca);
        verify(repository).save(marca);
    }

    @Test
    @DisplayName("buscar() con id existente debe retornar la marca")
    void buscar_idExistente_retornaMarca() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(marca));

        // When
        Marca resultado = service.buscar(1L);

        // Then
        assertThat(resultado).isEqualTo(marca);
    }

    @Test
    @DisplayName("buscar() con id inexistente debe retornar null (comportamiento actual del servicio)")
    void buscar_idInexistente_retornaNull() {
        // Given
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // When
        Marca resultado = service.buscar(99L);

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
    @DisplayName("listadoDTO() debe mapear correctamente nombre y país de origen")
    void listadoDTO_mapeaCorrectamenteAMarcaDTO() {
        // Given
        when(repository.findAll()).thenReturn(List.of(marca));

        // When
        List<MarcaDTO> resultado = service.listadoDTO();

        // Then
        assertThat(resultado).hasSize(1);
        MarcaDTO dto = resultado.get(0);
        assertThat(dto.getNombreMarca()).isEqualTo("UrbanWear");
        assertThat(dto.getPaisOrigen()).isEqualTo("Chile");
    }
}
