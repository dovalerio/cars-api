package br.com.dovalerio.cars_api.vehicle.service.impl;

import br.com.dovalerio.cars_api.common.exception.BusinessException;
import br.com.dovalerio.cars_api.common.exception.NotFoundException;
import br.com.dovalerio.cars_api.exchange.proxy.ExchangeRateProxy;
import br.com.dovalerio.cars_api.vehicle.Vehicle;
import br.com.dovalerio.cars_api.vehicle.dto.request.CreateVehicleRequest;
import br.com.dovalerio.cars_api.vehicle.dto.request.PatchVehicleRequest;
import br.com.dovalerio.cars_api.vehicle.dto.request.UpdateVehicleRequest;
import br.com.dovalerio.cars_api.vehicle.dto.response.VehicleBrandReport;
import br.com.dovalerio.cars_api.vehicle.repository.VehicleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
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
    private ExchangeRateProxy exchangeProxy;

    @InjectMocks
    private VehicleServiceImpl service;

    private CreateVehicleRequest createRequest() {
        return new CreateVehicleRequest(
                "Toyota",
                "Corolla",
                2022,
                "Black",
                "ABC1234",
                new BigDecimal("100000")
        );
    }

    private UpdateVehicleRequest updateRequest() {
        return new UpdateVehicleRequest(
                "Honda",
                "Civic",
                2023,
                "White",
                "XYZ9876",
                new BigDecimal("120000")
        );
    }

    private Vehicle vehicle() {
        return Vehicle.create(
                "Toyota",
                "Corolla",
                2022,
                "Black",
                "ABC1234",
                new BigDecimal("20000.00")
        );
    }

    @Test
    void shouldCreateVehicleSuccessfully() {
        CreateVehicleRequest request = createRequest();

        when(repository.existsByPlate(request.plate())).thenReturn(false);
        when(exchangeProxy.getUsdToBrlRate()).thenReturn(new BigDecimal("5.0"));
        when(repository.save(any(Vehicle.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Vehicle result = service.create(request);

        assertNotNull(result);
        assertEquals("Toyota", result.getBrand());
        assertEquals("Corolla", result.getModel());
        assertEquals(2022, result.getYear());
        assertEquals("Black", result.getColor());
        assertEquals("ABC1234", result.getPlate());
        assertEquals(new BigDecimal("20000.00"), result.getPriceUsd());

        verify(repository).save(any(Vehicle.class));
    }

    @Test
    void shouldThrowWhenPlateAlreadyExistsOnCreate() {
        CreateVehicleRequest request = createRequest();

        when(repository.existsByPlate(request.plate())).thenReturn(true);

        assertThrows(BusinessException.class, () -> service.create(request));

        verify(repository, never()).save(any());
        verify(exchangeProxy, never()).getUsdToBrlRate();
    }

    @Test
    void shouldPropagateExchangeExceptionOnCreate() {
        CreateVehicleRequest request = createRequest();

        when(repository.existsByPlate(request.plate())).thenReturn(false);
        when(exchangeProxy.getUsdToBrlRate()).thenThrow(new RuntimeException("Exchange error"));

        assertThrows(RuntimeException.class, () -> service.create(request));

        verify(repository, never()).save(any());
    }

    @Test
    void shouldReturnVehicleWhenActive() {
        UUID id = UUID.randomUUID();
        Vehicle vehicle = vehicle();

        when(repository.findById(id)).thenReturn(Optional.of(vehicle));

        Vehicle result = service.findById(id);

        assertSame(vehicle, result);
    }

    @Test
    void shouldThrowWhenVehicleNotFound() {
        UUID id = UUID.randomUUID();

        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.findById(id));
    }

    @Test
    void shouldThrowWhenVehicleInactive() {
        UUID id = UUID.randomUUID();
        Vehicle vehicle = vehicle();
        vehicle.deactivate();

        when(repository.findById(id)).thenReturn(Optional.of(vehicle));

        assertThrows(NotFoundException.class, () -> service.findById(id));
    }

    @Test
    void shouldReturnPagedResult() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Vehicle> page = new PageImpl<>(List.of(vehicle()));

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
                null,
                null,
                null,
                null,
                null,
                pageable
        );

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldUpdateVehicleSuccessfully() {
        UUID id = UUID.randomUUID();
        Vehicle vehicle = vehicle();
        UpdateVehicleRequest request = updateRequest();

        when(repository.findById(id)).thenReturn(Optional.of(vehicle));
        when(repository.existsByPlate(request.plate())).thenReturn(false);
        when(exchangeProxy.getUsdToBrlRate()).thenReturn(new BigDecimal("6.0"));
        when(repository.save(any(Vehicle.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Vehicle result = service.update(id, request);

        assertEquals("Honda", result.getBrand());
        assertEquals("Civic", result.getModel());
        assertEquals(2023, result.getYear());
        assertEquals("White", result.getColor());
        assertEquals("XYZ9876", result.getPlate());
        assertEquals(new BigDecimal("20000.00"), result.getPriceUsd());

        verify(repository).save(vehicle);
    }

    @Test
    void shouldUpdateVehicleWithoutValidatingPlateWhenPlateIsSame() {
        UUID id = UUID.randomUUID();
        Vehicle vehicle = vehicle();

        UpdateVehicleRequest request = new UpdateVehicleRequest(
                "Toyota",
                "Corolla XEI",
                2024,
                "Silver",
                "ABC1234",
                new BigDecimal("150000")
        );

        when(repository.findById(id)).thenReturn(Optional.of(vehicle));
        when(exchangeProxy.getUsdToBrlRate()).thenReturn(new BigDecimal("5.0"));
        when(repository.save(any(Vehicle.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Vehicle result = service.update(id, request);

        assertEquals("Corolla XEI", result.getModel());
        assertEquals(new BigDecimal("30000.00"), result.getPriceUsd());

        verify(repository, never()).existsByPlate(request.plate());
        verify(repository).save(vehicle);
    }

    @Test
    void shouldThrowWhenUpdatePlateAlreadyExists() {
        UUID id = UUID.randomUUID();
        Vehicle vehicle = vehicle();
        UpdateVehicleRequest request = updateRequest();

        when(repository.findById(id)).thenReturn(Optional.of(vehicle));
        when(repository.existsByPlate(request.plate())).thenReturn(true);

        assertThrows(BusinessException.class, () -> service.update(id, request));

        verify(repository, never()).save(any());
        verify(exchangeProxy, never()).getUsdToBrlRate();
    }

    @Test
    void shouldPatchVehicleSuccessfully() {
        UUID id = UUID.randomUUID();
        Vehicle vehicle = vehicle();

        PatchVehicleRequest request = new PatchVehicleRequest(
                "Honda",
                null,
                null,
                "White",
                new BigDecimal("100000")
        );

        when(repository.findById(id)).thenReturn(Optional.of(vehicle));
        when(exchangeProxy.getUsdToBrlRate()).thenReturn(new BigDecimal("5.0"));
        when(repository.save(any(Vehicle.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Vehicle result = service.patch(id, request);

        assertEquals("Honda", result.getBrand());
        assertEquals("Corolla", result.getModel());
        assertEquals(2022, result.getYear());
        assertEquals("White", result.getColor());
        assertEquals("ABC1234", result.getPlate());
        assertEquals(new BigDecimal("20000.00"), result.getPriceUsd());

        verify(repository).save(vehicle);
    }

    @Test
    void shouldPatchVehicleWithoutChangingPriceWhenPriceIsNull() {
        UUID id = UUID.randomUUID();
        Vehicle vehicle = vehicle();

        PatchVehicleRequest request = new PatchVehicleRequest(
                null,
                "Corolla Altis",
                null,
                null,
                null
        );

        when(repository.findById(id)).thenReturn(Optional.of(vehicle));
        when(repository.save(any(Vehicle.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Vehicle result = service.patch(id, request);

        assertEquals("Toyota", result.getBrand());
        assertEquals("Corolla Altis", result.getModel());
        assertEquals(new BigDecimal("20000.00"), result.getPriceUsd());

        verify(exchangeProxy, never()).getUsdToBrlRate();
        verify(repository).save(vehicle);
    }

    @Test
    void shouldDeleteVehicleUsingSoftDelete() {
        UUID id = UUID.randomUUID();
        Vehicle vehicle = vehicle();

        when(repository.findById(id)).thenReturn(Optional.of(vehicle));

        service.delete(id);

        assertFalse(vehicle.isActive());
        verify(repository).save(vehicle);
    }

    @Test
    void shouldReturnReportByBrand() {
        List<VehicleBrandReport> report = List.of(
                new VehicleBrandReport("Toyota", 2L),
                new VehicleBrandReport("Honda", 1L)
        );

        when(repository.countByBrand()).thenReturn(report);

        List<VehicleBrandReport> result = service.reportByBrand();

        assertEquals(2, result.size());
        assertEquals("Toyota", result.getFirst().brand());
        assertEquals(2L, result.getFirst().total());

        verify(repository).countByBrand();
    }
}