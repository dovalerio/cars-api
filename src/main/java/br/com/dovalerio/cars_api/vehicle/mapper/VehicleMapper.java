package br.com.dovalerio.cars_api.vehicle.mapper;

import br.com.dovalerio.cars_api.vehicle.Vehicle;
import br.com.dovalerio.cars_api.vehicle.dto.response.VehicleResponse;

public class VehicleMapper {

    public static VehicleResponse toResponse(Vehicle vehicle) {
        VehicleResponse response = new VehicleResponse();
        response.setId(vehicle.getId());
        response.setBrand(vehicle.getBrand());
        response.setModel(vehicle.getModel());
        response.setYear(vehicle.getYear());
        response.setColor(vehicle.getColor());
        response.setPlate(vehicle.getPlate());
        response.setPriceUsd(vehicle.getPriceUsd());
        return response;
    }
}