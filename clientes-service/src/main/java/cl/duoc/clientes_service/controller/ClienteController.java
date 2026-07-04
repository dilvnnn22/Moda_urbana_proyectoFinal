package cl.duoc.clientes_service.controller;

import cl.duoc.clientes_service.dto.ClienteDTO;
import cl.duoc.clientes_service.model.Cliente;
import cl.duoc.clientes_service.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/clientes")
@Tag(name = "Clientes", description = "Gestión de clientes de Moda Urbana")
public class ClienteController {

    private final ClienteService service;

    public ClienteController(ClienteService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar clientes")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente") })
    public List<Cliente> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar cliente por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado", content = @Content)
    })
    public Cliente buscar(@Parameter(description = "Id del cliente") @PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping
    @Operation(summary = "Crear cliente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente creado",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(
                            value = "{ \"id\": 1, \"nombre\": \"Camila\", \"apellido\": \"Soto\", "
                                  + "\"correo\": \"camila.soto@correo.cl\", \"telefono\": \"+56912345678\" }"
                    ))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
    })
    public Cliente guardar(@RequestBody Cliente cliente) {
        return service.guardar(cliente);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar cliente existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente actualizado"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado", content = @Content)
    })
    public Cliente actualizar(@Parameter(description = "Id del cliente") @PathVariable Long id,
                              @RequestBody Cliente cliente) {
        cliente.setId(id);
        return service.guardar(cliente);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar cliente")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Cliente eliminado") })
    public void eliminar(@Parameter(description = "Id del cliente") @PathVariable Long id) {
        service.eliminar(id);
    }

    @GetMapping("/listado")
    @Operation(summary = "Listado resumido de clientes (DTO)",
            description = "Retorna nombre completo y correo de cada cliente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ClienteDTO.class)))
    })
    public List<ClienteDTO> listado() {
        return service.listadoDTO();
    }
}
