package br.com.dovalerio.cars_api.vehicle.controller;

import br.com.dovalerio.cars_api.vehicle.Vehicle;
import br.com.dovalerio.cars_api.vehicle.dto.request.CreateVehicleRequest;

import br.com.dovalerio.cars_api.vehicle.dto.request.PatchVehicleRequest;
import br.com.dovalerio.cars_api.vehicle.dto.request.UpdateVehicleRequest;
import br.com.dovalerio.cars_api.vehicle.dto.response.VehicleBrandReport;
import br.com.dovalerio.cars_api.vehicle.dto.response.VehicleResponse;

import br.com.dovalerio.cars_api.vehicle.mapper.VehicleMapper;
import br.com.dovalerio.cars_api.vehicle.service.VehicleService;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/veiculos")
public class VehicleController {

    private final VehicleService service;

    public VehicleController(VehicleService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<VehicleResponse> create(
            @Valid @RequestBody CreateVehicleRequest request
    ) {
        Vehicle vehicle = service.create(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
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

    @PutMapping("/{id}")
    public ResponseEntity<VehicleResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateVehicleRequest request
    ) {
        Vehicle updated = service.update(id, request);
        return ResponseEntity.ok(VehicleMapper.toResponse(updated));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<VehicleResponse> patch(
            @PathVariable UUID id,
            @RequestBody PatchVehicleRequest request
    ) {
        Vehicle updated = service.patch(id, request);
        return ResponseEntity.ok(VehicleMapper.toResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/relatorios/por-marca")
    public ResponseEntity<List<VehicleBrandReport>> reportByBrand() {
        return ResponseEntity.ok(service.reportByBrand());
    }
}