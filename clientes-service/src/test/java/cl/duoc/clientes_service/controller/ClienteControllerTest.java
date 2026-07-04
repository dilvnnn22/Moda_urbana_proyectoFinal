package cl.duoc.clientes_service.controller;

import cl.duoc.clientes_service.dto.ClienteDTO;
import cl.duoc.clientes_service.model.Cliente;
import cl.duoc.clientes_service.service.ClienteService;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClienteController.class)
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClienteService service;

    @Autowired
    private ObjectMapper objectMapper;

    private Cliente cliente;

    @BeforeEach
    void setUp() {
        cliente = new Cliente(1L, "Camila", "Soto", "camila.soto@correo.cl", "+56912345678");
    }

    @Test
    void listar_retorna200_conListaDeClientes() throws Exception {
        when(service.listar()).thenReturn(List.of(cliente));

        mockMvc.perform(get("/api/v1/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Camila"));
    }

    @Test
    void buscar_retorna200_cuandoExiste() throws Exception {
        when(service.buscar(1L)).thenReturn(cliente);

        mockMvc.perform(get("/api/v1/clientes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correo").value("camila.soto@correo.cl"));
    }

    @Test
    void guardar_retorna200_conClienteCreado() throws Exception {
        when(service.guardar(any(Cliente.class))).thenReturn(cliente);

        mockMvc.perform(post("/api/v1/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cliente)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Camila"));
    }

    @Test
    void actualizar_retorna200_conClienteActualizado() throws Exception {
        when(service.guardar(any(Cliente.class))).thenReturn(cliente);

        mockMvc.perform(put("/clientes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cliente)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.apellido").value("Soto"));
    }

    @Test
    void eliminar_retorna200_cuandoSeLlama() throws Exception {
        doNothing().when(service).eliminar(anyLong());

        mockMvc.perform(delete("/api/v1/clientes/1"))
                .andExpect(status().isOk());

        verify(service, times(1)).eliminar(1L);
    }

    @Test
    void listado_retorna200_conDTO() throws Exception {
        when(service.listadoDTO()).thenReturn(List.of(new ClienteDTO("Camila Soto", "camila.soto@correo.cl")));

        mockMvc.perform(get("/api/v1/clientes/listado"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].correo").value("camila.soto@correo.cl"));
    }
}
