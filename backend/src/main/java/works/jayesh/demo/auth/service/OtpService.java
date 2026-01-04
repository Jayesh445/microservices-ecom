package works.jayesh.demo.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import works.jayesh.demo.user.model.entity.User;
import works.jayesh.demo.user.repository.UserRepository;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpService {

    private final UserRepository userRepository;
    private final EmailService emailService;

    private static final int OTP_LENGTH = 6;
    private static final int OTP_EXPIRY_MINUTES = 5;

    public void generateAndSendOtp(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        String otp = generateOtp();
        user.setOtp(otp);
        user.setOtpExpiryTime(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES));

        userRepository.save(user);
        emailService.sendOtpEmail(email, otp);

        log.info("OTP generated and sent to: {}", email);
    }

    public boolean verifyOtp(String email, String otp) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        if (user.getOtp() == null || user.getOtpExpiryTime() == null) {
            log.warn("No OTP found for user: {}", email);
            return false;
        }

        if (LocalDateTime.now().isAfter(user.getOtpExpiryTime())) {
            log.warn("OTP expired for user: {}", email);
            clearOtp(user);
            return false;
        }

        if (user.getOtp().equals(otp)) {
            clearOtp(user);
            log.info("OTP verified successfully for user: {}", email);
            return true;
        }

        log.warn("Invalid OTP for user: {}", email);
        return false;
    }

    private void clearOtp(User user) {
        user.setOtp(null);
        user.setOtpExpiryTime(null);
        userRepository.save(user);
    }

    private String generateOtp() {
        Random random = new SecureRandom();
        StringBuilder otp = new StringBuilder(OTP_LENGTH);

        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(random.nextInt(10));
        }

        return otp.toString();
    }
}
