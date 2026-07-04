package cl.duoc.marcas_service.controller;

import cl.duoc.marcas_service.dto.MarcaDTO;
import cl.duoc.marcas_service.model.Marca;
import cl.duoc.marcas_service.service.MarcaService;
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

@WebMvcTest(MarcaController.class)
class MarcaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MarcaService service;

    @Autowired
    private ObjectMapper objectMapper;

    private Marca marca;

    @BeforeEach
    void setUp() {
        marca = new Marca(1L, "UrbanWear", "Chile");
    }

    @Test
    void listar_retorna200_conListaDeMarcas() throws Exception {
        when(service.listar()).thenReturn(List.of(marca));

        mockMvc.perform(get("/api/v1/marcas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("UrbanWear"));
    }

    @Test
    void buscar_retorna200_cuandoExiste() throws Exception {
        when(service.buscar(1L)).thenReturn(marca);

        mockMvc.perform(get("/api/v1/marcas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paisOrigen").value("Chile"));
    }

    @Test
    void guardar_retorna200_conMarcaCreada() throws Exception {
        when(service.guardar(any(Marca.class))).thenReturn(marca);

        mockMvc.perform(post("/api/v1/marcas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(marca)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("UrbanWear"));
    }

    @Test
    void actualizar_retorna200_conMarcaActualizada() throws Exception {
        when(service.guardar(any(Marca.class))).thenReturn(marca);

        mockMvc.perform(put("/marcas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(marca)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("UrbanWear"));
    }

    @Test
    void eliminar_retorna200_cuandoSeLlama() throws Exception {
        doNothing().when(service).eliminar(anyLong());

        mockMvc.perform(delete("/api/v1/marcas/1"))
                .andExpect(status().isOk());

        verify(service, times(1)).eliminar(1L);
    }

    @Test
    void listado_retorna200_conDTO() throws Exception {
        when(service.listadoDTO()).thenReturn(List.of(new MarcaDTO("UrbanWear", "Chile")));

        mockMvc.perform(get("/api/v1/marcas/listado"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("UrbanWear"));
    }
}
