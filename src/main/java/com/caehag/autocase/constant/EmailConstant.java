package com.caehag.autocase.constant;

public class EmailConstant {
    public static final String SIMPLE_MAIL_TRANSFER_PROTOCOL = "smtps";
    public static final String USERNAME = "";
    public static final String PASSWORD = "";
    public static final String FROM_COMPANY = "";
    public static final String FROM_EMAIL = "";
    public static final String GMAIL_SMTP_SERVER = "smtp.gmail.com";
    public static final int DEFAULT_PORT = 465;
    public static final String SMTP_STARTTLS_ENABLE = "mail.smtp.starttls.enable";
    public static final String SMTP_AUTH = "mail.smtp.auth";
    public static final String WELCOME_SUBJECT = "";
    public static final String PASSWORD_RESET_SUBJECT = "";
    public static final String EMAIL_SENT_SUCCESSFULLY = "Email was sent successfully to ";
    public static final String EMAIL_NOT_SENT = "Email was not sent!";

    // Template Names
    public static final String WELCOME_TEMPLATE = "velocity/welcome.vm";
    public static final String PASSWORD_RESET_TEMPLATE = "velocity/passwordReset.vm";
}
