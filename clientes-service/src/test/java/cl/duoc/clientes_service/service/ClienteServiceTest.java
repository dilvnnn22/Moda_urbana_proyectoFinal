package cl.duoc.clientes_service.service;

import cl.duoc.clientes_service.dto.ClienteDTO;
import cl.duoc.clientes_service.model.Cliente;
import cl.duoc.clientes_service.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ClienteService - pruebas unitarias")
class ClienteServiceTest {

    @Mock
    private ClienteRepository repository;

    @InjectMocks
    private ClienteService service;

    private Cliente cliente;

    @BeforeEach
    void setUp() {
        cliente = new Cliente(1L, "Camila", "Rojas", "camila@mail.com", "+56912345678");
    }

    @Test
    @DisplayName("listar() debe retornar todos los clientes del repositorio")
    void listar_devuelveTodosLosClientes() {
        Cliente otro = new Cliente(2L, "Matías", "López", "matias@mail.com", "+56987654321");
        when(repository.findAll()).thenReturn(Arrays.asList(cliente, otro));

        List<Cliente> resultado = service.listar();

        assertThat(resultado).hasSize(2).containsExactly(cliente, otro);
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("guardar() debe delegar en repository.save() y retornar el cliente persistido")
    void guardar_persisteYRetornaElCliente() {
        when(repository.save(cliente)).thenReturn(cliente);

        Cliente resultado = service.guardar(cliente);

        assertThat(resultado).isEqualTo(cliente);
        verify(repository).save(cliente);
    }

    @Test
    @DisplayName("buscar() con id existente debe retornar el cliente")
    void buscar_idExistente_retornaCliente() {
        when(repository.findById(1L)).thenReturn(Optional.of(cliente));

        Cliente resultado = service.buscar(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getNombre()).isEqualTo("Camila");
    }

    @Test
    @DisplayName("buscar() con id inexistente debe retornar null")
    void buscar_idInexistente_retornaNull() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        Cliente resultado = service.buscar(99L);

        assertThat(resultado).isNull();
    }

    @Test
    @DisplayName("eliminar() debe invocar deleteById() en el repositorio")
    void eliminar_invocaDeleteByIdEnRepository() {
        doNothing().when(repository).deleteById(1L);

        service.eliminar(1L);

        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("listadoDTO() debe mapear correctamente Cliente a ClienteDTO (nombre completo y correo)")
    void listadoDTO_mapeaCorrectamenteAClienteDTO() {
        when(repository.findAll()).thenReturn(List.of(cliente));

        List<ClienteDTO> resultado = service.listadoDTO();

        assertThat(resultado).hasSize(1);
        ClienteDTO dto = resultado.get(0);
        assertThat(dto.getNombreCompleto()).isEqualTo("Camila Rojas");
        assertThat(dto.getCorreo()).isEqualTo("camila@mail.com");
    }
}
