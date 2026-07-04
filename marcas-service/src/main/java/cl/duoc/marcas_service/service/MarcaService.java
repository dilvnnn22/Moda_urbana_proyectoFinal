package cl.duoc.marcas_service.service;

import cl.duoc.marcas_service.dto.MarcaDTO;
import cl.duoc.marcas_service.exception.MarcaNotFoundException;
import cl.duoc.marcas_service.model.Marca;
import cl.duoc.marcas_service.repository.MarcaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MarcaService {

    private final MarcaRepository repository;

    public MarcaService(MarcaRepository repository) {
        this.repository = repository;
    }

    public List<Marca> listar() {
        return repository.findAll();
    }

    public Marca guardar(Marca marca) {
        return repository.save(marca);
    }

    public Marca buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new MarcaNotFoundException(id));
    }

    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new MarcaNotFoundException(id);
        }
        repository.deleteById(id);
    }

    public List<MarcaDTO> listadoDTO() {
        return repository.findAll()
                .stream()
                .map(m -> new MarcaDTO(
                        m.getNombre(),
                        m.getPaisOrigen()
                ))
                .collect(Collectors.toList());
    }
}