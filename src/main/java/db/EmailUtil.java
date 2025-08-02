/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package db;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

/**
 *
 * @author HuuDuc
 */
public class EmailUtil {

    private static final Dotenv dotenv = Dotenv.configure().load();
    private static final String SMTP_HOST = dotenv.get("SMTP_HOST");
    private static final String SMTP_PORT = dotenv.get("SMTP_PORT");
    private static final String SMTP_USER = dotenv.get("SMTP_USER");
    private static final String SMTP_PASSWORD = dotenv.get("SMTP_PASSWORD");

    // Sinh mã OTP 6 số ngẫu nhiên
    public static String generateOtp() {
        return String.format("%06d", (int) (Math.random() * 1000000));
    }

    // Gửi mail OTP xác thực tài khoản
    public static void sendOtpEmail(String recipient, String otp) throws MessagingException {
        send(
                recipient,
                "Mã OTP xác thực tài khoản - EcoMart",
                "Mã OTP của bạn là: " + otp + "\nVui lòng nhập mã này để hoàn tất đăng ký.\nMã có hiệu lực trong 1 phút."
        );
    }

    // Gửi mail tự do (ví dụ: xác nhận quên mật khẩu)
    public static void send(String recipient, String subject, String body) throws MessagingException {
        if (SMTP_HOST == null || SMTP_PORT == null || SMTP_USER == null || SMTP_PASSWORD == null) {
            throw new IllegalStateException("SMTP configuration is missing");
        }

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.ssl.trust", SMTP_HOST);

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SMTP_USER, SMTP_PASSWORD);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(SMTP_USER));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
        message.setSubject(subject);
        message.setText(body);
        Transport.send(message);
        System.out.println("Email sent successfully to " + recipient);
    }
}
