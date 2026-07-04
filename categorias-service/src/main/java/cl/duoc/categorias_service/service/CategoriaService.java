package cl.duoc.categorias_service.service;

import cl.duoc.categorias_service.dto.CategoriaDTO;
import cl.duoc.categorias_service.exception.CategoriaNotFoundException;
import cl.duoc.categorias_service.model.Categoria;
import cl.duoc.categorias_service.repository.CategoriaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoriaService {

    private final CategoriaRepository repository;

    public CategoriaService(CategoriaRepository repository) {
        this.repository = repository;
    }

    public List<Categoria> listar() {
        return repository.findAll();
    }

    public Categoria guardar(Categoria categoria) {
        return repository.save(categoria);
    }

    public Categoria buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new CategoriaNotFoundException(id));
    }

    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new CategoriaNotFoundException(id);
        }
        repository.deleteById(id);
    }

    public List<CategoriaDTO> listadoDTO() {
        return repository.findAll()
                .stream()
                .map(c -> new CategoriaDTO(
                        c.getNombre()
                ))
                .collect(Collectors.toList());
    }
}
