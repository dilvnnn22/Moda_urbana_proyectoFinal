package cl.duoc.productos_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * Feign Client para consumir marcas-service.
 * Permite obtener el nombre de una marca a partir de su id.
 */
@FeignClient(name = "marcas-service")
public interface MarcaClient {

    @GetMapping("/api/v1/marcas/{id}")
    Map<String, Object> obtenerMarca(@PathVariable Long id);
}
