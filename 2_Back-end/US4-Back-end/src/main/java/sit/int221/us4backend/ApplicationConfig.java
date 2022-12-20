package sit.int221.us4backend;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import sit.int221.us4backend.utils.*;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Properties;

@Configuration
public class ApplicationConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper;
    }

//    private Argon2PasswordEncoder argon2Encoder = new Argon2PasswordEncoder(16, 16, 1, 2048, 3);
    private Argon2PasswordEncoder argon2Encoder = new Argon2PasswordEncoder();
    @Bean
    public Argon2PasswordEncoder argon2Encoder() { return argon2Encoder; }

    private ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    @Bean
    public Validator validator() {
        return validatorFactory.getValidator();
    }

    @Bean
    public ListMapper listMapper() {
        return ListMapper.getInstance();
    }

    @Bean
    public DateTimeManager dateTimeManager() {
        return DateTimeManager.getInstance();
    }

    @Bean
    public EventValidator eventValidator() { return EventValidator.getInstance(); }

    @Bean
    public EventCategoryValidator eventCategoryValidator() { return EventCategoryValidator.getInstance(); }

    @Bean
    public UserValidator userValidator() { return UserValidator.getInstance(); }

    @Bean
    public JwtTokenUtil jwtTokenUtil() { return JwtTokenUtil.getInstance(); }

    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);

        mailSender.setUsername("chotiphat.que@mail.kmutt.ac.th");
        mailSender.setPassword("qpcmquimhxvqbykh");

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }

//    @Bean(name = "multipartResolver")
//    public MultipartResolver multipartResolver() {
//        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
//        multipartResolver.setMaxUploadSizePerFile(10485760);
//        return multipartResolver;
//    }

//    @Bean
//    public StandardServletMultipartResolver multipartResolver() {
//        return new StandardServletMultipartResolver();
//    }

}
