package edu.uclm.es.GramolaJSV.services;

import java.util.Properties;

import org.springframework.stereotype.Service;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

@Service
public class MailService {

    private static final String SENDER_EMAIL = "gsidiazsesmero@gmail.com";
    private static final String APP_PASSWORD = "oncu navd zqfv jdtf ";

    public void mandarCorreo(String correo, String link, int tipo) {
        String subject = "";
        String body = "";
        if (tipo == 0) {
            subject = "⏳ ¡Solo un paso más! Confirma tu cuenta de Gramola";

            body = "¡Hola!\n\n" +
                    "Tu cuenta ha sido registrada correctamente. Para empezar a disfrutar de toda nuestra música " +
                    "dentro de tu bar solo falta que confirmes tu primer pago.\n\n" +
                    "⚠️ IMPORTANTE: Por seguridad, el enlace de confirmación caducará en solo 30 MINUTOS. " +
                    "Si el link caduca tendrás que registrarte otra vez\n\n" +
                    "Pulsa en el siguiente link para completar el proceso:\n" +
                    link + "\n\n" +
                    "¡Date prisa, no dejes que la música se detenga!\n\n" +
                    "¡QUEREMOS MARCHA MARCHA!\n\n" +
                    "El equipo de desarrollo";

        } else {

            subject = "🎸 ¡Cambios en el cartel! Actualización de tu cuenta en Gramola";

            body = "¡Hola de nuevo!\n\n" +
                    "Hemos recibido una solicitud para actualizar la información de tu cuenta. " +
                    "En Gramola nos tomamos la seguridad tan en serio como el buen sonido, " +
                    "así que necesitamos que confirmes estos cambios antes de aplicarlos.\n\n" +
                    "Si no has sido tú quien ha solicitado este cambio," +
                    "te recomendamos cambiar tu contraseña inmediatamente.\n\n" +
                    "Pulsa en el siguiente link para empezar los cambios:\n" +
                    link + "\n\n" +
                    "¡Tu bar, tus reglas, tu música!\n\n" +
                    "¡QUE NO PARE LA FIESTA!\n\n" +
                    "El equipo de desarrollo";

        }

        sendEmail(correo, subject, body);
    }

    /**
     * Configura y envía el correo electrónico usando el servidor SMTP de Gmail.
     * 
     * @param recipient La dirección de correo del destinatario.
     * @param subject   El asunto del correo.
     * @param body      El cuerpo del correo.
     */
    public static void sendEmail(String recipient, String subject, String body) {

        // 1. Configurar las propiedades para el servidor SMTP de Gmail (TLS)
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com"); // Servidor SMTP de Google
        props.put("mail.smtp.port", "587"); // Puerto estándar para TLS
        props.put("mail.smtp.auth", "true"); // Se requiere autenticación
        props.put("mail.smtp.starttls.enable", "true");// Habilitar STARTTLS (para seguridad)

        // 2. Crear un objeto Session con autenticación
        // Usamos la Contraseña de Aplicación en lugar de la contraseña personal
        Authenticator auth = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER_EMAIL, APP_PASSWORD);
            }
        };

        Session session = Session.getInstance(props, auth);

        try {
            // 3. Crear el mensaje
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(recipient));
            message.setSubject(subject);
            message.setText(body); // Para enviar HTML, usa message.setContent(body, "text/html");

            // 4. Enviar el mensaje
            Transport.send(message);

            System.out.println("✅ Correo enviado con éxito a: " + recipient);

        } catch (MessagingException e) {
            System.err.println("❌ Error al enviar el correo:");
            e.printStackTrace();
            // Si hay un error, revisa:
            // - Que el puerto 587 no esté bloqueado.
            // - Que la Contraseña de Aplicación sea correcta (16 caracteres).
            // - Que el correo remitente sea tu dirección de Gmail.
        }
    }
}