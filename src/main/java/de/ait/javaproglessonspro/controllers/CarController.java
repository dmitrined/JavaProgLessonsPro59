package de.ait.javaproglessonspro.controllers;


import de.ait.javaproglessonspro.enums.FuelType;
import de.ait.javaproglessonspro.model.Car;
import de.ait.javaproglessonspro.repository.CarRepository;
import de.ait.javaproglessonspro.validation.CarValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@Tag(name = "Car management API")
@RestController
@RequestMapping("/api/cars")
@Slf4j
public class CarController {

    private final CarRepository carRepository;
    private final CarValidator carValidator;

    @Value("${app.dealership.name:Welcome to AIT Gr.59 API}")
    private String dealershipName;

    public CarController(CarRepository carRepository, CarValidator carValidator) {
        this.carRepository = carRepository;
        this.carValidator = carValidator;
    }

    @GetMapping("/info")
    public ResponseEntity<String> getInfo() {
        return ResponseEntity.ok("Welcome to the " + dealershipName + " car dealership!");
    }

    @Operation(summary = "Get all cars")
    @GetMapping
    public ResponseEntity<List<Car>> getAllCars() {
        return ResponseEntity.ok(carRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Car> getCarById(@PathVariable Long id) {
        if (!carRepository.existsById(id)) {
            log.warn("Car with id {} not found", id);
            return ResponseEntity.notFound().build();
        }
        log.info("Car with id {} found", id);
        return ResponseEntity.ofNullable(carRepository.findById(id).orElse(null));
    }

    @Operation(summary = "Delete a car by id")
    @DeleteMapping("/{id}")
    public ResponseEntity deleteCar(@PathVariable Long id) {
        if (!carRepository.existsById(id)) {
            log.warn("Car with id {} not found", id);
            return ResponseEntity.notFound().build();
        }
        carRepository.deleteById(id);
        log.info("Car with id {} deleted", id);
        return ResponseEntity.noContent().build();
    }


    //api/cars/search?brand=BMW
    @GetMapping("/search")
    public ResponseEntity<List<Car>> searchCars(@RequestParam String brand) {
        return ResponseEntity.ok(carRepository.findByBrand(brand));
    }


    @Operation(summary = "Add a new car")
    @PostMapping
    public ResponseEntity<Long> addCar(@RequestBody Car car) {
        if (!carValidator.isValid(car)) {
            return ResponseEntity.badRequest().build();
        }
        Car savedCar = carRepository.save(car);
        if (savedCar == null) {
            log.error("Car could not be saved");
            return ResponseEntity.badRequest().build();
        }
        log.info("Car with id {} saved", savedCar.getId());
        return new ResponseEntity(HttpStatusCode.valueOf(201));
    }

    @Operation(summary = "Update one car by id")
    @PutMapping("/{id}")
    public ResponseEntity updateCar(@PathVariable Long id, @RequestBody Car car) {
        if (!carValidator.isValid(car)) {
            return ResponseEntity.badRequest().build();
        }
        if (carRepository.existsById(id)) {
            Car carToUpdate = carRepository.findById(id).orElse(null);
            carToUpdate.setBrand(car.getBrand());
            carToUpdate.setModel(car.getModel());
            carToUpdate.setProductionYear(car.getProductionYear());
            carToUpdate.setMileage(car.getMileage());
            carToUpdate.setPrice(car.getPrice());
            carToUpdate.setStatus(car.getStatus());
            carToUpdate.setColor(car.getColor());
            carToUpdate.setHorsepower(car.getHorsepower());
            carToUpdate.setFuelType(car.getFuelType());
            carToUpdate.setTransmission(car.getTransmission());
            carRepository.save(carToUpdate);
            log.info("Car with id {} updated", id);
            return ResponseEntity.ok("updated car with id = " + id);
        }
        log.warn("Car with id {} not found", id);
        return ResponseEntity.notFound().build();
    }

    // GET /api/cars/by-price?min=10000&max=20000
    @GetMapping("/by-price")
    public ResponseEntity<List<Car>> searchByPriceBetween(
            @RequestParam int min, @RequestParam int max
    ) {
        return ResponseEntity.ok(carRepository.findByPriceBetween(min, max));
    }

    // GET /api/cars/by-color?color=black

    @Operation(summary = "Search cars by color",
            description = "Returns a list of cars with the specified color (case-insensitive). " +
                    "Available colors include Black, White, Silver, Blue, Red, Gray, etc. " +
                    "Example: /api/cars/by-color?color=black")
    @GetMapping("/by-color")
    public ResponseEntity<List<Car>> getCarByColor(@RequestParam String color) {
        List<Car> cars = carRepository.findByColorIgnoreCase(color);
        if (cars.isEmpty()) {
            log.warn("Color {} not found", color);
            return ResponseEntity.notFound().build();
        }
        log.info("Color {} found", color);
        return ResponseEntity.ok(cars);
    }

    @Operation(summary = "Search cars by fuel type",
            description = "Returns a list of cars with the specified fuel type. " +
                    "Available fuel types: PETROL, DIESEL, ELECTRIC, HYBRID. " +
                    "Example: /api/cars/by-fuel?fuelType=DIESEL")
    @GetMapping("/by-fuel")
    public ResponseEntity<List<Car>> getCarByFuelType(@RequestParam FuelType fuelType) {
        List<Car> cars = carRepository.findByFuelType(fuelType);
        if (cars.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(cars);
    }

    // GET /api/cars/by-power?minHp=150&maxHp=300
    @Operation(summary = "Search cars with horse power between given range")
    @GetMapping("/by-power")
    public ResponseEntity<List<Car>> searchByHorsePower(
            @RequestParam int minHp,
            @RequestParam int maxHp) {

        if (minHp < 0 || maxHp < 0 || minHp > maxHp) {
            return ResponseEntity.badRequest().build();
        }

        List<Car> cars = carRepository.findByHorsepowerBetween(minHp, maxHp);
        if (cars.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(cars);
    }

}