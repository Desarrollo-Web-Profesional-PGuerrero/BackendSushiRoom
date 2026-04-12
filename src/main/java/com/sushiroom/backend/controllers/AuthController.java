package com.sushiroom.backend.controllers;

import com.sushiroom.backend.models.Usuario;
import com.sushiroom.backend.repositories.UsuarioRepository;
import com.sushiroom.backend.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    // ==================== LOGIN CON 2FA ====================
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        Map<String, Object> response = new HashMap<>();

        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();

            if (passwordEncoder.matches(password, usuario.getPasswordHash())) {
                // Generar código 2FA
                String twoFactorCode = generateTwoFactorCode();
                usuario.setTwoFactorCode(twoFactorCode);
                usuario.setTwoFactorCodeExpiry(LocalDateTime.now().plusMinutes(5));
                usuarioRepository.save(usuario);

                // Enviar código por email
                try {
                    emailService.sendTwoFactorCode(email, twoFactorCode);
                    response.put("success", true);
                    response.put("requiresTwoFactor", true);
                    response.put("email", email);
                    response.put("message", "Código de verificación enviado a tu correo");
                    return ResponseEntity.ok(response);
                } catch (Exception e) {
                    response.put("success", false);
                    response.put("message", "Error al enviar código de verificación");
                    return ResponseEntity.status(500).body(response);
                }
            }
        }

        response.put("success", false);
        response.put("message", "Credenciales incorrectas");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    // ==================== VERIFICAR CÓDIGO 2FA ====================
    @PostMapping("/verify-2fa")
    public ResponseEntity<Map<String, Object>> verifyTwoFactor(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code");
        Map<String, Object> response = new HashMap<>();

        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

        if (usuarioOpt.isEmpty()) {
            response.put("success", false);
            response.put("message", "Usuario no encontrado");
            return ResponseEntity.badRequest().body(response);
        }

        Usuario usuario = usuarioOpt.get();

        if (usuario.getTwoFactorCode() == null || !usuario.getTwoFactorCode().equals(code)) {
            response.put("success", false);
            response.put("message", "Código inválido");
            return ResponseEntity.badRequest().body(response);
        }

        if (usuario.getTwoFactorCodeExpiry().isBefore(LocalDateTime.now())) {
            response.put("success", false);
            response.put("message", "El código ha expirado. Solicita uno nuevo");
            return ResponseEntity.badRequest().body(response);
        }

        // Limpiar código 2FA
        usuario.setTwoFactorCode(null);
        usuario.setTwoFactorCodeExpiry(null);
        usuarioRepository.save(usuario);

        response.put("success", true);
        response.put("nombre", usuario.getNombre());
        response.put("email", usuario.getEmail());
        response.put("rol", usuario.getRol());

        return ResponseEntity.ok(response);
    }

    // ==================== REENVIAR CÓDIGO 2FA ====================
    @PostMapping("/resend-2fa")
    public ResponseEntity<Map<String, Object>> resendTwoFactor(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        Map<String, Object> response = new HashMap<>();

        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

        if (usuarioOpt.isEmpty()) {
            response.put("success", false);
            response.put("message", "Usuario no encontrado");
            return ResponseEntity.badRequest().body(response);
        }

        Usuario usuario = usuarioOpt.get();

        // Generar nuevo código
        String twoFactorCode = generateTwoFactorCode();
        usuario.setTwoFactorCode(twoFactorCode);
        usuario.setTwoFactorCodeExpiry(LocalDateTime.now().plusMinutes(8));
        usuarioRepository.save(usuario);

        try {
            emailService.sendTwoFactorCode(email, twoFactorCode);
            response.put("success", true);
            response.put("message", "Nuevo código enviado a tu correo");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al enviar el código");
        }

        return ResponseEntity.ok(response);
    }

    // ==================== REGISTRO ====================
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
        nuevoUsuario.setPasswordHash(passwordEncoder.encode(password));
        nuevoUsuario.setNombre(nombre);
        nuevoUsuario.setRol(rol);
        nuevoUsuario.setActivo(true);
        nuevoUsuario.setFechaRegistro(LocalDateTime.now());

        usuarioRepository.save(nuevoUsuario);

        response.put("success", true);
        response.put("message", "Usuario registrado exitosamente");
        return ResponseEntity.ok(response);
    }

    // ==================== RECUPERACIÓN DE CONTRASEÑA ====================
    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, Object>> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        Map<String, Object> response = new HashMap<>();

        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

        if (usuarioOpt.isEmpty()) {
            response.put("success", false);
            response.put("message", "No existe una cuenta con ese correo electrónico");
            return ResponseEntity.badRequest().body(response);
        }

        Usuario usuario = usuarioOpt.get();

        // Generar token único
        String token = UUID.randomUUID().toString();
        usuario.setResetToken(token);
        usuario.setResetTokenExpiry(LocalDateTime.now().plusHours(1));
        usuarioRepository.save(usuario);

        // Enviar email
        try {
            String frontendUrl = "http://localhost:5173";
            emailService.sendResetPasswordEmail(email, token, frontendUrl);

            response.put("success", true);
            response.put("message", "Se ha enviado un enlace de recuperación a tu correo electrónico");
        } catch (Exception e) {
            System.err.println("Error al enviar email: " + e.getMessage());
            response.put("success", false);
            response.put("message", "Error al enviar el correo. Intenta de nuevo.");
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, Object>> resetPassword(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String newPassword = request.get("newPassword");
        Map<String, Object> response = new HashMap<>();

        Optional<Usuario> usuarioOpt = usuarioRepository.findByResetToken(token);

        if (usuarioOpt.isEmpty()) {
            response.put("success", false);
            response.put("message", "Token inválido");
            return ResponseEntity.badRequest().body(response);
        }

        Usuario usuario = usuarioOpt.get();

        if (usuario.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            response.put("success", false);
            response.put("message", "El enlace ha expirado. Solicita un nuevo restablecimiento");
            return ResponseEntity.badRequest().body(response);
        }

        // Actualizar contraseña
        usuario.setPasswordHash(passwordEncoder.encode(newPassword));
        usuario.setResetToken(null);
        usuario.setResetTokenExpiry(null);
        usuarioRepository.save(usuario);

        response.put("success", true);
        response.put("message", "Contraseña actualizada exitosamente");

        return ResponseEntity.ok(response);
    }

    // ==================== MÉTODO PARA GENERAR CÓDIGO 2FA ====================
    private String generateTwoFactorCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }
}