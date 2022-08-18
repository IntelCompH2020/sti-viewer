package gr.cite.notification.service.notify;

import gr.cite.notification.common.StringUtils;
import gr.cite.notification.common.enums.NotificationContactType;
import gr.cite.notification.config.email.EmailProperties;
import gr.cite.notification.service.contact.model.Contact;
import gr.cite.notification.service.contact.model.EmailContact;
import gr.cite.notification.service.message.model.EmailMessage;
import gr.cite.notification.service.message.model.Message;
import gr.cite.tools.logging.LoggerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.Charset;

import static java.nio.charset.StandardCharsets.UTF_8;

@Component
public class EmailNotifier implements Notify{
    private final static LoggerService logger = new LoggerService(LoggerFactory.getLogger(EmailNotifier.class));

    private final JavaMailSender javaMailSender;
    private final EmailProperties emailProperties;

    @Autowired
    public EmailNotifier(JavaMailSender javaMailSender, EmailProperties emailProperties) {
        this.javaMailSender = javaMailSender;
        this.emailProperties = emailProperties;
    }

    @Override
    public String notify(Contact contact, Message message) {
        EmailContact emailContact = (EmailContact) contact;
        EmailMessage emailMessage = (EmailMessage) message;

        if (emailContact == null || emailContact.getEmails() == null || emailContact.getEmails().stream().allMatch(StringUtils::isNullOrEmpty)) throw new IllegalArgumentException("contact not provided");
        if (emailMessage == null) throw new IllegalArgumentException("message not provided");

        try {
            MimeMessage mailMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mailMessage, UTF_8.name());
            helper.setTo(emailContact.getEmails().toArray(new String[0]));
            helper.setCc(emailContact.getCcEmails().toArray(new String[0]));
            helper.setBcc(emailContact.getBccEmails().toArray(new String[0]));
            helper.setSubject(emailMessage.getSubject());
            helper.setText(emailMessage.getBody(), true);
            helper.setFrom(emailProperties.getAddress());
            javaMailSender.send(mailMessage);
        } catch (MessagingException e) {
            logger.error(e.getLocalizedMessage(), e);
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public NotificationContactType supports() {
        return NotificationContactType.EMAIL;
    }
}
