package cl.duoc.pedidos_service.controller;

import cl.duoc.pedidos_service.model.Pedido;
import cl.duoc.pedidos_service.service.PedidoService;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PedidoController.class)
class PedidoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PedidoService service;

    @Autowired
    private ObjectMapper objectMapper;

    private Pedido pedido;

    @BeforeEach
    void setUp() {
        pedido = new Pedido(1L, 10L, 20L, 2, LocalDate.of(2026, 6, 1), "PENDIENTE");
    }

    @Test
    void listar_retorna200_conListaDePedidos() throws Exception {
        when(service.listar()).thenReturn(List.of(pedido));

        mockMvc.perform(get("/api/v1/pedidos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].estado").value("PENDIENTE"));
    }

    @Test
    void guardar_retorna200_conPedidoCreado() throws Exception {
        when(service.guardar(any(Pedido.class))).thenReturn(pedido);

        mockMvc.perform(post("/api/v1/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pedido)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clienteId").value(10));
    }

    @Test
    void buscar_retorna200_cuandoExiste() throws Exception {
        when(service.buscar(1L)).thenReturn(pedido);

        mockMvc.perform(get("/api/v1/pedidos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void eliminar_retorna200_cuandoSeLlama() throws Exception {
        doNothing().when(service).eliminar(anyLong());

        mockMvc.perform(delete("/api/v1/pedidos/1"))
                .andExpect(status().isOk());

        verify(service, times(1)).eliminar(1L);
    }

    @Test
    void detalle_retorna200_conDetalleCombinado() throws Exception {
        Map<String, Object> detalle = Map.of(
                "pedido", pedido,
                "cliente", Map.of("nombre", "Camila"),
                "producto", Map.of("nombre", "Polera Oversize")
        );
        when(service.obtenerDetalle(1L)).thenReturn(detalle);

        mockMvc.perform(get("/api/v1/pedidos/detalle/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cliente.nombre").value("Camila"))
                .andExpect(jsonPath("$.producto.nombre").value("Polera Oversize"));
    }
}
