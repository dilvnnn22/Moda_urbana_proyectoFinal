# Cambios aplicados: Config-Server + API Gateway + Swagger

Este zip es tu repo `moda_urbana_stack` completo, con los tests Mockito de la
pasada anterior **más** lo siguiente. Puedes reemplazar tu carpeta local
completa por esta (o hacer merge archivo por archivo si prefieres revisar
cada cambio antes de aceptarlo).

## 🐛 Bug crítico encontrado y corregido: el Gateway no enrutaba nada
Tu `api-gateway` usa el starter **WebMVC** (`spring-cloud-starter-gateway-server-webmvc`),
pero la propiedad de auto-ruteo estaba escrita como
`spring.cloud.gateway.server.webflux.discovery.locator.enabled=true` — prefijo
**webflux**, que no aplica a tu starter (es webmvc). Esa propiedad no hacía
nada: probablemente tu Gateway no estaba enrutando ninguna petición. Lo
reemplacé por **rutas explícitas** con predicados y filtros (lo que pide la
rúbrica IE 3.3.2 textualmente), bajo el prefijo correcto
`spring.cloud.gateway.server.webmvc.routes`.

## 1. Config Server → modo nativo
Tu `config-server` apuntaba a `https://github.com/dilvnnn22/config-repo`, que
no encontré público/poblado. Lo cambié a **modo nativo**, leyendo desde una
carpeta local `config-microservicios/` (nueva, en la raíz del repo, al mismo
nivel que cada carpeta de servicio). Si prefieres seguir usando un repo Git
separado, dímelo y lo dejamos así, pero modo nativo es más simple de
mantener y de mostrar en la defensa (no depende de un repo externo).

## 2. config-microservicios/ (nueva carpeta, en la raíz)
- `application.yml`: config común (Eureka, Swagger habilitado).
- `productos-service.yml`, `clientes-service.yml`, etc. (8 archivos): cada uno
  con su propio datasource (`db_productos`, `db_clientes`...), JPA y Flyway.
  Antes esto vivía hardcodeado en cada `application.properties` local; ahora
  está **centralizado y override-able por variable de entorno**
  (`SPRING_DATASOURCE_URL`, etc.) — esto es justo lo que pide la rúbrica:
  "separación adecuada de configuraciones sensibles o dependientes del
  entorno".
- `api-gateway.yml`: las 16 rutas (8 servicios × ruta de API + ruta de
  documentación Swagger) con predicados (`Path=`) y filtros
  (`AddRequestHeader`, `RewritePath`) explícitos, más la agregación de
  Swagger UI.

## 3. Cada servicio: spring-cloud-starter-config agregado donde faltaba
Solo `pagos-service` y `carrito-service` tenían la dependencia
`spring-cloud-starter-config`. Sin ella, `spring.config.import=configserver:...`
de los otros 6 servicios (productos, clientes, categorias, marcas, pedidos,
inventario) probablemente no hacía nada. Ya está agregada en los 8 + en
`api-gateway`.

## 4. .properties → .yml en los 11 servicios
La rúbrica pide explícitamente "archivos YAML" varias veces (IE 3.3.4 y su
par en la defensa, IE 3.3.5). Todo el proyecto usaba `.properties`. Convertí
los 11 a `.yml`, simplificando cada uno a lo mínimo local
(`spring.application.name`, `spring.config.import`, `server.port`) — el resto
(datasource, JPA, Flyway, Eureka) ahora se sirve centralizado desde
`config-microservicios/`.

## 5. Swagger/OpenAPI agregado a los 8 servicios + Gateway
- Dependencia `springdoc-openapi-starter-webmvc-ui:3.0.3` en los 8 pom.xml +
  en `api-gateway`.
- `@Tag`, `@Operation`, `@ApiResponses` (incluyendo 404/400/500 donde aplica)
  en **todos** los endpoints de los 8 controllers.
- Ejemplos JSON (`@ExampleObject`) en los endpoints de creación (`POST`) y en
  el de detalle de `pedidos-service`.
- `@OpenAPIDefinition` con título y descripción en la clase principal de cada
  servicio, para que el Swagger UI no diga "OpenAPI definition" genérico.
- El Gateway agrega los 8 Swagger UI en uno solo:
  `http://localhost:8090/swagger-ui.html`.

## Cómo probar
1. Local (sin Docker): levanta `eureka-service` → `config-server` →
   el resto, cada uno desde su propio IDE/`mvn spring-boot:run`. El
   `config-server` busca `config-microservicios/` con una ruta relativa
   (`file:../config-microservicios`), así que debe ejecutarse con el working
   directory dentro de la carpeta `config-server/` (el default de Maven/IDE).
2. Entra a `http://localhost:8090/swagger-ui.html` — deberías ver las 8 APIs
   listadas en el dropdown superior derecho.
3. Prueba una ruta real a través del gateway, ej.
   `http://localhost:8090/productos` — debería responder igual que
   `http://localhost:8081/productos` directo.

## Pendiente / siguiente paso
Docker + Railway/Render (lo dejamos para la siguiente pasada, como
acordamos). El `docker-compose.yml` que armamos antes va a necesitar un
ajuste menor para montar `config-microservicios/` como volumen — lo hago
cuando lleguemos a esa parte.
