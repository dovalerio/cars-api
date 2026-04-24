package br.com.dovalerio.cars_api.exchange.proxy;

import br.com.dovalerio.cars_api.config.CurrencyConfig;
import br.com.dovalerio.cars_api.exchange.client.ExchangeRateClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Duration;

@Component
public class ExchangeRateProxy {

    private static final String KEY = "usd_brl_rate";

    @Autowired
    private ExchangeRateClient client;

    @Autowired
    private StringRedisTemplate redis;

    @Autowired
    private CurrencyConfig config;

    public BigDecimal getUsdToBrlRate() {

        String cached = redis.opsForValue().get(KEY);

        try {
            BigDecimal rate = client.getRateFromPrimary();

            redis.opsForValue().set(
                    KEY,
                    rate.toString(),
                    Duration.ofMinutes(config.getCacheTtlMinutes())
            );

            return rate;

        } catch (Exception e1) {

            try {
                BigDecimal rate = client.getRateFromFallback();

                redis.opsForValue().set(
                        KEY,
                        rate.toString(),
                        Duration.ofMinutes(config.getCacheTtlMinutes())
                );

                return rate;

            } catch (Exception e2) {

                if (cached != null) {
                    return new BigDecimal(cached);
                }

                throw new RuntimeException("Currency service unavailable");
            }
        }
    }
}