package br.com.dovalerio.cars_api.vehicle.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateVehicleRequest {

    @NotBlank(message = "Brand is required")
    private String brand;

    @NotBlank(message = "Model is required")
    private String model;

    @NotNull(message = "Year is required")
    @Min(value = 1900, message = "Year must be valid")
    private Integer year;

    @NotBlank(message = "Color is required")
    private String color;

    @NotBlank(message = "Plate is required")
    private String plate;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private BigDecimal priceBrl;
}