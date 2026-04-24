package br.com.dovalerio.cars_api.exchange.proxy;

import br.com.dovalerio.cars_api.config.CurrencyConfig;
import br.com.dovalerio.cars_api.exchange.client.ExchangeRateClient;
import br.com.dovalerio.cars_api.exchange.exception.CurrencyServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Duration;

@Component
@RequiredArgsConstructor
public class ExchangeRateProxy {

    private static final String KEY = "usd_brl_rate";

    private final ExchangeRateClient client;
    private final StringRedisTemplate redis;
    private final CurrencyConfig config;

    public BigDecimal getUsdToBrlRate() {

        var ops = redis.opsForValue();
        String cached = ops.get(KEY);

        try {
            BigDecimal rate = client.getRateFromPrimary();

            ops.set(KEY, rate.toString(),
                    Duration.ofMinutes(config.getCacheTtlMinutes()));

            return rate;

        } catch (Exception e1) {

            try {
                BigDecimal rate = client.getRateFromFallback();

                ops.set(KEY, rate.toString(),
                        Duration.ofMinutes(config.getCacheTtlMinutes()));

                return rate;

            } catch (Exception e2) {

                if (cached != null) {
                    return new BigDecimal(cached);
                }

                throw new CurrencyServiceException("Currency service unavailable");
            }
        }
    }
}