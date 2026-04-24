package br.com.dovalerio.cars_api.vehicle.dto.request;

import java.math.BigDecimal;

public record UpdateVehicleRequest(
        String brand,
        String model,
        Integer year,
        String color,
        String plate,
        BigDecimal priceBrl
) {}