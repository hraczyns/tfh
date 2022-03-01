package com.hraczynski.trains.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import java.io.File;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationEmailService {
    private static final String INIT_TEMPLATE = "init-reservation-template";
    private static final String COMPLETED_TEMPLATE = "completed-reservation-template";

    @Value("${mail-frontend-page-url}")
    private String pageUrl;

    private final EmailService emailService;
    private final TemplateEngine templateEngine;

    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public void sendReservationInitEmail(String email, String reservationIdentifier) {
        log.info("Preparing init reservation mail");
        Context context = getParams(email, reservationIdentifier);
        String template = templateEngine.process(INIT_TEMPLATE, context);
        try {
            log.info("Attempting to send mail to user {}", email);
            emailService.sendMail(template, "Reservation created!", email);
        } catch (MessagingException e) {
            log.error("Cannot send email to user {}", email);
        }
    }

    public void sendReservationInitEmail(List<String> emails, String reservationIdentifier) {
        try {
            executorService.invokeAll(emails.stream()
                    .map(email -> (Callable<Void>) () -> {
                        sendReservationInitEmail(email, reservationIdentifier);
                        return null;
                    })
                    .collect(Collectors.toList())
            );
        } catch (InterruptedException e) {
            log.error("Error sending concurrent mails");
        }
    }

    public void sendReservationEmailWithTicket(List<String> emails, String reservationIdentifier, File ticket) {
        try {
            executorService.invokeAll(emails.stream()
                    .map(email -> (Callable<Void>) () -> {
                        sendReservationEmailWithTicket(email, reservationIdentifier, ticket);
                        return null;
                    })
                    .collect(Collectors.toList())
            );
        } catch (InterruptedException e) {
            log.error("Error sending concurrent mails");
        }
    }

    private void sendReservationEmailWithTicket(String email, String reservationIdentifier, File ticket) {
        log.info("Preparing reservation mail with ticket");
        Context context = getParams(email, reservationIdentifier);
        String template = templateEngine.process(COMPLETED_TEMPLATE, context);
        try {
            log.info("Attempting to send mail to user {}", email);
            emailService.sendMail(template, "Reservation completed!", ticket, email);
        } catch (MessagingException e) {
            log.error("Cannot send email to user {}", email);
        }
    }

    private Context getParams(String email, String reservationIdentifier) {
        Context context = new Context();
        context.setVariable("username", email);
        context.setVariable("reservationId", reservationIdentifier);
        context.setVariable("reservationLink", pageUrl + "/reservations?identifier=" + reservationIdentifier);
        context.setVariable("loginLink", pageUrl + "/login");
        return context;
    }
}
