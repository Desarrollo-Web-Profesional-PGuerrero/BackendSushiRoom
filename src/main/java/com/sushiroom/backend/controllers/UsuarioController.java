package com.sushiroom.backend.controllers;

import com.sushiroom.backend.models.Usuario;
import com.sushiroom.backend.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/usuarios")
@CrossOrigin(origins = "http://localhost:5173")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public List<Usuario> getAllUsuarios() {
        return usuarioRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> createUsuario(@RequestBody Map<String, String> usuarioData) {
        if (usuarioRepository.findByEmail(usuarioData.get("email")).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "El email ya está registrado"));
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(usuarioData.get("nombre"));
        usuario.setEmail(usuarioData.get("email"));
        usuario.setPassword(passwordEncoder.encode(usuarioData.get("password")));
        usuario.setRol(usuarioData.get("rol"));
        usuario.setActivo(true);

        Usuario saved = usuarioRepository.save(usuario);
        saved.setPassword(null);
        
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUsuario(@PathVariable Long id, @RequestBody Map<String, String> usuarioData) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Usuario usuario = usuarioOpt.get();
        usuario.setNombre(usuarioData.get("nombre"));
        usuario.setEmail(usuarioData.get("email"));
        usuario.setRol(usuarioData.get("rol"));
        
        if (usuarioData.containsKey("password") && !usuarioData.get("password").isEmpty()) {
            usuario.setPassword(passwordEncoder.encode(usuarioData.get("password")));
        }

        Usuario updated = usuarioRepository.save(usuario);
        updated.setPassword(null);
        
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUsuario(@PathVariable Long id) {
        if (!usuarioRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        usuarioRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<?> toggleActivo(@PathVariable Long id) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Usuario usuario = usuarioOpt.get();
        usuario.setActivo(!usuario.isActivo());
        Usuario updated = usuarioRepository.save(usuario);
        updated.setPassword(null);
        
        return ResponseEntity.ok(updated);
    }
}