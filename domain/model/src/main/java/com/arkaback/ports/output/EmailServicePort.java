package com.arkaback.ports.output;

public interface EmailServicePort {

    boolean sendEmail(String to, String subject, String body);
    //Envía un email con plantilla HTML
    boolean sendHtmlEmail(String to, String subject, String htmlBody);
}
