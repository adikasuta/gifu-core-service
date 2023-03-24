package com.gifu.coreservice.service;
import javax.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class MailService {
    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private TemplateEngine templateEngine;

    public void sendWelcomeEmail(String to, String name) {
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom("your-email@gmail.com");
            messageHelper.setTo(to);
            messageHelper.setSubject("Welcome to my application");
            String content = generateWelcomeEmailContent(name);
            messageHelper.setText(content, true);
        };
        emailSender.send(messagePreparator);
    }

    private String generateWelcomeEmailContent(String name) {
        Context context = new Context();
        context.setVariable("name", name);
        return templateEngine.process("welcome", context);
    }
}
