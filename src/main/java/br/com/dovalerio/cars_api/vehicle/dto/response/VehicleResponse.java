package br.com.dovalerio.cars_api.vehicle.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VehicleResponse {

    private UUID id;
    private String brand;
    private String model;
    private Integer year;
    private String color;
    private String plate;
    private BigDecimal priceUsd;
}