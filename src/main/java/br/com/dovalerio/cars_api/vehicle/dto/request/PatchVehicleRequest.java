package br.com.dovalerio.cars_api.vehicle.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record PatchVehicleRequest(

        String brand,

        String model,

        @Min(value = 1900, message = "Year must be valid")
        Integer year,

        String color,

        @Positive(message = "Price must be positive")
        BigDecimal priceBrl
) {}