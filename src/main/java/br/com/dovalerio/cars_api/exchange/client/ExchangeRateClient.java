package br.com.dovalerio.cars_api.exchange.client;

import java.math.BigDecimal;

public interface ExchangeRateClient {

    BigDecimal getRateFromPrimary();

    BigDecimal getRateFromFallback();
}