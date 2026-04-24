package br.com.dovalerio.cars_api.vehicle.controller;

import br.com.dovalerio.cars_api.vehicle.Vehicle;
import br.com.dovalerio.cars_api.vehicle.dto.request.CreateVehicleRequest;
import br.com.dovalerio.cars_api.vehicle.dto.response.VehicleResponse;
import br.com.dovalerio.cars_api.vehicle.mapper.VehicleMapper;
import br.com.dovalerio.cars_api.vehicle.service.VehicleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/veiculos")
public class VehicleController {

    @Autowired
    private VehicleService service;


    @PostMapping
    public ResponseEntity<VehicleResponse> create(@Valid @RequestBody CreateVehicleRequest request) {
        Vehicle vehicle = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(VehicleMapper.toResponse(vehicle));
    }

    @GetMapping
    public ResponseEntity<Page<VehicleResponse>> list(
            @RequestParam(required = false) String marca,
            @RequestParam(required = false) Integer ano,
            @RequestParam(required = false) String cor,
            @RequestParam(required = false) BigDecimal minPreco,
            @RequestParam(required = false) BigDecimal maxPreco,
            Pageable pageable
    ) {

        Page<Vehicle> page = service.findAll(
                marca, ano, cor, minPreco, maxPreco, pageable
        );

        Page<VehicleResponse> response = page.map(VehicleMapper::toResponse);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleResponse> findById(@PathVariable UUID id) {
        Vehicle vehicle = service.findById(id);
        return ResponseEntity.ok(VehicleMapper.toResponse(vehicle));
    }
}