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

            String htmlBody = buildVerificationEmailHtml(verificationUrl);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlBody, true); // true = HTML
            helper.setFrom("noreply@bookmatch.com");

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Error al enviar el email de verificación", e);
        }
    }

    /**
     * Construye el HTML del email de verificación con diseño profesional.
     *
     * @param verificationUrl URL de verificación con el token.
     * @return HTML formateado del email.
     */
    private String buildVerificationEmailHtml(String verificationUrl) {
        return "<!DOCTYPE html>" +
               "<html lang='es'>" +
               "<head>" +
               "    <meta charset='UTF-8'>" +
               "    <meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
               "    <title>Verifica tu cuenta de BookMatch</title>" +
               "</head>" +
               "<body style='margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f4f4f4;'>" +
               "    <table role='presentation' style='width: 100%; border-collapse: collapse;'>" +
               "        <tr>" +
               "            <td style='padding: 40px 0; text-align: center;'>" +
               "                <table role='presentation' style='width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 10px; box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);'>" +
               "                    <!-- Header -->" +
               "                    <tr>" +
               "                        <td style='background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 40px 30px; text-align: center; border-radius: 10px 10px 0 0;'>" +
               "                            <h1 style='color: #ffffff; margin: 0; font-size: 32px; font-weight: bold;'>📚 BookMatch</h1>" +
               "                        </td>" +
               "                    </tr>" +
               "                    <!-- Content -->" +
               "                    <tr>" +
               "                        <td style='padding: 40px 30px;'>" +
               "                            <h2 style='color: #333333; margin: 0 0 20px 0; font-size: 24px;'>¡Bienvenido a BookMatch!</h2>" +
               "                            <p style='color: #666666; font-size: 16px; line-height: 1.6; margin: 0 0 20px 0;'>" +
               "                                Gracias por registrarte en BookMatch, tu plataforma para descubrir y compartir tu pasión por los libros." +
               "                            </p>" +
               "                            <p style='color: #666666; font-size: 16px; line-height: 1.6; margin: 0 0 30px 0;'>" +
               "                                Para completar tu registro y empezar a disfrutar de todas las funcionalidades, por favor verifica tu dirección de correo electrónico haciendo clic en el botón de abajo:" +
               "                            </p>" +
               "                            <!-- Button -->" +
               "                            <table role='presentation' style='margin: 0 auto;'>" +
               "                                <tr>" +
               "                                    <td style='border-radius: 8px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);'>" +
               "                                        <a href='" + verificationUrl + "' style='display: inline-block; padding: 16px 40px; color: #ffffff; text-decoration: none; font-size: 16px; font-weight: bold; border-radius: 8px;'>Verificar mi cuenta</a>" +
               "                                    </td>" +
               "                                </tr>" +
               "                            </table>" +
               "                            <p style='color: #999999; font-size: 14px; line-height: 1.6; margin: 30px 0 0 0;'>" +
               "                                O copia y pega este enlace en tu navegador:" +
               "                            </p>" +
               "                            <p style='color: #667eea; font-size: 14px; word-break: break-all; margin: 10px 0 0 0;'>" +
               "                                " + verificationUrl +
               "                            </p>" +
               "                        </td>" +
               "                    </tr>" +
               "                    <!-- Warning Box -->" +
               "                    <tr>" +
               "                        <td style='padding: 0 30px 30px 30px;'>" +
               "                            <div style='background-color: #fff3cd; border-left: 4px solid #ffc107; padding: 15px; border-radius: 5px;'>" +
               "                                <p style='color: #856404; font-size: 14px; margin: 0; line-height: 1.6;'>" +
               "                                    ⚠️ <strong>Importante:</strong> Este enlace expirará en 24 horas por seguridad." +
               "                                </p>" +
               "                            </div>" +
               "                        </td>" +
               "                    </tr>" +
               "                    <!-- Footer -->" +
               "                    <tr>" +
               "                        <td style='background-color: #f8f9fa; padding: 30px; text-align: center; border-radius: 0 0 10px 10px;'>" +
               "                            <p style='color: #999999; font-size: 14px; margin: 0 0 10px 0;'>" +
               "                                Si no creaste esta cuenta, puedes ignorar este mensaje de forma segura." +
               "                            </p>" +
               "                            <p style='color: #666666; font-size: 14px; margin: 0;'>" +
               "                                Saludos,<br><strong>El equipo de BookMatch</strong>" +
               "                            </p>" +
               "                        </td>" +
               "                    </tr>" +
               "                </table>" +
               "            </td>" +
               "        </tr>" +
               "    </table>" +
               "</body>" +
               "</html>";
    }
}
