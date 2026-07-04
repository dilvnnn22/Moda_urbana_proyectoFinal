package cl.duoc.productos_service.controller;

import cl.duoc.productos_service.dto.ProductoDTO;
import cl.duoc.productos_service.dto.ProductoDetalleDTO;
import cl.duoc.productos_service.exception.ProductoNotFoundException;
import cl.duoc.productos_service.model.Producto;
import cl.duoc.productos_service.service.ProductoService;
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
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductoController.class)
class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductoService service;

    @Autowired
    private ObjectMapper objectMapper;

    private Producto producto;
    private ProductoDetalleDTO detalle;

    /**
     * @BeforeEach se ejecuta ANTES de cada test, preparando el estado inicial.
     * Aquí creamos un producto y su DTO de detalle reutilizables en todos los métodos.
     */
    @BeforeEach
    void setUp() {
        producto = new Producto(1L, "Polera Oversize", "Polera 100% algodón", 19990.0, 40, 1L, 2L);
        detalle  = new ProductoDetalleDTO(1L, "Polera Oversize", "Polera 100% algodón",
                                          19990.0, 40, "UrbanWear", "Poleras");
    }

    @Test
    void listar_retorna200_conNombreDeMarcaYCategoria() throws Exception {
        when(service.listarConDetalle()).thenReturn(List.of(detalle));

        mockMvc.perform(get("/api/v1/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Polera Oversize"))
                .andExpect(jsonPath("$[0].nombreMarca").value("UrbanWear"))
                .andExpect(jsonPath("$[0].nombreCategoria").value("Poleras"))
                .andExpect(jsonPath("$[0].precio").value(19990.0));
    }

    @Test
    void guardar_retorna200_conProductoCreado() throws Exception {
        when(service.guardar(any(Producto.class))).thenReturn(producto);

        mockMvc.perform(post("/api/v1/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(producto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Polera Oversize"));
    }

    @Test
    void buscar_retorna200_conNombreDeMarcaYCategoria() throws Exception {
        when(service.buscarConDetalle(1L)).thenReturn(detalle);

        mockMvc.perform(get("/api/v1/productos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombreMarca").value("UrbanWear"))
                .andExpect(jsonPath("$.nombreCategoria").value("Poleras"));
    }

    @Test
    void buscar_retorna404_cuandoNoExiste() throws Exception {
        when(service.buscarConDetalle(anyLong())).thenReturn(null);

        mockMvc.perform(get("/api/v1/productos/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void eliminar_retorna200_cuandoSeLlama() throws Exception {
        doNothing().when(service).eliminar(anyLong());

        mockMvc.perform(delete("/api/v1/productos/1"))
                .andExpect(status().isOk());

        verify(service, times(1)).eliminar(1L);
    }

    @Test
    void listadoDTO_retorna200_conDTO() throws Exception {
        when(service.listadoDTO()).thenReturn(List.of(new ProductoDTO("Polera Oversize", 19990.0)));

        mockMvc.perform(get("/api/v1/productos/listado"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].precio").value(19990.0));
    }

    @Test
    void precio_retorna200_conProductosBajoElPrecio() throws Exception {
        when(service.buscarPorPrecio(anyDouble())).thenReturn(List.of(producto));

        mockMvc.perform(get("/api/v1/productos/precio/25000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Polera Oversize"));
    }

    @Test
    void stock_retorna200_conProductosStockBajo() throws Exception {
        when(service.stockBajo(anyInt())).thenReturn(List.of(producto));

        mockMvc.perform(get("/api/v1/productos/stock/50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].stock").value(40));
    }

    @Test
    void marca_retorna200_conProductosDeLaMarca() throws Exception {
        when(service.buscarPorMarca(anyLong())).thenReturn(List.of(producto));

        mockMvc.perform(get("/api/v1/productos/marca/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].marcaId").value(1));
    }

    @Test
    void categoria_retorna200_conProductosDeLaCategoria() throws Exception {
        when(service.buscarPorCategoria(anyLong())).thenReturn(List.of(producto));

        mockMvc.perform(get("/api/v1/productos/categoria/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].categoriaId").value(2));
    }

    @Test
    void detalle_retorna200_conNombreDeMarcaYCategoria() throws Exception {
        when(service.buscarConDetalle(1L)).thenReturn(detalle);

        mockMvc.perform(get("/api/v1/productos/detalle/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreMarca").value("UrbanWear"))
                .andExpect(jsonPath("$.nombreCategoria").value("Poleras"));
    }
}
