package br.com.dovalerio.cars_api.exchange.client;

public interface HttpClient {
    <T> T get(String url, Class<T> responseType);
}