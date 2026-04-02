package com.sushiroom.backend.controllers;

import com.sushiroom.backend.models.Usuario;
import com.sushiroom.backend.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");
        
        Map<String, Object> response = new HashMap<>();
        
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
        
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            
            // Verificar contraseña encriptada
            if (passwordEncoder.matches(password, usuario.getPasswordHash())) {
                response.put("success", true);
                response.put("nombre", usuario.getNombre());
                response.put("email", usuario.getEmail());
                response.put("rol", usuario.getRol());
                return ResponseEntity.ok(response);
            }
        }
        
        response.put("success", false);
        response.put("message", "Credenciales incorrectas");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
    
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, String> userData) {
        String email = userData.get("email");
        String password = userData.get("password");
        String nombre = userData.get("nombre");
        String rol = userData.getOrDefault("rol", "cliente");
        
        Map<String, Object> response = new HashMap<>();
        
        if (usuarioRepository.existsByEmail(email)) {
            response.put("success", false);
            response.put("message", "El email ya está registrado");
            return ResponseEntity.badRequest().body(response);
        }
        
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setEmail(email);
        nuevoUsuario.setPasswordHash(passwordEncoder.encode(password)); // Encriptar
        nuevoUsuario.setNombre(nombre);
        nuevoUsuario.setRol(rol);
        nuevoUsuario.setActivo(true);
        nuevoUsuario.setFechaRegistro(LocalDateTime.now());
        
        usuarioRepository.save(nuevoUsuario);
        
        response.put("success", true);
        response.put("message", "Usuario registrado exitosamente");
        return ResponseEntity.ok(response);
    }
}