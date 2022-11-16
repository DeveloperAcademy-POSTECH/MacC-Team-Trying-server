package trying.cosmos.global.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.web.servlet.server.Encoding;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import java.util.Locale;

@Configuration
public class CommonBeanConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public MessageSource messageSource() {
        Locale.setDefault(Locale.KOREAN);
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();

        messageSource.setBasename("classpath:/messages/messages");
        messageSource.setDefaultEncoding(Encoding.DEFAULT_CHARSET.toString());
        messageSource.setDefaultLocale(Locale.getDefault());
        messageSource.setCacheSeconds(600);

        return messageSource;
    }

    @Bean
    public MessageSourceAccessor messageSourceAccessor() {
        return new MessageSourceAccessor(messageSource());
    }
}
