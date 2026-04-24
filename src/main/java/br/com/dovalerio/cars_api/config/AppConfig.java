package br.com.dovalerio.cars_api.config;

import br.com.dovalerio.cars_api.config.properties.CurrencyProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
        CurrencyProperties.class
})
public class AppConfig {
}