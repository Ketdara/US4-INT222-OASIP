package sit.int221.us4backend;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sit.int221.us4backend.utils.*;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

@Configuration
public class ApplicationConfig {
    private ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper;
    }

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
}
