package br.com.dovalerio.cars_api.vehicle.service;

import br.com.dovalerio.cars_api.vehicle.Vehicle;
import br.com.dovalerio.cars_api.vehicle.dto.request.CreateVehicleRequest;
import br.com.dovalerio.cars_api.vehicle.dto.request.PatchVehicleRequest;
import br.com.dovalerio.cars_api.vehicle.dto.request.UpdateVehicleRequest;
import br.com.dovalerio.cars_api.vehicle.dto.response.VehicleBrandReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;


public interface VehicleService {

    Vehicle create(CreateVehicleRequest request);

    Page<Vehicle> findAll(
            String brand,
            Integer year,
            String color,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Pageable pageable
    );

    Vehicle findById(UUID id);

    Vehicle update(UUID id, UpdateVehicleRequest request);

    Vehicle patch(UUID id, PatchVehicleRequest request);

    void delete(UUID id);

    List<VehicleBrandReport> reportByBrand();
}