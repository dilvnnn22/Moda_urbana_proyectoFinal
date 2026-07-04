package cl.duoc.productos_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * Feign Client para consumir categorias-service.
 * Permite obtener el nombre de una categoría a partir de su id.
 */
@FeignClient(name = "categorias-service")
public interface CategoriaClient {

    @GetMapping("/api/v1/categorias/{id}")
    Map<String, Object> obtenerCategoria(@PathVariable Long id);
}
