package br.com.dovalerio.cars_api.vehicle.specification;

import br.com.dovalerio.cars_api.vehicle.Vehicle;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public final class VehicleSpecification {

    private VehicleSpecification() {}

    private static final String FIELD_ACTIVE = "active";
    private static final String FIELD_BRAND = "brand";
    private static final String FIELD_YEAR = "year";
    private static final String FIELD_COLOR = "color";
    private static final String FIELD_PRICE = "priceUsd";

    public static Specification<Vehicle> isActive() {
        return (root, query, cb) ->
                cb.isTrue(root.get(FIELD_ACTIVE));
    }

    public static Specification<Vehicle> hasBrand(String brand) {
        if (brand == null) return alwaysTrue();

        return (root, query, cb) ->
                cb.equal(root.get(FIELD_BRAND), brand);
    }

    public static Specification<Vehicle> hasYear(Integer year) {
        if (year == null) return alwaysTrue();

        return (root, query, cb) ->
                cb.equal(root.get(FIELD_YEAR), year);
    }

    public static Specification<Vehicle> hasColor(String color) {
        if (color == null) return alwaysTrue();

        return (root, query, cb) ->
                cb.equal(root.get(FIELD_COLOR), color);
    }

    public static Specification<Vehicle> priceBetween(BigDecimal min, BigDecimal max) {

        if (min == null && max == null) {
            return alwaysTrue();
        }

        return (root, query, cb) -> {
            if (min != null && max != null) {
                return cb.between(root.get(FIELD_PRICE), min, max);
            }

            if (min != null) {
                return cb.greaterThanOrEqualTo(root.get(FIELD_PRICE), min);
            }

            return cb.lessThanOrEqualTo(root.get(FIELD_PRICE), max);
        };
    }

    private static Specification<Vehicle> alwaysTrue() {
        return (root, query, cb) -> cb.conjunction();
    }
}