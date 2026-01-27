package com.bookmatch.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

/**
 * Servicio para el envío de correos electrónicos.
 * Gestiona el envío de emails de verificación a los usuarios registrados.
 */
@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    /**
     * Envía un correo electrónico de verificación al usuario.
     *
     * @param toEmail Email del destinatario.
     * @param token Token de verificación único generado para el usuario.
     */
    public void sendVerificationEmail(String toEmail, String token) {
        try {
            String subject = "Verifica tu cuenta de BookMatch";
            String verificationUrl = frontendUrl + "/verify-email?token=" + token;

            String body = "¡Bienvenido a BookMatch!\n\n" +
                         "Por favor, haz clic en el siguiente enlace para verificar tu cuenta:\n\n" +
                         verificationUrl + "\n\n" +
                         "Este enlace expirará en 24 horas.\n\n" +
                         "Si no creaste esta cuenta, ignora este mensaje.\n\n" +
                         "Saludos,\nEl equipo de BookMatch";

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(body);
            helper.setFrom("noreply@bookmatch.com");

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Error al enviar el email de verificación", e);
        }
    }
}
