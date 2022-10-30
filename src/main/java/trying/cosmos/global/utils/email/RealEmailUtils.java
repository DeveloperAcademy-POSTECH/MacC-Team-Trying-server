package trying.cosmos.global.utils.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import trying.cosmos.global.aop.LogSpace;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class RealEmailUtils implements EmailUtils {

    private final JavaMailSender emailSender;
    private final TemplateEngine templateEngine;

    @Value("${image.logo}")
    private String logo;

    @Async
    @Override
    public void send(String to, String subject, String template, EmailType type, Map<String, String> model) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            message.setSubject(subject);
            message.setFrom("[Come It] Trying <trying221216@gmail.com>");
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            MimeMultipart multipart = new MimeMultipart("related");

            BodyPart messageBodyPart = new MimeBodyPart();
            Context context = new Context();
            model.forEach(context::setVariable);
            String html = templateEngine.process("email-template", context);
            messageBodyPart.setContent(html, "text/html;charset=utf-8");

            multipart.addBodyPart(messageBodyPart);

            BodyPart imageBodyPart = new MimeBodyPart();
            FileDataSource dataSource = new FileDataSource(logo);
            imageBodyPart.setDataHandler(new DataHandler(dataSource));
            imageBodyPart.setHeader("Content-ID", "<image>");

            multipart.addBodyPart(imageBodyPart);

            message.setContent(multipart);

            log.info("{}Send email to {} code = {}", LogSpace.getSpace(), to, model.get("code"));
            emailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("메일 전송 실패", e);
        }
    }
}