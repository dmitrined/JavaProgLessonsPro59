package de.ait.javaproglessonspro.repository;

import de.ait.javaproglessonspro.enums.CarStatus;
import de.ait.javaproglessonspro.model.Car;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarRepository extends JpaRepository<Car, Long> {

    //SELECT * FROM CARS WHERE brand = ?
    List<Car> findByBrand(String brand);

    List<Car> findByStatus(CarStatus status);

    boolean existsById(@NonNull Long id);

    List<Car> findByPriceBetween(int min, int max);

}