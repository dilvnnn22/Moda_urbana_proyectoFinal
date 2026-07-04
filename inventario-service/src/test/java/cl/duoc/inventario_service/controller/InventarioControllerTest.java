package cl.duoc.inventario_service.controller;

import cl.duoc.inventario_service.dto.InventarioDTO;
import cl.duoc.inventario_service.model.Inventario;
import cl.duoc.inventario_service.service.InventarioService;
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

@WebMvcTest(InventarioController.class)
class InventarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InventarioService service;

    @Autowired
    private ObjectMapper objectMapper;

    private Inventario inventario;

    @BeforeEach
    void setUp() {
        inventario = new Inventario(1L, 5L, 40, "Bodega Central");
    }

    @Test
    void listar_retorna200_conListaDeInventario() throws Exception {
        when(service.listar()).thenReturn(List.of(inventario));

        mockMvc.perform(get("/api/v1/inventario"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ubicacionBodega").value("Bodega Central"));
    }

    @Test
    void buscar_retorna200_cuandoExiste() throws Exception {
        when(service.buscar(1L)).thenReturn(inventario);

        mockMvc.perform(get("/api/v1/inventario/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cantidadDisponible").value(40));
    }

    @Test
    void guardar_retorna200_conRegistroCreado() throws Exception {
        when(service.guardar(any(Inventario.class))).thenReturn(inventario);

        mockMvc.perform(post("/api/v1/inventario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inventario)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productoId").value(5));
    }

    @Test
    void actualizar_retorna200_conRegistroActualizado() throws Exception {
        when(service.guardar(any(Inventario.class))).thenReturn(inventario);

        mockMvc.perform(put("/inventario/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inventario)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ubicacionBodega").value("Bodega Central"));
    }

    @Test
    void eliminar_retorna200_cuandoSeLlama() throws Exception {
        doNothing().when(service).eliminar(anyLong());

        mockMvc.perform(delete("/api/v1/inventario/1"))
                .andExpect(status().isOk());

        verify(service, times(1)).eliminar(1L);
    }

    @Test
    void listado_retorna200_conDTO() throws Exception {
        when(service.listadoDTO()).thenReturn(List.of(new InventarioDTO(5L, 40)));

        mockMvc.perform(get("/api/v1/inventario/listado"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].cantidadDisponible").value(40));
    }
}
