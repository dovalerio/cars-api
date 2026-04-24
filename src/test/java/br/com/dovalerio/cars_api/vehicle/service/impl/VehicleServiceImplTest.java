package br.com.dovalerio.cars_api.vehicle.service.impl;

import br.com.dovalerio.cars_api.common.exception.BusinessException;
import br.com.dovalerio.cars_api.common.exception.NotFoundException;
import br.com.dovalerio.cars_api.exchange.proxy.ExchangeRateProxy;
import br.com.dovalerio.cars_api.vehicle.Vehicle;
import br.com.dovalerio.cars_api.vehicle.dto.request.CreateVehicleRequest;
import br.com.dovalerio.cars_api.vehicle.repository.VehicleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehicleServiceImplTest {

    @Mock
    private VehicleRepository repository;

    @Mock
    private ExchangeRateProxy proxy;

    @InjectMocks
    private VehicleServiceImpl service;

    private CreateVehicleRequest buildRequest() {
        CreateVehicleRequest r = new CreateVehicleRequest();
        r.setBrand("Toyota");
        r.setModel("Corolla");
        r.setYear(2022);
        r.setColor("Black");
        r.setPlate("ABC1234");
        r.setPriceBrl(new BigDecimal("100000"));
        return r;
    }

    @Test
    void shouldCreateVehicleSuccessfully() {

        CreateVehicleRequest req = buildRequest();

        when(repository.existsByPlate(req.getPlate())).thenReturn(false);
        when(proxy.getUsdToBrlRate()).thenReturn(new BigDecimal("5.0"));
        when(repository.save(any(Vehicle.class))).thenAnswer(i -> i.getArgument(0));

        Vehicle result = service.create(req);

        assertNotNull(result);
        assertEquals("Toyota", result.getBrand());
        assertEquals("Corolla", result.getModel());
        assertEquals("Black", result.getColor());
        assertEquals("ABC1234", result.getPlate());
        assertEquals(new BigDecimal("20000.00"), result.getPriceUsd());

        verify(repository).save(any(Vehicle.class));
    }

    @Test
    void shouldThrowWhenPlateAlreadyExists() {

        CreateVehicleRequest req = buildRequest();

        when(repository.existsByPlate(req.getPlate())).thenReturn(true);

        assertThrows(BusinessException.class,
                () -> service.create(req));

        verify(repository, never()).save(any());
        verify(proxy, never()).getUsdToBrlRate();
    }

    @Test
    void shouldCalculateUsdCorrectly() {

        CreateVehicleRequest req = buildRequest();

        when(repository.existsByPlate(req.getPlate())).thenReturn(false);
        when(proxy.getUsdToBrlRate()).thenReturn(new BigDecimal("4.0"));
        when(repository.save(any(Vehicle.class))).thenAnswer(i -> i.getArgument(0));

        Vehicle result = service.create(req);

        assertEquals(new BigDecimal("25000.00"), result.getPriceUsd());
    }

    @Test
    void shouldPropagateExchangeException() {

        CreateVehicleRequest req = buildRequest();

        when(repository.existsByPlate(req.getPlate())).thenReturn(false);
        when(proxy.getUsdToBrlRate()).thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class,
                () -> service.create(req));

        verify(repository, never()).save(any());
    }

    @Test
    void shouldReturnVehicleWhenActive() {

        UUID id = UUID.randomUUID();

        Vehicle vehicle = Vehicle.builder()
                .id(id)
                .brand("Toyota")
                .active(true)
                .build();

        when(repository.findById(id)).thenReturn(Optional.of(vehicle));

        Vehicle result = service.findById(id);

        assertEquals(id, result.getId());
    }

    @Test
    void shouldThrowWhenVehicleNotFound() {

        UUID id = UUID.randomUUID();

        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> service.findById(id));
    }

    @Test
    void shouldThrowWhenVehicleInactive() {

        UUID id = UUID.randomUUID();

        Vehicle vehicle = Vehicle.builder()
                .id(id)
                .active(false)
                .build();

        when(repository.findById(id)).thenReturn(Optional.of(vehicle));

        assertThrows(NotFoundException.class,
                () -> service.findById(id));
    }

    @Test
    void shouldReturnPagedResult() {

        Pageable pageable = PageRequest.of(0, 10);

        Vehicle v = Vehicle.builder()
                .brand("Toyota")
                .active(true)
                .build();

        Page<Vehicle> page = new PageImpl<>(List.of(v));

        when(repository.findAll(
                ArgumentMatchers.<Specification<Vehicle>>any(),
                eq(pageable)
        )).thenReturn(page);

        Page<Vehicle> result = service.findAll(
                "Toyota",
                2022,
                "Black",
                new BigDecimal("10000"),
                new BigDecimal("200000"),
                pageable
        );

        assertEquals(1, result.getContent().size());
    }

    @Test
    void shouldReturnEmptyPageWhenNoResults() {

        Pageable pageable = PageRequest.of(0, 10);

        when(repository.findAll(
                ArgumentMatchers.<Specification<Vehicle>>any(),
                eq(pageable)
        )).thenReturn(Page.empty());

        Page<Vehicle> result = service.findAll(
                null, null, null, null, null, pageable
        );

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldCallRepositoryWithSpecification() {

        Pageable pageable = PageRequest.of(0, 10);

        when(repository.findAll(
                ArgumentMatchers.<Specification<Vehicle>>any(),
                eq(pageable)
        )).thenReturn(Page.empty());

        service.findAll(null, null, null, null, null, pageable);

        verify(repository).findAll(
                ArgumentMatchers.<Specification<Vehicle>>any(),
                eq(pageable)
        );
    }
}