package cl.duoc.categorias_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(info = @Info(title = "Categorías Service API", version = "1.0", description = "Gestión de categorías de productos de Moda Urbana"))
@SpringBootApplication
public class CategoriasServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CategoriasServiceApplication.class, args);
	}

}
