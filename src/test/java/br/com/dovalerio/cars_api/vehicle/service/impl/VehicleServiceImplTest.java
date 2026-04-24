package br.com.dovalerio.cars_api.vehicle.service.impl;

import br.com.dovalerio.cars_api.common.exception.BusinessException;
import br.com.dovalerio.cars_api.exchange.proxy.ExchangeRateProxy;
import br.com.dovalerio.cars_api.vehicle.Vehicle;
import br.com.dovalerio.cars_api.vehicle.dto.request.CreateVehicleRequest;
import br.com.dovalerio.cars_api.vehicle.repository.VehicleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

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

    private CreateVehicleRequest buildRequest() {
        CreateVehicleRequest req = new CreateVehicleRequest();
        req.setBrand("Toyota");
        req.setModel("Corolla");
        req.setYear(2022);
        req.setColor("Black");
        req.setPlate("ABC1234");
        req.setPriceBrl(new BigDecimal("100000"));
        return req;
    }

    @Test
    void shouldCreateVehicleSuccessfully() {
        // given
        CreateVehicleRequest request = buildRequest();

        when(repository.existsByPlate(request.getPlate())).thenReturn(false);
        when(exchangeProxy.getUsdToBrlRate()).thenReturn(new BigDecimal("5.0"));

        when(repository.save(any(Vehicle.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        Vehicle result = service.create(request);

        // then
        assertNotNull(result);
        assertEquals("Toyota", result.getBrand());
        assertEquals("ABC1234", result.getPlate());

        // 100000 / 5 = 20000
        assertEquals(new BigDecimal("20000.00"), result.getPriceUsd());

        verify(repository).save(any(Vehicle.class));
    }

    @Test
    void shouldThrowExceptionWhenPlateAlreadyExists() {
        // given
        CreateVehicleRequest request = buildRequest();

        when(repository.existsByPlate(request.getPlate())).thenReturn(true);

        // when / then
        assertThrows(BusinessException.class, () -> service.create(request));

        verify(repository, never()).save(any());
    }

    @Test
    void shouldCalculateUsdCorrectly() {
        // given
        CreateVehicleRequest request = buildRequest();

        when(repository.existsByPlate(request.getPlate())).thenReturn(false);
        when(exchangeProxy.getUsdToBrlRate()).thenReturn(new BigDecimal("4.0"));

        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        Vehicle result = service.create(request);

        // then
        // 100000 / 4 = 25000
        assertEquals(new BigDecimal("25000.00"), result.getPriceUsd());
    }
}