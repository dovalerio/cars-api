package br.com.dovalerio.cars_api.vehicle.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record UpdateVehicleRequest(
        @NotBlank(message = "Brand is required")
        String brand,
        @NotBlank(message = "Model is required")
        String model,
        @NotNull(message = "Year is required")
        @Min(value = 1900, message = "Year must be valid")
        Integer year,
        @NotBlank(message = "Color is required")
        String color,
        @NotBlank(message = "Plate is required")
        String plate,
        @NotNull(message = "Price is required")
        @Positive(message = "Price must be positive")
        BigDecimal priceBrl
) {}