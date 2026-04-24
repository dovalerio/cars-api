package br.com.dovalerio.cars_api.vehicle.service.impl;

import br.com.dovalerio.cars_api.common.exception.BusinessException;
import br.com.dovalerio.cars_api.common.exception.NotFoundException;
import br.com.dovalerio.cars_api.exchange.proxy.ExchangeRateProxy;
import br.com.dovalerio.cars_api.vehicle.Vehicle;
import br.com.dovalerio.cars_api.vehicle.dto.request.CreateVehicleRequest;
import br.com.dovalerio.cars_api.vehicle.dto.request.PatchVehicleRequest;
import br.com.dovalerio.cars_api.vehicle.dto.request.UpdateVehicleRequest;

import br.com.dovalerio.cars_api.vehicle.repository.VehicleRepository;
import br.com.dovalerio.cars_api.vehicle.service.VehicleService;
import br.com.dovalerio.cars_api.vehicle.specification.VehicleSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository repository;
    private final ExchangeRateProxy exchangeProxy;

    public VehicleServiceImpl(
            VehicleRepository repository,
            ExchangeRateProxy exchangeProxy
    ) {
        this.repository = repository;
        this.exchangeProxy = exchangeProxy;
    }

    @Override
    public Vehicle create(CreateVehicleRequest request) {

        validatePlate(request.plate());

        Vehicle vehicle = Vehicle.create(
                request.brand(),
                request.model(),
                request.year(),
                request.color(),
                request.plate(),
                convertToUsd(request.priceBrl())
        );

        return repository.save(vehicle);
    }

    @Override
    public Page<Vehicle> findAll(
            String brand,
            Integer year,
            String color,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Pageable pageable
    ) {

        Specification<Vehicle> spec = Specification
                .where(VehicleSpecification.isActive())
                .and(VehicleSpecification.hasBrand(brand))
                .and(VehicleSpecification.hasYear(year))
                .and(VehicleSpecification.hasColor(color))
                .and(VehicleSpecification.priceBetween(minPrice, maxPrice));

        return repository.findAll(spec, pageable);
    }

    @Override
    public Vehicle findById(UUID id) {
        return repository.findById(id)
                .filter(Vehicle::isActive)
                .orElseThrow(() -> new NotFoundException("Vehicle not found"));
    }

    @Override
    public Vehicle update(UUID id, UpdateVehicleRequest request) {
        validateUpdateRequest(request);

        Vehicle vehicle = findById(id);

        if (!vehicle.getPlate().equals(request.plate())) {
            validatePlate(request.plate());
        }

        vehicle.update(
                request.brand(),
                request.model(),
                request.year(),
                request.color(),
                request.plate(),
                convertToUsd(request.priceBrl())
        );

        return repository.save(vehicle);
    }

    @Override
    public Vehicle patch(UUID id, PatchVehicleRequest request) {
        validatePatchRequest(request);

        Vehicle vehicle = findById(id);

        vehicle.patch(
                request.brand(),
                request.model(),
                request.year(),
                request.color(),
                request.priceBrl() != null
                        ? convertToUsd(request.priceBrl())
                        : null
        );

        return repository.save(vehicle);
    }

    @Override
    public void delete(UUID id) {

        Vehicle vehicle = findById(id);

        vehicle.deactivate();

        repository.save(vehicle);
    }

    @Override
    public List<br.com.dovalerio.cars_api.vehicle.dto.response.VehicleBrandReport> reportByBrand() {
        return repository.countByBrand();
    }

    private void validatePlate(String plate) {
        if (repository.existsByPlate(plate)) {
            throw new BusinessException("Plate already exists");
        }
    }

    private BigDecimal convertToUsd(BigDecimal priceBrl) {
        BigDecimal rate = exchangeProxy.getUsdToBrlRate();
        return priceBrl.divide(rate, 2, RoundingMode.HALF_UP);
    }

    private void validateUpdateRequest(UpdateVehicleRequest request) {
        if (isBlank(request.brand())
                || isBlank(request.model())
                || request.year() == null
                || request.year() < 1900
                || isBlank(request.color())
                || isBlank(request.plate())
                || request.priceBrl() == null
                || request.priceBrl().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Invalid vehicle data for update");
        }
    }

    private void validatePatchRequest(PatchVehicleRequest request) {
        if (request.year() != null && request.year() < 1900) {
            throw new BusinessException("Year must be valid");
        }

        if (request.priceBrl() != null && request.priceBrl().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Price must be positive");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}