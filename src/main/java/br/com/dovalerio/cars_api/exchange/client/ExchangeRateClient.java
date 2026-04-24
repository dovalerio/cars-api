package br.com.dovalerio.cars_api.exchange.client;

import br.com.dovalerio.cars_api.config.CurrencyConfig;
import br.com.dovalerio.cars_api.exchange.dto.AwesomeApiResponse;
import br.com.dovalerio.cars_api.exchange.dto.FrankfurterResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ExchangeRateClient {

    @Autowired
    private CurrencyConfig config;

    @Autowired
    private HttpClient httpClient;

    public BigDecimal getRateFromPrimary() {

        AwesomeApiResponse response =
                httpClient.get(
                        config.getEndpoints().getPrimary(),
                        AwesomeApiResponse.class
                );

        if (response == null ||
                response.getUsdbrl() == null ||
                response.getUsdbrl().getBid() == null) {

            throw new RuntimeException("Invalid primary response");
        }

        return new BigDecimal(response.getUsdbrl().getBid());
    }

    public BigDecimal getRateFromFallback() {

        FrankfurterResponse response =
                httpClient.get(
                        config.getEndpoints().getFallback(),
                        FrankfurterResponse.class
                );

        if (response == null ||
                response.getRates() == null ||
                response.getRates().get("BRL") == null) {

            throw new RuntimeException("Invalid fallback response");
        }

        return response.getRates().get("BRL");
    }
}