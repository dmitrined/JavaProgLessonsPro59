package de.ait.javaproglessonspro.controllers;
import de.ait.javaproglessonspro.model.Car;
import de.ait.javaproglessonspro.repository.CarRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
public class LiquibaseIT {
    @Autowired
    private CarRepository carRepository;

    @Test
    void testSeedDataFrom008Exists() {
        List<Car> cars = carRepository.findAll();

        boolean toyotaExists = cars.stream()
                .anyMatch(car -> "Toyota".equals(car.getBrand()) && "Camry".equals(car.getModel()));

        boolean bmwExists = cars.stream()
                .anyMatch(car -> "BMW".equals(car.getBrand()) && "M5".equals(car.getModel()));

        assertTrue(toyotaExists, "Toyota Camry from changeset 008 should exist in test profile");
        assertTrue(bmwExists, "BMW M5 from changeset 008 should exist in test profile");
    }

}
