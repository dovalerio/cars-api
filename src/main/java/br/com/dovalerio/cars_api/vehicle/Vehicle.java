package br.com.dovalerio.cars_api.vehicle;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "vehicles", uniqueConstraints = {
        @UniqueConstraint(columnNames = "plate")
})
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String brand;
    private String model;
    private Integer year;
    private String color;

    @Column(nullable = false, unique = true)
    private String plate;

    private BigDecimal priceUsd;

    private boolean active;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    protected Vehicle() {}

    public static Vehicle create(
            String brand,
            String model,
            Integer year,
            String color,
            String plate,
            BigDecimal priceUsd
    ) {
        Vehicle v = new Vehicle();
        v.brand = brand;
        v.model = model;
        v.year = year;
        v.color = color;
        v.plate = plate;
        v.priceUsd = priceUsd;
        v.active = true;
        return v;
    }

    public void update(
            String brand,
            String model,
            Integer year,
            String color,
            String plate,
            BigDecimal priceUsd
    ) {
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.color = color;
        this.plate = plate;
        this.priceUsd = priceUsd;
    }

    public void patch(
            String brand,
            String model,
            Integer year,
            String color,
            BigDecimal priceUsd
    ) {
        if (brand != null) this.brand = brand;
        if (model != null) this.model = model;
        if (year != null) this.year = year;
        if (color != null) this.color = color;
        if (priceUsd != null) this.priceUsd = priceUsd;
    }

    public void deactivate() {
        this.active = false;
    }

    public UUID getId() { return id; }
    public String getBrand() { return brand; }
    public String getModel() { return model; }
    public Integer getYear() { return year; }
    public String getColor() { return color; }
    public String getPlate() { return plate; }
    public BigDecimal getPriceUsd() { return priceUsd; }
    public boolean isActive() { return active; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }

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