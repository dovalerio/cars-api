package br.com.dovalerio.cars_api.exchange.client.impl;

import br.com.dovalerio.cars_api.config.CurrencyConfig;
import br.com.dovalerio.cars_api.exchange.client.ExchangeRateClient;
import br.com.dovalerio.cars_api.exchange.client.HttpClient;
import br.com.dovalerio.cars_api.exchange.dto.AwesomeApiResponse;
import br.com.dovalerio.cars_api.exchange.dto.FrankfurterResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class ExchangeRateClientImpl implements ExchangeRateClient {

    private final CurrencyConfig config;
    private final HttpClient httpClient;

    @Override
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

    @Override
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