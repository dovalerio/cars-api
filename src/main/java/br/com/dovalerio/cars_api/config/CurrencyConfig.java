package br.com.dovalerio.cars_api.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.currency")
@Getter
@Setter
public class    CurrencyConfig {

    private int cacheTtlMinutes;
    private Endpoints endpoints;

    @Getter
    @Setter
    public static class Endpoints {
        private String primary;
        private String fallback;
    }
}