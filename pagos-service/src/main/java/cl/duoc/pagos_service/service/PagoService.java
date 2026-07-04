package cl.duoc.pagos_service.service;

import cl.duoc.pagos_service.model.Pago;
import cl.duoc.pagos_service.repository.PagoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PagoService {

    private static final double MONTO_MINIMO = 1.0;
    private static final List<String> METODOS_VALIDOS =
            List.of("WEBPAY", "TRANSFERENCIA", "EFECTIVO", "TARJETA_CREDITO");
    private static final List<String> ESTADOS_VALIDOS =
            List.of("PENDIENTE", "APROBADO", "RECHAZADO", "ANULADO");

    private final PagoRepository repository;

    public PagoService(PagoRepository repository) {
        this.repository = repository;
    }

    public List<Pago> listar() {
        return repository.findAll();
    }

    /**
     * Registra un pago aplicando las siguientes reglas de negocio:
     * 1. El monto debe ser mayor a cero.
     * 2. El método de pago debe ser uno de los aceptados por el sistema.
     * 3. Si no se especifica estado, se asigna "PENDIENTE" por defecto.
     *
     * @throws IllegalArgumentException si el monto o el método de pago son inválidos.
     */
    public Pago guardar(Pago pago) {
        validarMonto(pago.getMonto());
        validarMetodoPago(pago.getMetodoPago());

        if (pago.getEstado() == null || pago.getEstado().isBlank()) {
            pago.setEstado("PENDIENTE");
        }

        return repository.save(pago);
    }

    public Pago buscar(Long id) {
        return repository.findById(id).orElse(null);
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }

    /**
     * Cambia el estado de un pago existente.
     * Solo se permite pasar a "APROBADO" o "RECHAZADO" desde "PENDIENTE".
     * Un pago "ANULADO" no puede cambiar de estado.
     *
     * @throws IllegalArgumentException si la transición de estado no está permitida.
     * @throws IllegalStateException    si el pago no existe.
     */
    public Pago cambiarEstado(Long id, String nuevoEstado) {
        Pago pago = repository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Pago no encontrado con id: " + id));

        if (!ESTADOS_VALIDOS.contains(nuevoEstado)) {
            throw new IllegalArgumentException(
                    "Estado inválido: " + nuevoEstado + ". Valores permitidos: " + ESTADOS_VALIDOS);
        }

        if ("ANULADO".equals(pago.getEstado())) {
            throw new IllegalArgumentException("No se puede cambiar el estado de un pago anulado.");
        }

        if ("PENDIENTE".equals(pago.getEstado()) &&
                !List.of("APROBADO", "RECHAZADO").contains(nuevoEstado)) {
            throw new IllegalArgumentException(
                    "Desde PENDIENTE solo se puede pasar a APROBADO o RECHAZADO.");
        }

        pago.setEstado(nuevoEstado);
        return repository.save(pago);
    }

    /**
     * Retorna todos los pagos de un pedido específico.
     */
    public List<Pago> buscarPorPedido(Long pedidoId) {
        return repository.findByPedidoId(pedidoId);
    }

    // ── validaciones privadas ──────────────────────────────────────────────────

    private void validarMonto(Double monto) {
        if (monto == null || monto < MONTO_MINIMO) {
            throw new IllegalArgumentException(
                    "El monto del pago debe ser mayor a " + MONTO_MINIMO + ". Valor recibido: " + monto);
        }
    }

    private void validarMetodoPago(String metodoPago) {
        if (metodoPago == null || !METODOS_VALIDOS.contains(metodoPago)) {
            throw new IllegalArgumentException(
                    "Método de pago inválido: " + metodoPago
                    + ". Métodos aceptados: " + METODOS_VALIDOS);
        }
    }
}
