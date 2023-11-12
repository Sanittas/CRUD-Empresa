package br.com.sanittas.app.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EmailServices {

    @Autowired
    private JavaMailSender emailSender;

    public void enviarEmailComToken(String email, String token) {
        try {
            log.info("Enviando e-mail para: {}", email);

            String assunto = "Recuperação de senha";
            String conteudo = "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "<title>Recuperação de Senha</title>\n" +
                    "<body style=\"margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f0f8ff;\">\n" +
                    "    <center style=\"width: 100%; table-layout: fixed; background-color: #f0f8ff;\">\n" +
                    "        <div class=\"container\" style=\"max-width: 600px; padding: 20px; margin: 20px auto; background-color: #ffffff; border-radius: 10px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); text-align: center;\">\n" +
                    "            <img src=\"https://i.imgur.com/GspdXCW.jpg\" style=\"max-width: 200px; margin-top: 0;\" />\n" +
                    "            <p style=\"text-align: center; margin-top: 0;\">Olá!</p>\n" +
                    "            <p style=\"text-align: center;\">Você solicitou a recuperação de senha. Clique no link abaixo para redefinir sua senha:</p>\n" +
                    String.format("            <div style=\"text-align: center;\"> <a href=\"http://localhost:3000/validarToken/%s\" style=\"display: inline-block; padding: 10px 20px; margin: 20px 0; color: #ffffff; background-color: #909CFF; border-radius: 5px; text-decoration: none;\">Redefinir Senha</a>\n",token) +
                    "            </div>\n" +
                    "            <p style=\"text-align: center;\">Se você não solicitou a recuperação de senha, ignore este e-mail.</p>\n" +
                    "        </div>\n" +
                    "    </center>\n" +
                    "</body>\n" +
                    "</html>";

            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom("sanittas.organization@gmail.com");
            helper.setTo(email);
            helper.setSubject(assunto);
            helper.setText(conteudo, true);

            emailSender.send(message);

            log.info("E-mail enviado com sucesso.");

        } catch (MessagingException e) {
            log.error("Erro ao enviar e-mail: {}", e.getMessage());
            throw new RuntimeException(e.getLocalizedMessage());
        }
    }
}
