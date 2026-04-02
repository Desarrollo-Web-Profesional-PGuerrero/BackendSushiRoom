package com.sushiroom.backend.controllers;

import com.sushiroom.backend.models.Usuario;
import com.sushiroom.backend.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
        
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("error", "Credenciales incorrectas"));
        }

        Usuario usuario = usuarioOpt.get();
        
        if (!passwordEncoder.matches(password, usuario.getPassword())) {
            return ResponseEntity.status(401).body(Map.of("error", "Credenciales incorrectas"));
        }

        if (!usuario.isActivo()) {
            return ResponseEntity.status(401).body(Map.of("error", "Usuario inactivo"));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("token", "fake-jwt-token-" + System.currentTimeMillis());
        response.put("usuario", Map.of(
            "id", usuario.getId(),
            "nombre", usuario.getNombre(),
            "email", usuario.getEmail(),
            "rol", usuario.getRol()
        ));
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader(value = "Authorization", required = false) String authorization) {
        if (authorization == null) {
            return ResponseEntity.status(401).body(Map.of("error", "No autorizado"));
        }
        
        // Por ahora, retornamos un usuario de prueba
        // En producción, extraerías el email del token JWT
        return ResponseEntity.ok(Map.of(
            "id", 1,
            "nombre", "Admin Principal",
            "email", "admin@sushiroom.com",
            "rol", "ADMIN"
        ));
    }
}