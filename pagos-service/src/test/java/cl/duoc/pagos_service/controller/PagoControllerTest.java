package cl.duoc.pagos_service.controller;

import cl.duoc.pagos_service.model.Pago;
import cl.duoc.pagos_service.service.PagoService;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PagoController.class)
class PagoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PagoService service;

    @Autowired
    private ObjectMapper objectMapper;

    private Pago pago;

    @BeforeEach
    void setUp() {
        pago = new Pago(1L, 100L, 19990.0, "WEBPAY", "PENDIENTE");
    }

    @Test
    void listar_retorna200_conListaDePagos() throws Exception {
        when(service.listar()).thenReturn(List.of(pago));

        mockMvc.perform(get("/api/v1/pagos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].metodoPago").value("WEBPAY"));
    }

    @Test
    void guardar_retorna200_conPagoCreado() throws Exception {
        when(service.guardar(any(Pago.class))).thenReturn(pago);

        mockMvc.perform(post("/api/v1/pagos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pago)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.monto").value(19990.0));
    }

    @Test
    void guardar_retorna400_cuandoMontoInvalido() throws Exception {
        when(service.guardar(any(Pago.class))).thenThrow(new IllegalArgumentException("Monto inválido"));

        mockMvc.perform(post("/api/v1/pagos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pago)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void buscar_retorna200_cuandoExiste() throws Exception {
        when(service.buscar(1L)).thenReturn(pago);

        mockMvc.perform(get("/api/v1/pagos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));
    }

    @Test
    void buscar_retorna404_cuandoNoExiste() throws Exception {
        when(service.buscar(anyLong())).thenReturn(null);

        mockMvc.perform(get("/api/v1/pagos/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void eliminar_retorna200_cuandoSeLlama() throws Exception {
        doNothing().when(service).eliminar(anyLong());

        mockMvc.perform(delete("/api/v1/pagos/1"))
                .andExpect(status().isOk());

        verify(service, times(1)).eliminar(1L);
    }

    @Test
    void cambiarEstado_retorna200_conEstadoActualizado() throws Exception {
        pago.setEstado("APROBADO");
        when(service.cambiarEstado(1L, "APROBADO")).thenReturn(pago);

        mockMvc.perform(patch("/api/v1/pagos/1/estado")
                        .param("estado", "APROBADO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("APROBADO"));
    }

    @Test
    void cambiarEstado_retorna400_cuandoTransicionInvalida() throws Exception {
        when(service.cambiarEstado(anyLong(), anyString()))
                .thenThrow(new IllegalArgumentException("Transición no permitida"));

        mockMvc.perform(patch("/api/v1/pagos/1/estado")
                        .param("estado", "INVALIDO"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void buscarPorPedido_retorna200_conPagosDePedido() throws Exception {
        when(service.buscarPorPedido(100L)).thenReturn(List.of(pago));

        mockMvc.perform(get("/api/v1/pagos/pedido/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].pedidoId").value(100));
    }
}
