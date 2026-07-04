package cl.duoc.carrito_service.service;

import cl.duoc.carrito_service.model.Carrito;
import cl.duoc.carrito_service.repository.CarritoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class CarritoService {

    private final CarritoRepository repository;

    public CarritoService(CarritoRepository repository) {
        this.repository = repository;
    }

    public List<Carrito> listar() {
        return repository.findAll();
    }

    public Carrito guardar(Carrito carrito) {
        return repository.save(carrito);
    }

    public Carrito buscar(Long id) {
        return repository.findById(id).orElse(null);
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}
