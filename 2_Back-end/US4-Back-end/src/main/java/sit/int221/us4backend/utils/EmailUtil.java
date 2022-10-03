package sit.int221.us4backend.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Component
public class EmailUtil {
    @Autowired
    private JavaMailSender javaMailSender;

    public void sendRealEmail(String to, String subject, String text) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try{
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(new InternetAddress("noreply@intproj21.sit.kmutt.ac.th", "noreply@intproj21.sit.kmutt.ac.th"));
            helper.setTo(to);
            helper.setSubject(subject);    // [OASIP] Server-side Clinic @ Mon Oct 24, 2022 16:00 - 16:30 (ICT)
            helper.setText(text);          // Booking Name, Event Category, When, Event Notes
            javaMailSender.send(message);
        } catch(Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error when sending email");
        }
    }
}