package br.com.dovalerio.cars_api.exchange.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AwesomeApiResponse {

    @JsonProperty("USDBRL")
    private UsdBrl usdbrl;

    @Getter
    @Setter
    public static class UsdBrl {
        private String bid;
    }
}