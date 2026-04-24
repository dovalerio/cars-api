package br.com.dovalerio.cars_api.vehicle;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "vehicles", uniqueConstraints = {
        @UniqueConstraint(columnNames = "plate")
})

@Getter
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Setter
    private String brand;
    @Setter
    private String model;
    @Setter
    private Integer year;
    @Setter
    private String color;

    @Column(nullable = false, unique = true)
    @Setter
    private String plate;

    @Setter
    private BigDecimal priceUsd;

    @Setter
    private Boolean active = true;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = OffsetDateTime.now();
        this.active = true;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }
}