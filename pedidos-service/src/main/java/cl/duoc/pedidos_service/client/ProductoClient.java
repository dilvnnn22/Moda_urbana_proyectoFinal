package cl.duoc.pedidos_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(name = "productos-service")
public interface ProductoClient {

    @GetMapping("/api/v1/productos/{id}")
    Map<String, Object> obtenerProducto(@PathVariable Long id);
}
