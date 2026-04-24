package br.com.dovalerio.cars_api.exchange.proxy;

import br.com.dovalerio.cars_api.config.CurrencyConfig;
import br.com.dovalerio.cars_api.exchange.client.ExchangeRateClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TExchangeRateProxyTest {

    @Mock
    private ExchangeRateClient client;

    @Mock
    private StringRedisTemplate redis;

    @Mock
    private ValueOperations<String, String> valueOps;

    @Mock
    private CurrencyConfig config;

    @InjectMocks
    private ExchangeRateProxy proxy;

    private static final String KEY = "usd_brl_rate";

    @BeforeEach
    void setup() {
        when(redis.opsForValue()).thenReturn(valueOps);
        when(config.getCacheTtlMinutes()).thenReturn(10);
    }

    @Test
    void shouldReturnRateFromPrimary() {

        when(valueOps.get(anyString())).thenReturn(null);
        when(client.getRateFromPrimary()).thenReturn(new BigDecimal("5.0"));

        BigDecimal result = proxy.getUsdToBrlRate();

        assertEquals(new BigDecimal("5.0"), result);

        verify(client).getRateFromPrimary();
        verify(client, never()).getRateFromFallback();
        verify(valueOps).set(eq(KEY), eq("5.0"), any());
    }

    @Test
    void shouldUseFallbackWhenPrimaryFails() {

        when(valueOps.get(anyString())).thenReturn(null);

        when(client.getRateFromPrimary())
                .thenThrow(new RuntimeException());

        when(client.getRateFromFallback())
                .thenReturn(new BigDecimal("4.5"));

        BigDecimal result = proxy.getUsdToBrlRate();

        assertEquals(new BigDecimal("4.5"), result);

        verify(client).getRateFromPrimary();
        verify(client).getRateFromFallback();
        verify(valueOps).set(eq(KEY), eq("4.5"), any());
    }

    @Test
    void shouldUseCacheWhenBothApisFail() {

        when(valueOps.get(KEY)).thenReturn("4.0");

        when(client.getRateFromPrimary())
                .thenThrow(new RuntimeException());

        when(client.getRateFromFallback())
                .thenThrow(new RuntimeException());

        BigDecimal result = proxy.getUsdToBrlRate();

        assertEquals(new BigDecimal("4.0"), result);

        verify(client).getRateFromPrimary();
        verify(client).getRateFromFallback();
        verify(valueOps, never()).set(any(), any(), any());
    }

    @Test
    void shouldThrowExceptionWhenNoCacheAndApisFail() {

        when(valueOps.get(KEY)).thenReturn(null);

        when(client.getRateFromPrimary())
                .thenThrow(new RuntimeException());

        when(client.getRateFromFallback())
                .thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class,
                () -> proxy.getUsdToBrlRate());
    }

    @Test
    void shouldCallPrimaryThenFallbackInOrder() {

        when(valueOps.get(anyString())).thenReturn(null);

        when(client.getRateFromPrimary())
                .thenThrow(new RuntimeException());

        when(client.getRateFromFallback())
                .thenReturn(new BigDecimal("4.5"));

        proxy.getUsdToBrlRate();

        InOrder inOrder = inOrder(client);

        inOrder.verify(client).getRateFromPrimary();
        inOrder.verify(client).getRateFromFallback();
    }
}