package cl.duoc.inventario_service.service;

import cl.duoc.inventario_service.dto.InventarioDTO;
import cl.duoc.inventario_service.model.Inventario;
import cl.duoc.inventario_service.repository.InventarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InventarioService {

    private final InventarioRepository repository;

    public InventarioService(InventarioRepository repository) {
        this.repository = repository;
    }

    public List<Inventario> listar() {
        return repository.findAll();
    }

    public Inventario guardar(Inventario inventario) {
        return repository.save(inventario);
    }

    public Inventario buscar(Long id) {
        return repository.findById(id).orElse(null);
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }

    public List<InventarioDTO> listadoDTO() {
        return repository.findAll()
                .stream()
                .map(i -> new InventarioDTO(
                        i.getProductoId(),
                        i.getCantidadDisponible()
                ))
                .collect(Collectors.toList());
    }
}
