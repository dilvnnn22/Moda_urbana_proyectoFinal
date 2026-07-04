package cl.duoc.marcas_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(info = @Info(title = "Marcas Service API", version = "1.0", description = "Gestión de marcas de productos de Moda Urbana"))
@SpringBootApplication
public class MarcasServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MarcasServiceApplication.class, args);
	}

}
