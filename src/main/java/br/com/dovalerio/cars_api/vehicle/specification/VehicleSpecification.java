package br.com.dovalerio.cars_api.vehicle.specification;

import br.com.dovalerio.cars_api.vehicle.Vehicle;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class VehicleSpecification {

    public static Specification<Vehicle> isActive() {
        return (root, query, cb) -> cb.isTrue(root.get("active"));
    }

    public static Specification<Vehicle> hasBrand(String brand) {
        return (root, query, cb) ->
                brand == null ? null : cb.equal(root.get("brand"), brand);
    }

    public static Specification<Vehicle> hasYear(Integer year) {
        return (root, query, cb) ->
                year == null ? null : cb.equal(root.get("year"), year);
    }

    public static Specification<Vehicle> hasColor(String color) {
        return (root, query, cb) ->
                color == null ? null : cb.equal(root.get("color"), color);
    }

    public static Specification<Vehicle> priceBetween(BigDecimal min, BigDecimal max) {
        return (root, query, cb) -> {
            if (min == null && max == null) return null;
            if (min != null && max != null)
                return cb.between(root.get("priceUsd"), min, max);
            if (min != null)
                return cb.greaterThanOrEqualTo(root.get("priceUsd"), min);
            return cb.lessThanOrEqualTo(root.get("priceUsd"), max);
        };
    }
}