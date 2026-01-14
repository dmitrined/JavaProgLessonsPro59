package de.ait.controllers;

import de.ait.model.Car;
import de.ait.repository.CarRepository;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cars")
public class CarController {

    private final CarRepository carRepository;

    public CarController(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    @GetMapping
    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

    @GetMapping("/{id}")
    public Car getCarById(@PathVariable Long id) {
        return carRepository.findById(id).orElse(null);
    }

    @DeleteMapping("/{id}")
    public void deleteCar(@PathVariable Long id) {
        carRepository.deleteById(id);
    }

    @GetMapping("/brand/{brand}")
    public List<Car> getCarByBrand(@PathVariable String brand) {
        return carRepository.findByBrand(brand);
    }


    @PostMapping
    public Long addCar(@Valid @RequestBody Car car) {
        return carRepository.save(car).getId();
    }

    @PutMapping("/{id}")
    public String updateCar(@PathVariable Long id, @RequestBody Car car) {
        return carRepository.findById(id)
                .map(carToUpdate -> {
                    carToUpdate.setBrand(car.getBrand());
                    carToUpdate.setModel(car.getModel());
                    carToUpdate.setProductionYear(car.getProductionYear());
                    carToUpdate.setMileage(car.getMileage());
                    carToUpdate.setPrice(car.getPrice());
                    carToUpdate.setStatus(car.getStatus());
                    carRepository.save(carToUpdate);
                    return "updated car with id = " + id;
                })
                .orElse("car with id = " + id + " not found");
    }
}