package br.com.dovalerio.cars_api.vehicle.repository;

import br.com.dovalerio.cars_api.vehicle.Vehicle;
import br.com.dovalerio.cars_api.vehicle.dto.response.VehicleBrandReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface VehicleRepository extends JpaRepository<Vehicle, UUID>, JpaSpecificationExecutor<Vehicle> {

    boolean existsByPlate(String plate);
    @Query("""
    SELECT new br.com.dovalerio.cars_api.vehicle.dto.response.VehicleBrandReport(
        v.brand, COUNT(v)
    )
    FROM Vehicle v
    WHERE v.active = true
    GROUP BY v.brand
""")
    List<VehicleBrandReport> countByBrand();

}