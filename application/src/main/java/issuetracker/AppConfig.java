package issuetracker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class AppConfig {

    private static Logger LOG = LogManager.getLogger(AppConfig.class);

    @Bean
    public WebMvcConfigurer mvcConfigurer(@Value("${frontend-path}") String frontendPath, @Value("${frontend-url}") String frontendUrl) {
        return new WebMvcConfigurer() {
            @Override
            public void addResourceHandlers (ResourceHandlerRegistry registry) {
                registry
                        .addResourceHandler(frontendUrl)
                        .addResourceLocations(Paths.get(frontendPath).toUri().toString());
            }
        };
    }

}
