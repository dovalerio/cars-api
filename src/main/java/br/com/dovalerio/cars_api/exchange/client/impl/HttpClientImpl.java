package br.com.dovalerio.cars_api.exchange.client.impl;

import br.com.dovalerio.cars_api.exchange.client.HttpClient;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class HttpClientImpl implements HttpClient {

    private final RestClient restClient = RestClient.create();

    @Override
    public <T> T get(String url, Class<T> responseType) {
        return restClient.get()
                .uri(url)
                .retrieve()
                .body(responseType);
    }
}