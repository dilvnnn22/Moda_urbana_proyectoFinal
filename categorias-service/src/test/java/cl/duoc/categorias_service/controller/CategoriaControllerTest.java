package cl.duoc.categorias_service.controller;

import cl.duoc.categorias_service.dto.CategoriaDTO;
import cl.duoc.categorias_service.model.Categoria;
import cl.duoc.categorias_service.service.CategoriaService;
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

@WebMvcTest(CategoriaController.class)
class CategoriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoriaService service;

    @Autowired
    private ObjectMapper objectMapper;

    private Categoria categoria;

    @BeforeEach
    void setUp() {
        categoria = new Categoria(1L, "Poleras", "Poleras y polerones urbanos");
    }

    @Test
    void listar_retorna200_conListaDeCategorias() throws Exception {
        when(service.listar()).thenReturn(List.of(categoria));

        mockMvc.perform(get("/api/v1/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Poleras"));
    }

    @Test
    void buscar_retorna200_cuandoExiste() throws Exception {
        when(service.buscar(1L)).thenReturn(categoria);

        mockMvc.perform(get("/api/v1/categorias/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void guardar_retorna200_conCategoriaCreada() throws Exception {
        when(service.guardar(any(Categoria.class))).thenReturn(categoria);

        mockMvc.perform(post("/api/v1/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoria)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Poleras"));
    }

    @Test
    void actualizar_retorna200_conCategoriaActualizada() throws Exception {
        when(service.guardar(any(Categoria.class))).thenReturn(categoria);

        mockMvc.perform(put("/categorias/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoria)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Poleras"));
    }

    @Test
    void eliminar_retorna200_cuandoSeLlama() throws Exception {
        doNothing().when(service).eliminar(anyLong());

        mockMvc.perform(delete("/api/v1/categorias/1"))
                .andExpect(status().isOk());

        verify(service, times(1)).eliminar(1L);
    }

    @Test
    void listado_retorna200_conDTO() throws Exception {
        when(service.listadoDTO()).thenReturn(List.of(new CategoriaDTO("Poleras")));

        mockMvc.perform(get("/api/v1/categorias/listado"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Poleras"));
    }
}
