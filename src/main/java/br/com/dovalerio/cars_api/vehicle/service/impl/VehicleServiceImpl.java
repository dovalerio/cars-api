package br.com.dovalerio.cars_api.vehicle.service.impl;

import br.com.dovalerio.cars_api.common.exception.BusinessException;
import br.com.dovalerio.cars_api.common.exception.NotFoundException;

import br.com.dovalerio.cars_api.exchange.proxy.ExchangeRateProxy;

import br.com.dovalerio.cars_api.vehicle.Vehicle;
import br.com.dovalerio.cars_api.vehicle.dto.request.CreateVehicleRequest;
import br.com.dovalerio.cars_api.vehicle.repository.VehicleRepository;
import br.com.dovalerio.cars_api.vehicle.service.VehicleService;
import br.com.dovalerio.cars_api.vehicle.specification.VehicleSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository repository;
    private final ExchangeRateProxy exchangeProxy;

    @Override
    public Vehicle create(CreateVehicleRequest request) {

        validatePlate(request.getPlate());

        BigDecimal rate = exchangeProxy.getUsdToBrlRate();

        BigDecimal priceUsd = convertToUsd(request.getPriceBrl(), rate);

        Vehicle vehicle = buildVehicle(request, priceUsd);

        return repository.save(vehicle);
    }

    @Override
    public Page<Vehicle> findAll(String brand, Integer year, String color,
                                 BigDecimal minPrice, BigDecimal maxPrice,
                                 Pageable pageable) {

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
                .filter(Vehicle::getActive)
                .orElseThrow(() -> new NotFoundException("Vehicle not found"));
    }

    private void validatePlate(String plate) {
        if (repository.existsByPlate(plate)) {
            throw new BusinessException("Plate already exists");
        }
    }

    private BigDecimal convertToUsd(BigDecimal priceBrl, BigDecimal rate) {
        return priceBrl.divide(rate, 2, RoundingMode.HALF_UP);
    }

    private Vehicle buildVehicle(CreateVehicleRequest request, BigDecimal priceUsd) {
        return Vehicle.builder()
                .brand(request.getBrand())
                .model(request.getModel())
                .year(request.getYear())
                .color(request.getColor())
                .plate(request.getPlate())
                .priceUsd(priceUsd)
                .build();
    }
}