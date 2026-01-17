package de.ait.javaproglessonspro.util;

import de.ait.javaproglessonspro.enums.CarStatus;
import de.ait.javaproglessonspro.model.Car;
import de.ait.javaproglessonspro.enums.FuelType;
import de.ait.javaproglessonspro.enums.Transmission;
import de.ait.javaproglessonspro.repository.CarRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InitData {
    @Bean
    CommandLineRunner initDatabase(CarRepository carRepository) {
        return args -> {
            if (carRepository.count() == 0) {
                carRepository.save(new Car("BMW", "X5", 2019, 100000, 50000, CarStatus.AVAILABLE, "Black", 250, FuelType.DIESEL, Transmission.AUTOMATIC));
                carRepository.save(new Car("Audi", "A4", 2018, 100000, 40000, CarStatus.AVAILABLE, "White", 190, FuelType.PETROL, Transmission.MANUAL));
                carRepository.save(new Car("Mercedes", "C-Class", 2020, 80000, 60000, CarStatus.AVAILABLE, "Grey", 204, FuelType.HYBRID, Transmission.AUTOMATIC));
                carRepository.save(new Car("Tesla", "Model 3", 2022, 15000, 55000, CarStatus.AVAILABLE, "Red", 283, FuelType.ELECTRIC, Transmission.AUTOMATIC));
                carRepository.save(new Car("Toyota", "Camry", 2017, 120000, 18000, CarStatus.AVAILABLE, "Silver", 181, FuelType.PETROL, Transmission.AUTOMATIC));
                carRepository.save(new Car("Ford", "Mustang", 2016, 60000, 35000, CarStatus.AVAILABLE, "Yellow", 450, FuelType.PETROL, Transmission.MANUAL));
                carRepository.save(new Car("Porsche", "911", 2021, 10000, 150000, CarStatus.AVAILABLE, "Green", 450, FuelType.PETROL, Transmission.AUTOMATIC));
            }
        };
    }
}