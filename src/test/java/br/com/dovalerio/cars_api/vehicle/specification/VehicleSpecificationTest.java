package br.com.dovalerio.cars_api.vehicle.specification;

import br.com.dovalerio.cars_api.vehicle.Vehicle;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.persistence.criteria.*;

import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehicleSpecificationTest {

    @Mock
    private Root<Vehicle> root;

    @Mock
    private CriteriaQuery<?> query;

    @Mock
    private CriteriaBuilder cb;

    @Mock
    private Path<Boolean> booleanPath;

    @Mock
    private Path<String> stringPath;

    @Mock
    private Path<Integer> intPath;

    @Mock
    private Path<BigDecimal> bigDecimalPath;

    @Mock
    private Predicate predicate;

    @Test
    void shouldFilterActive() {
        when(root.<Boolean>get("active")).thenReturn(booleanPath);
        when(cb.isTrue(booleanPath)).thenReturn(predicate);

        Specification<Vehicle> spec = VehicleSpecification.isActive();

        Predicate result = spec.toPredicate(root, query, cb);

        assertNotNull(result);
        verify(cb).isTrue(booleanPath);
    }

    @Test
    void shouldFilterByBrand() {
        when(root.<String>get("brand")).thenReturn(stringPath);
        when(cb.equal(stringPath, "Toyota")).thenReturn(predicate);

        Specification<Vehicle> spec = VehicleSpecification.hasBrand("Toyota");

        spec.toPredicate(root, query, cb);

        verify(cb).equal(stringPath, "Toyota");
    }

    @Test
    void shouldFilterByYear() {
        when(root.<Integer>get("year")).thenReturn(intPath);
        when(cb.equal(intPath, 2022)).thenReturn(predicate);

        Specification<Vehicle> spec = VehicleSpecification.hasYear(2022);

        spec.toPredicate(root, query, cb);

        verify(cb).equal(intPath, 2022);
    }

    @Test
    void shouldReturnTrueSpecWhenYearNull() {
        Specification<Vehicle> spec = VehicleSpecification.hasYear(null);

        spec.toPredicate(root, query, cb);

        verify(cb).conjunction();
    }

    @Test
    void shouldFilterByColor() {
        when(root.<String>get("color")).thenReturn(stringPath);
        when(cb.equal(stringPath, "Black")).thenReturn(predicate);

        Specification<Vehicle> spec = VehicleSpecification.hasColor("Black");

        spec.toPredicate(root, query, cb);

        verify(cb).equal(stringPath, "Black");
    }

    @Test
    void shouldReturnTrueSpecWhenColorNull() {
        Specification<Vehicle> spec = VehicleSpecification.hasColor(null);

        spec.toPredicate(root, query, cb);

        verify(cb).conjunction();
    }

    @Test
    void shouldFilterBetween() {
        BigDecimal min = new BigDecimal("10");
        BigDecimal max = new BigDecimal("100");

        when(root.<BigDecimal>get("priceUsd")).thenReturn(bigDecimalPath);
        when(cb.between(bigDecimalPath, min, max)).thenReturn(predicate);

        Specification<Vehicle> spec =
                VehicleSpecification.priceBetween(min, max);

        spec.toPredicate(root, query, cb);

        verify(cb).between(bigDecimalPath, min, max);
    }

    @Test
    void shouldFilterMinOnly() {
        BigDecimal min = new BigDecimal("10");

        when(root.<BigDecimal>get("priceUsd")).thenReturn(bigDecimalPath);
        when(cb.greaterThanOrEqualTo(bigDecimalPath, min)).thenReturn(predicate);

        Specification<Vehicle> spec =
                VehicleSpecification.priceBetween(min, null);

        spec.toPredicate(root, query, cb);

        verify(cb).greaterThanOrEqualTo(bigDecimalPath, min);
    }

    @Test
    void shouldFilterMaxOnly() {
        BigDecimal max = new BigDecimal("100");

        when(root.<BigDecimal>get("priceUsd")).thenReturn(bigDecimalPath);
        when(cb.lessThanOrEqualTo(bigDecimalPath, max)).thenReturn(predicate);

        Specification<Vehicle> spec =
                VehicleSpecification.priceBetween(null, max);

        spec.toPredicate(root, query, cb);

        verify(cb).lessThanOrEqualTo(bigDecimalPath, max);
    }

    @Test
    void shouldReturnTrueSpecWhenNoPrice() {
        Specification<Vehicle> spec =
                VehicleSpecification.priceBetween(null, null);

        spec.toPredicate(root, query, cb);

        verify(cb).conjunction();
    }
}