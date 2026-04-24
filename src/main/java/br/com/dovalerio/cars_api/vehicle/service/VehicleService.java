package br.com.dovalerio.cars_api.vehicle.service;

import br.com.dovalerio.cars_api.vehicle.Vehicle;
import br.com.dovalerio.cars_api.vehicle.dto.request.CreateVehicleRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.UUID;


public interface VehicleService {
    Vehicle create(CreateVehicleRequest request);
    Page<Vehicle> findAll(String brand, Integer year, String color,
                          BigDecimal minPrice, BigDecimal maxPrice,
                          Pageable pageable);

    Vehicle findById(UUID id);

}