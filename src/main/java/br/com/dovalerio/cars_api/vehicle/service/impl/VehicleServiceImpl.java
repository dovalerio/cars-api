package br.com.dovalerio.cars_api.vehicle.service.impl;

import br.com.dovalerio.cars_api.common.exception.BusinessException;
import br.com.dovalerio.cars_api.common.exception.NotFoundException;

import br.com.dovalerio.cars_api.exchange.proxy.ExchangeRateProxy;
import br.com.dovalerio.cars_api.vehicle.Vehicle;
import br.com.dovalerio.cars_api.vehicle.dto.request.CreateVehicleRequest;
import br.com.dovalerio.cars_api.vehicle.repository.VehicleRepository;
import br.com.dovalerio.cars_api.vehicle.service.VehicleService;
import br.com.dovalerio.cars_api.vehicle.specification.VehicleSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Service
public class VehicleServiceImpl implements VehicleService {

    @Autowired
    private VehicleRepository repository;
    @Autowired
    private ExchangeRateProxy exchangeProxy;

    @Override
    public Vehicle create(CreateVehicleRequest request) {

        if (repository.existsByPlate(request.getPlate())) {
            throw new BusinessException("Plate already exists");
        }

        Vehicle vehicle = new Vehicle();
        BigDecimal rate = exchangeProxy.getUsdToBrlRate();

        BigDecimal priceUsd = request.getPriceBrl()
                .divide(rate, 2, RoundingMode.HALF_UP);

        vehicle.setPriceUsd(priceUsd);


        vehicle.setBrand(request.getBrand());
        vehicle.setModel(request.getModel());
        vehicle.setYear(request.getYear());
        vehicle.setColor(request.getColor());
        vehicle.setPlate(request.getPlate());


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
}