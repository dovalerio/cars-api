package br.com.dovalerio.cars_api.exchange.exception;

public class CurrencyServiceException extends RuntimeException {

    public CurrencyServiceException(String message) {
        super(message);
    }
}