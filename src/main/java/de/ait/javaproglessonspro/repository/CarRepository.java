package de.ait.javaproglessonspro.repository;

import de.ait.javaproglessonspro.enums.CarStatus;
import de.ait.javaproglessonspro.enums.FuelType;
import de.ait.javaproglessonspro.model.Car;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CarRepository extends JpaRepository<Car, Long> {

    //SELECT * FROM CARS WHERE brand = ?
    List<Car> findByBrand(String brand);

    List<Car> findByStatus(CarStatus status);

    boolean existsById(@NonNull Long id);

    List<Car> findByPriceBetween(int min, int max);

    List<Car> findByColorIgnoreCase(String color);

    boolean existsByColorIgnoreCase(String color);

    boolean existsByFuelType(FuelType fuelType);

    List<Car> findByFuelType(FuelType fuelType);

    List<Car> findByHorsepowerBetween(int min, int max);

    Optional<Car> findById(Long carId);

}
