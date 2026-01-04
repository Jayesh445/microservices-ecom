package works.jayesh.demo.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.otp.console-output:false}")
    private boolean consoleOutput;

    @Async
    public void sendOtpEmail(String to, String otp) {
        try {
            // In development mode, print OTP to console
            if (consoleOutput) {
                log.info("\n" +
                        "╔════════════════════════════════════════╗\n" +
                        "║        DEVELOPMENT MODE - OTP          ║\n" +
                        "╠════════════════════════════════════════╣\n" +
                        "║  Email: {:<30} ║\n" +
                        "║  OTP:   {:<30} ║\n" +
                        "║  Valid for: 5 minutes                  ║\n" +
                        "╚════════════════════════════════════════╝\n",
                        to, otp);
            }

            // Always send email (even in dev, for testing)
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Your OTP for E-Commerce App");
            message.setText("Your OTP is: " + otp
                    + "\n\nThis OTP is valid for 5 minutes.\n\nIf you didn't request this, please ignore this email.");

            mailSender.send(message);
            log.info("OTP email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send OTP email to: {}", to, e);
            // In dev mode, still show OTP even if email fails
            if (consoleOutput) {
                log.warn("Email failed, but OTP is available in console above");
            }
        }
    }

    @Async
    public void sendWelcomeEmail(String to, String name) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Welcome to E-Commerce App");
            message.setText("Hello " + name
                    + ",\n\nWelcome to our E-Commerce platform!\n\nYour account has been successfully created.\n\nThank you for joining us!");

            mailSender.send(message);
            log.info("Welcome email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send welcome email to: {}", to, e);
        }
    }
}
