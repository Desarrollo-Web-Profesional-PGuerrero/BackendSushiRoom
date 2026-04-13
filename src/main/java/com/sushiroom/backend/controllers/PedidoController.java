package com.sushiroom.backend.controllers;

import com.sushiroom.backend.models.Pedido;
import com.sushiroom.backend.models.Usuario;
import com.sushiroom.backend.models.DetallePedido;
import com.sushiroom.backend.repositories.PedidoRepository;
import com.sushiroom.backend.repositories.DetallePedidoRepository;
import com.sushiroom.backend.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:3000"})
public class PedidoController {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private DetallePedidoRepository detallePedidoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Obtener todos los pedidos
    @GetMapping
    public ResponseEntity<List<Pedido>> getAllPedidos() {
        List<Pedido> pedidos = pedidoRepository.findAllByOrderByFechaPedidoDesc();
        pedidos.forEach(pedido -> {
            if (pedido.getDetalles() != null) {
                pedido.getDetalles().forEach(detalle -> detalle.setPedido(null));
            }
        });
        return ResponseEntity.ok(pedidos);
    }

    // Obtener pedido por ID
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getPedidoById(@PathVariable Integer id) {
        Optional<Pedido> pedidoOpt = pedidoRepository.findById(id);

        if (pedidoOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Pedido pedido = pedidoOpt.get();
        List<DetallePedido> detalles = detallePedidoRepository.findByPedidoId(id);

        Map<String, Object> response = new HashMap<>();
        response.put("pedido", pedido);
        response.put("detalles", detalles);

        return ResponseEntity.ok(response);
    }

    // Buscar pedido por número de pedido (para clientes)
    @GetMapping("/buscar/{numeroPedido}")
    public ResponseEntity<?> buscarPedido(@PathVariable String numeroPedido) {
        System.out.println("=== BUSCANDO PEDIDO ===");
        System.out.println("Número de pedido: " + numeroPedido);

        Optional<Pedido> pedidoOpt = pedidoRepository.findByNumeroPedido(numeroPedido);

        if (pedidoOpt.isEmpty()) {
            System.out.println("Pedido NO encontrado");
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Pedido no encontrado");
            errorResponse.put("numeroPedido", numeroPedido);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        Pedido pedido = pedidoOpt.get();
        List<DetallePedido> detalles = detallePedidoRepository.findByPedidoId(pedido.getId());

        System.out.println("Pedido encontrado: " + pedido.getNumeroPedido());

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("pedido", pedido);
        response.put("detalles", detalles);

        return ResponseEntity.ok(response);
    }

    // Obtener pedidos por estado
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Pedido>> getPedidosByEstado(@PathVariable String estado) {
        List<Pedido> pedidos = pedidoRepository.findByEstadoOrderByFechaPedidoDesc(estado);
        return ResponseEntity.ok(pedidos);
    }

    // Actualizar estado del pedido
    @PatchMapping("/{id}/estado")
    public ResponseEntity<Pedido> updateEstado(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        Optional<Pedido> pedidoOpt = pedidoRepository.findById(id);

        if (pedidoOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Pedido pedido = pedidoOpt.get();
        String nuevoEstado = body.get("estado");
        pedido.setEstado(nuevoEstado);

        Pedido pedidoActualizado = pedidoRepository.save(pedido);
        return ResponseEntity.ok(pedidoActualizado);
    }

    // Obtener resumen de pedidos por estado
    @GetMapping("/resumen")
    public ResponseEntity<Map<String, Integer>> getResumenPedidos() {
        Map<String, Integer> resumen = new HashMap<>();
        resumen.put("pendiente", pedidoRepository.countByEstado("pendiente"));
        resumen.put("preparacion", pedidoRepository.countByEstado("preparacion"));
        resumen.put("listo", pedidoRepository.countByEstado("listo"));
        resumen.put("entregado", pedidoRepository.countByEstado("entregado"));
        resumen.put("total", (int) pedidoRepository.count());

        return ResponseEntity.ok(resumen);
    }

    // Crear nuevo pedido
    @PostMapping
    @Transactional
    public ResponseEntity<Map<String, Object>> crearPedido(@RequestBody Map<String, Object> requestBody) {
        try {
            System.out.println("=== CREANDO PEDIDO ===");
            System.out.println("Request recibido: " + requestBody);

            List<Map<String, Object>> items = (List<Map<String, Object>>) requestBody.get("items");
            String metodoPago = (String) requestBody.get("metodoPago");
            String comentarios = (String) requestBody.get("comentarios");

            if (items == null || items.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "El pedido debe tener al menos un producto");
                return ResponseEntity.badRequest().body(response);
            }

            // Calcular total
            BigDecimal total = BigDecimal.ZERO;
            for (Map<String, Object> item : items) {
                BigDecimal precio = new BigDecimal(item.get("precio").toString());
                Integer cantidad = Integer.parseInt(item.get("cantidad").toString());
                total = total.add(precio.multiply(new BigDecimal(cantidad)));
            }

            // 🔥 PASO 1: Crear pedido con número TEMPORAL (para evitar NOT NULL)
            String fechaActual = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String numeroTemporal = "TEMP-" + System.currentTimeMillis();

            Pedido pedido = new Pedido();
            pedido.setNumeroPedido(numeroTemporal);  // ← Temporal, no es nulo
            pedido.setFechaPedido(LocalDateTime.now());
            pedido.setEstado("pendiente");
            pedido.setTotal(total);
            pedido.setMetodoPago(metodoPago);
            pedido.setComentarios(comentarios);
            pedido.setUsuario(null);

            Pedido pedidoGuardado = pedidoRepository.save(pedido);
            pedidoRepository.flush();
            System.out.println("Pedido guardado ID: " + pedidoGuardado.getId());

            // 🔥 PASO 2: Generar número REAL usando el ID
            String numeroReal = String.format("PED-%s-%04d", fechaActual, pedidoGuardado.getId());
            pedidoGuardado.setNumeroPedido(numeroReal);
            pedidoRepository.save(pedidoGuardado);
            System.out.println("Número de pedido asignado: " + numeroReal);

            // 🔥 PASO 3: Crear detalles del pedido
            for (Map<String, Object> item : items) {
                DetallePedido detalle = new DetallePedido();
                detalle.setPedido(pedidoGuardado);
                detalle.setProductoId(Integer.parseInt(item.get("productoId").toString()));
                detalle.setNombreProducto((String) item.get("nombreProducto"));
                detalle.setCantidad(Integer.parseInt(item.get("cantidad").toString()));
                detalle.setPrecioUnitario(new BigDecimal(item.get("precio").toString()));
                detallePedidoRepository.save(detalle);
                System.out.println("Detalle guardado: " + detalle.getNombreProducto());
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("pedido", pedidoGuardado);
            response.put("numeroPedido", numeroReal);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}