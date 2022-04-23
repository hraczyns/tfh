package com.hraczynski.trains.user.enabletoken;

import com.hraczynski.trains.email.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;

@Slf4j
@Service
@RequiredArgsConstructor
public class VerificationTokenEmailService {

    private static final String VERIFICATION_TOKEN_TEMPLATE = "verification-token-template";

    private final EmailService emailService;
    private final TemplateEngine templateEngine;

    @Value("${frontend-page-url}")
    private String pageUrl;

    public void sendVerificationEmail(String username, String token) {
        log.info("Mail was sent to user {}", username);
        Context context = new Context();
        context.setVariable("username", username);
        context.setVariable("url", pageUrl + "/signup/verification-token?token=" + token);
        String template = templateEngine.process(VERIFICATION_TOKEN_TEMPLATE, context);
        try {
            emailService.sendMail(template, "User verification email!", username);
        } catch (MessagingException e) {
            log.error("Cannot send mail to user {}", username);
        }
    }
}
