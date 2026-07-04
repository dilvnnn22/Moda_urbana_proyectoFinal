# Moda Urbana - Arquitectura de Microservicios

Proyecto de e-commerce de moda con arquitectura de microservicios Spring Boot, dockerizado y con Swagger/OpenAPI centralizado en el API Gateway.

## Microservicios

| Servicio | Puerto local | Ruta en Gateway |
|---|---|---|
| eureka-service | 8761 | — |
| config-server | 8888 | — |
| api-gateway | 9090 | http://localhost:9090 |
| productos-service | dinámico (port: 0) | /api/v1/productos/** |
| clientes-service | dinámico (port: 0) | /api/v1/clientes/** |
| categorias-service | dinámico (port: 0) | /api/v1/categorias/** |
| marcas-service | dinámico (port: 0) | /api/v1/marcas/** |
| pedidos-service | dinámico (port: 0) | /api/v1/pedidos/** |
| carrito-service | dinámico (port: 0) | /api/v1/carrito/** |
| pagos-service | dinámico (port: 0) | /api/v1/pagos/** |
| inventario-service | dinámico (port: 0) | /api/v1/inventario/** |

## Levantar con Docker

```bash
docker compose up --build
```

Una vez levantado, acceder a:
- **Swagger UI centralizado**: http://localhost:9090/swagger-ui.html
- **Eureka dashboard**: http://localhost:8761

## Ejecutar tests unitarios

```bash
cd productos-service
./mvnw test

cd pedidos-service
./mvnw test
```

Los tests de la capa **Controller** usan `@WebMvcTest` + `MockMvc` y los de la capa **Service** usan `@ExtendWith(MockitoExtension.class)` con `@Mock` / `@InjectMocks`.

Para los tests, cada servicio tiene `src/test/resources/application.yml` que deshabilita el Config Server y Eureka, permitiendo que los tests corran sin infraestructura levantada.

## Stack

- Java 25 + Spring Boot 4.0.6
- Spring Cloud 2025.1.1 (Eureka, Config, Gateway WebFlux, OpenFeign)
- MySQL 8 + Flyway (migraciones en `src/main/resources/db/migration/`)
- Springdoc OpenAPI 3.0.2
- Lombok + Mockito + JUnit 5

## Configuración centralizada

Toda la configuración de datasource, JPA, Flyway, Eureka y Springdoc vive en `config-microservicios/` y es servida por el `config-server`. Los `application.yml` locales de cada microservicio solo contienen el nombre del servicio y la dirección del config-server.
