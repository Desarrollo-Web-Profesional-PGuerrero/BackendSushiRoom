package com.sushiroom.backend.controllers;

import com.sushiroom.backend.models.Usuario;
import com.sushiroom.backend.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "http://localhost:5173")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Obtener todos los usuarios
    @GetMapping
    public List<Usuario> getAllUsuarios() {
        return usuarioRepository.findAll();
    }

    // Obtener solo empleados
    @GetMapping("/empleados")
    public List<Usuario> getEmpleados() {
        return usuarioRepository.findAll().stream()
                .filter(u -> "empleado".equals(u.getRol()))
                .toList();
    }

    // Crear nuevo usuario (empleado)
    @PostMapping
    public ResponseEntity<Map<String, Object>> createUsuario(@RequestBody Map<String, String> userData) {
        Map<String, Object> response = new HashMap<>();

        // Verificar si el email ya existe
        if (usuarioRepository.existsByEmail(userData.get("email"))) {
            response.put("success", false);
            response.put("message", "El email ya está registrado");
            return ResponseEntity.badRequest().body(response);
        }

        // Crear nuevo usuario
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(userData.get("nombre"));
        nuevoUsuario.setEmail(userData.get("email"));
        nuevoUsuario.setPasswordHash(passwordEncoder.encode(userData.get("password")));
        nuevoUsuario.setRol("empleado"); // Siempre se crean como empleados
        nuevoUsuario.setActivo(true);
        nuevoUsuario.setFechaRegistro(LocalDateTime.now());

        usuarioRepository.save(nuevoUsuario);

        response.put("success", true);
        response.put("message", "Empleado creado exitosamente");
        response.put("usuario", nuevoUsuario);
        return ResponseEntity.ok(response);
    }

    // ACTUALIZAR usuario (empleado o admin)
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateUsuario(@PathVariable Integer id, @RequestBody Map<String, String> userData) {
        Map<String, Object> response = new HashMap<>();

        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);

        if (usuarioOpt.isEmpty()) {
            response.put("success", false);
            response.put("message", "Usuario no encontrado");
            return ResponseEntity.notFound().build();
        }

        Usuario usuario = usuarioOpt.get();

        // Actualizar campos
        if (userData.containsKey("nombre")) {
            usuario.setNombre(userData.get("nombre"));
        }
        if (userData.containsKey("email")) {
            // Verificar si el nuevo email ya existe en otro usuario
            if (!usuario.getEmail().equals(userData.get("email")) &&
                    usuarioRepository.existsByEmail(userData.get("email"))) {
                response.put("success", false);
                response.put("message", "El email ya está registrado por otro usuario");
                return ResponseEntity.badRequest().body(response);
            }
            usuario.setEmail(userData.get("email"));
        }
        if (userData.containsKey("rol")) {
            usuario.setRol(userData.get("rol"));
        }

        usuarioRepository.save(usuario);

        response.put("success", true);
        response.put("message", "Usuario actualizado exitosamente");
        response.put("usuario", usuario);

        return ResponseEntity.ok(response);
    }

    // Eliminar usuario
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteUsuario(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();

        if (!usuarioRepository.existsById(id)) {
            response.put("success", false);
            response.put("message", "Usuario no encontrado");
            return ResponseEntity.notFound().build();
        }

        usuarioRepository.deleteById(id);
        response.put("success", true);
        response.put("message", "Usuario eliminado");
        return ResponseEntity.ok(response);
    }
}