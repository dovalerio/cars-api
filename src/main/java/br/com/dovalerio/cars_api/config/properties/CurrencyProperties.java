package br.com.dovalerio.cars_api.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "app.currency")
public class CurrencyProperties {

    private int cacheTtlMinutes;
    private Endpoints endpoints;

    @Setter
    @Getter
    public static class Endpoints {
        private String primary;
        private String fallback;

    }
}