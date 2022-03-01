package com.hraczynski.trains.email;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender javaMailSender;
    private final EmailProperties mailProperties;

    public void sendMail(String template, String title, String... to) throws MessagingException {
        sendMail(template, title, null, to);
    }

    public void sendMail(String template, String title, File file, String... to) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
        mimeMessageHelper.setFrom(mailProperties.getUsername());
        mimeMessageHelper.setTo(to);
        if (file != null) {
            mimeMessageHelper.addAttachment("ticket", file);
        }
        mimeMessageHelper.setSubject(title);
        mimeMessageHelper.setText(template, true);
        javaMailSender.send(mimeMessage);
    }
}
