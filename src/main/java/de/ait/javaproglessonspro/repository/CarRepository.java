package de.ait.javaproglessonspro.repository;

import de.ait.javaproglessonspro.enums.CarStatus;
import de.ait.javaproglessonspro.enums.Transmission;
import de.ait.javaproglessonspro.model.Car;
import de.ait.javaproglessonspro.enums.FuelType;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarRepository extends JpaRepository<Car, Long> {

    //SELECT * FROM CARS WHERE brand = ?
    List<Car> findByBrand(String brand);

    List<Car> findByBrandIgnoreCase(String brand);

    List<Car> findByStatus(CarStatus status);

    boolean existsById(@NonNull Long id);

    List<Car> findByPriceBetween(int min, int max);

    List<Car> findByColorIgnoreCase(String color);

    List<Car> findByFuelType(FuelType fuelType);

    List<Car> findByHorsepowerBetween(int minHp, int maxHp);

    List<Car> findCarByTransmission(Transmission transmission);

}