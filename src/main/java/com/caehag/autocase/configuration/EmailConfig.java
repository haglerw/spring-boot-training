package com.caehag.autocase.configuration;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

import static com.caehag.autocase.constant.EmailConstant.*;

@Configuration
public class EmailConfig {

    @Bean
    public JavaMailSender mailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setProtocol(SIMPLE_MAIL_TRANSFER_PROTOCOL);
        mailSender.setHost(GMAIL_SMTP_SERVER);
        mailSender.setPort(DEFAULT_PORT);
        mailSender.setUsername(USERNAME);
        mailSender.setPassword(PASSWORD);

        Properties props = System.getProperties();
        props.put(SMTP_AUTH, true);
        props.put(SMTP_STARTTLS_ENABLE, true);

        mailSender.setJavaMailProperties(props);

        return mailSender;
    }

    @Bean
    public VelocityEngine velocityEngine() {
        VelocityEngine engine = new VelocityEngine();

        engine.setProperty("resource.loader", "class");
        engine.setProperty("class.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");

        return engine;
    }
}
