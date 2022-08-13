package com.caehag.autocase.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

import javax.mail.internet.InternetAddress;
import java.io.StringWriter;

import static com.caehag.autocase.constant.AppConstant.CHAR_ENCODING;
import static com.caehag.autocase.constant.EmailConstant.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    private final JavaMailSender mailSender;
    private final VelocityEngine velocityEngine;

    public void sendEmail(String subject, String toEmailAddress, VelocityContext velocityContext, String templateName) {
        MimeMessagePreparator preparator = mimeMessage -> {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
            message.setTo(toEmailAddress);
            message.setFrom(new InternetAddress(FROM_EMAIL, FROM_COMPANY));

            StringWriter stringWriter = new StringWriter();

            velocityEngine.mergeTemplate(templateName, CHAR_ENCODING, velocityContext, stringWriter);

            message.setSubject(subject);
            message.setText(stringWriter.toString(), true);
        };

        try {
            mailSender.send(preparator);
            log.info(EMAIL_SENT_SUCCESSFULLY + toEmailAddress);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(EMAIL_NOT_SENT);
        }
    }
}
