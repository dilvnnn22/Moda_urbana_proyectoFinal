package cl.duoc.carrito_service.controller;

import cl.duoc.carrito_service.model.Carrito;
import cl.duoc.carrito_service.service.CarritoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CarritoController.class)
class CarritoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CarritoService service;

    @Autowired
    private ObjectMapper objectMapper;

    private Carrito carrito;

    @BeforeEach
    void setUp() {
        carrito = new Carrito(1L, 10L, 5L, 2);
    }

    @Test
    void listar_retorna200_conListaDeCarrito() throws Exception {
        when(service.listar()).thenReturn(List.of(carrito));

        mockMvc.perform(get("/api/v1/carrito"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].clienteId").value(10));
    }

    @Test
    void guardar_retorna200_conItemAgregado() throws Exception {
        when(service.guardar(any(Carrito.class))).thenReturn(carrito);

        mockMvc.perform(post("/api/v1/carrito")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carrito)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cantidad").value(2));
    }

    @Test
    void buscar_retorna200_cuandoExiste() throws Exception {
        when(service.buscar(1L)).thenReturn(carrito);

        mockMvc.perform(get("/api/v1/carrito/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productoId").value(5));
    }

    @Test
    void eliminar_retorna200_cuandoSeLlama() throws Exception {
        doNothing().when(service).eliminar(anyLong());

        mockMvc.perform(delete("/api/v1/carrito/1"))
                .andExpect(status().isOk());

        verify(service, times(1)).eliminar(1L);
    }
}