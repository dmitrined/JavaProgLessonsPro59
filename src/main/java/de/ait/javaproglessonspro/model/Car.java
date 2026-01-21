package de.ait.javaproglessonspro.model;


import de.ait.javaproglessonspro.enums.CarStatus;
import de.ait.javaproglessonspro.enums.FuelType;
import de.ait.javaproglessonspro.enums.Transmission;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;

@Entity
@Table(name = "cars")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "Brand must not be empty")
    private String brand;

    @Column(nullable = false)
    @NotBlank(message = "Model must not be empty")
    private String model;

    @Column(name = "production_year")
    @Min(value = 1900, message = "Year must be greater than 1900")
    private int productionYear;

    @Min(value = 0, message = "Mileage must be greater than 0")
    private int mileage;

    @Min(value = 1, message = "Price must be greater than 0")
    private int price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CarStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FuelType fuelType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Transmission transmission;

    @Column(nullable = false)
    @NotBlank(message = "Color must not be empty")
    private String color;

    @Column(nullable = false)
    @Min(value = 1, message = "Horsepower must be greater than 0")
    private int horsepower;


    public Car(String brand, String model,
               int productionYear,
               int mileage,
               int price,
               CarStatus status,
               String color,
               int horsepower,
               FuelType fuelType,
               Transmission transmission) {
        this.brand = brand;
        this.model = model;
        this.productionYear = productionYear;
        this.mileage = mileage;
        this.price = price;
        this.status = status;
        this.color = color;
        this.horsepower = horsepower;
        this.fuelType = fuelType;
        this.transmission = transmission;
    }
}