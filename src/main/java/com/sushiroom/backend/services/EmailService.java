// src/main/java/com/sushiroom/backend/services/EmailService.java
package com.sushiroom.backend.services;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Value("${sendgrid.api.key}")
    private String apiKey;

    @Value("${email.from}")
    private String fromEmail;

    public void sendResetPasswordEmail(String to, String token, String frontendUrl) {
        String resetLink = frontendUrl + "/reset-password?token=" + token;

        try {
            Email from = new Email(fromEmail);
            Email toEmail = new Email(to);
            String subject = "The Sushi Room - Recuperación de contraseña";
            Content content = new Content("text/plain",
                    "Hola,\n\n" +
                            "Hemos recibido una solicitud para restablecer tu contraseña.\n\n" +
                            "Para crear una nueva contraseña, haz clic en el siguiente enlace:\n\n" +
                            resetLink + "\n\n" +
                            "Este enlace expirará en 1 hora.\n\n" +
                            "Si no solicitaste este cambio, ignora este mensaje.\n\n" +
                            "Saludos,\n" +
                            "Equipo de The Sushi Room"
            );

            Mail mail = new Mail(from, subject, toEmail, content);
            SendGrid sg = new SendGrid(apiKey);
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            sg.api(request);
        } catch (Exception e) {
            throw new RuntimeException("Error al enviar email de recuperación con SendGrid", e);
        }
    }

    public void sendTwoFactorCode(String to, String code) {
        try {
            Email from = new Email(fromEmail);
            Email toEmail = new Email(to);
            String subject = "The Sushi Room - Código de verificación 2FA";
            Content content = new Content("text/plain",
                    "Hola,\n\n" +
                            "Tu código de verificación es:\n\n" +
                            code + "\n\n" +
                            "Este código expirará en 5 minutos.\n\n" +
                            "Si no solicitaste este código, ignora este mensaje.\n\n" +
                            "Saludos,\n" +
                            "Equipo de The Sushi Room"
            );

            Mail mail = new Mail(from, subject, toEmail, content);
            SendGrid sg = new SendGrid(apiKey);
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            sg.api(request);
        } catch (Exception e) {
            throw new RuntimeException("Error al enviar código 2FA con SendGrid", e);
        }
    }
}