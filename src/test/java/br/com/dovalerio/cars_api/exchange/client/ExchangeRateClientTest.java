package br.com.dovalerio.cars_api.exchange.client;

import br.com.dovalerio.cars_api.config.CurrencyConfig;
import br.com.dovalerio.cars_api.exchange.dto.AwesomeApiResponse;
import br.com.dovalerio.cars_api.exchange.dto.FrankfurterResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExchangeRateClientTest {

    @Mock
    private HttpClient httpClient;

    @Mock
    private CurrencyConfig config;

    @InjectMocks
    private ExchangeRateClient client;

    @BeforeEach
    void setup() {
        CurrencyConfig.Endpoints endpoints = new CurrencyConfig.Endpoints();
        endpoints.setPrimary("primary-url");
        endpoints.setFallback("fallback-url");

        when(config.getEndpoints()).thenReturn(endpoints);
    }

    @Test
    void shouldReturnRateFromPrimary() {

        AwesomeApiResponse response = new AwesomeApiResponse();
        AwesomeApiResponse.UsdBrl usd = new AwesomeApiResponse.UsdBrl();
        usd.setBid("5.0");
        response.setUsdbrl(usd);

        when(httpClient.get("primary-url", AwesomeApiResponse.class))
                .thenReturn(response);

        BigDecimal result = client.getRateFromPrimary();

        assertEquals(new BigDecimal("5.0"), result);
    }

    @Test
    void shouldThrowExceptionWhenPrimaryInvalid() {

        when(httpClient.get("primary-url", AwesomeApiResponse.class))
                .thenReturn(new AwesomeApiResponse());

        assertThrows(RuntimeException.class,
                () -> client.getRateFromPrimary());
    }

    @Test
    void shouldReturnRateFromFallback() {

        FrankfurterResponse response = new FrankfurterResponse();
        response.setRates(Map.of("BRL", new BigDecimal("5.2")));

        when(httpClient.get("fallback-url", FrankfurterResponse.class))
                .thenReturn(response);

        BigDecimal result = client.getRateFromFallback();

        assertEquals(new BigDecimal("5.2"), result);
    }

    @Test
    void shouldThrowExceptionWhenFallbackInvalid() {

        when(httpClient.get("fallback-url", FrankfurterResponse.class))
                .thenReturn(new FrankfurterResponse());

        assertThrows(RuntimeException.class,
                () -> client.getRateFromFallback());
    }
}