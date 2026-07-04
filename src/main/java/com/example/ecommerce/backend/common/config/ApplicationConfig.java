package com.example.ecommerce.backend.common.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Common application bean configuration shared across feature packages.
 *
 * @author Pial Kanti Samadder
 */
@Configuration
public class ApplicationConfig {
    /**
     * Provides the shared Jackson object mapper for application components.
     *
     * <p>
     * Configures:
     * <ul>
     *     <li>Java Time module support</li>
     *     <li>ISO-8601 date serialization instead of timestamps</li>
     *     <li>Graceful handling of unknown JSON properties</li>
     * </ul>
     *
     * @return configured object mapper
     * @author Pial Kanti Samadder
     */
    @Bean
    public ObjectMapper objectMapper() {
        return JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .build();
    }
}
