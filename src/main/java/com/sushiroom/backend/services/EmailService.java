// src/main/java/com/sushiroom/backend/services/EmailService.java
package com.sushiroom.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendResetPasswordEmail(String to, String token, String frontendUrl) {
        String resetLink = frontendUrl + "/reset-password?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("The Sushi Room - Recuperación de contraseña");
        message.setText(
                "Hola,\n\n" +
                        "Hemos recibido una solicitud para restablecer tu contraseña.\n\n" +
                        "Para crear una nueva contraseña, haz clic en el siguiente enlace:\n\n" +
                        resetLink + "\n\n" +
                        "Este enlace expirará en 1 hora.\n\n" +
                        "Si no solicitaste este cambio, ignora este mensaje.\n\n" +
                        "Saludos,\n" +
                        "Equipo de The Sushi Room"
        );

        mailSender.send(message);
    }

    public void sendTwoFactorCode(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("The Sushi Room - Código de verificación 2FA");
        message.setText(
                "Hola,\n\n" +
                        "Tu código de verificación es:\n\n" +
                        code + "\n\n" +
                        "Este código expirará en 5 minutos.\n\n" +
                        "Si no solicitaste este código, ignora este mensaje.\n\n" +
                        "Saludos,\n" +
                        "Equipo de The Sushi Room"
        );

        mailSender.send(message);
    }
}