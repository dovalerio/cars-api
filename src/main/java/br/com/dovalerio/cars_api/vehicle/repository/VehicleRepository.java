package br.com.dovalerio.cars_api.vehicle.repository;

import br.com.dovalerio.cars_api.vehicle.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface VehicleRepository extends JpaRepository<Vehicle, UUID>, JpaSpecificationExecutor<Vehicle> {

    boolean existsByPlate(String plate);

}