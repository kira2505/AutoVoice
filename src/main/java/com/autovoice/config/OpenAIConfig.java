package com.autovoice.config;

import com.openai.springboot.OpenAIClientCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAIConfig {

    @Value("${openai.api-key}")
    private String apiKey;

    @Value("${openai.base-url}")
    private String baseUrl;

    @Bean
    public OpenAIClientCustomizer openAIClientCustomizer() {
        return builder -> builder
                .apiKey(apiKey)
                .baseUrl(baseUrl)
                .maxRetries(3);
    }
}
