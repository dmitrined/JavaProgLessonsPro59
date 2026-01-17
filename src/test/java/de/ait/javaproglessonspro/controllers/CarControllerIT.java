package de.ait.javaproglessonspro.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.ait.javaproglessonspro.enums.CarStatus;
import de.ait.javaproglessonspro.model.Car;
import de.ait.javaproglessonspro.enums.FuelType;
import de.ait.javaproglessonspro.enums.Transmission;
import de.ait.javaproglessonspro.repository.CarRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
class CarControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        carRepository.deleteAll();
    }

    private Car buildValidCar(String brand, String model){
        Car car = new Car();
        car.setBrand(brand);
        car.setModel(model);
        car.setProductionYear(2020);
        car.setMileage(30000);
        car.setPrice(30000);
        car.setStatus(CarStatus.AVAILABLE);
        car.setColor("Black");
        car.setHorsepower(200);
        car.setFuelType(FuelType.PETROL);
        car.setTransmission(Transmission.AUTOMATIC);
        return car;
    }

    @Test
    @DisplayName("GET /api/cars/{id} should return car if exists")
    void testGetCarByIdShouldReturnCar() throws Exception {
        Car saved = carRepository.save(buildValidCar("BMW", "X5"));

        mockMvc.perform(get("/api/cars/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.brand", is("BMW")))
                .andExpect(jsonPath("$.model", is("X5")));
    }

    @Test
    @DisplayName("GET /api/cars/{id} should return 404 if not found")
    void testGetCarByIdShouldNotReturnCar() throws Exception {
        mockMvc.perform(get("/api/cars/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/cars should create a new car")
    void testAddCar() throws Exception {
        Car car = buildValidCar("Audi", "A6");

        mockMvc.perform(post("/api/cars")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(car)))
                .andExpect(status().isCreated());

        assertThat(carRepository.count(), is(1L));
    }

    @Test
    @DisplayName("DELETE /api/cars/{id} should delete car")
    void testDeleteCar() throws Exception {
        Car saved = carRepository.save(buildValidCar("Mercedes", "E-Class"));

        mockMvc.perform(delete("/api/cars/{id}", saved.getId()))
                .andExpect(status().isNoContent());

        assertThat(carRepository.existsById(saved.getId()), is(false));
    }

    @Test
    @DisplayName("PUT /api/cars/{id} should update car")
    void testUpdateCar() throws Exception {
        Car saved = carRepository.save(buildValidCar("Tesla", "Model 3"));
        saved.setPrice(35000);

        mockMvc.perform(put("/api/cars/{id}", saved.getId())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(saved)))
                .andExpect(status().isOk());

        Car updated = carRepository.findById(saved.getId()).orElseThrow();
        assertThat(updated.getPrice(), is(35000));
    }

    @Test
    @DisplayName("GET /api/cars should return all cars")
    void testGetAllCars() throws Exception {
        carRepository.save(buildValidCar("BMW", "X5"));
        carRepository.save(buildValidCar("Audi", "A4"));

        mockMvc.perform(get("/api/cars"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @DisplayName("GET /api/cars/by-color should return cars by color")
    void testGetCarsByColor() throws Exception {
        Car blackCar = buildValidCar("BMW", "X5");
        blackCar.setColor("Black");
        carRepository.save(blackCar);

        Car whiteCar = buildValidCar("Audi", "A4");
        whiteCar.setColor("White");
        carRepository.save(whiteCar);

        mockMvc.perform(get("/api/cars/by-color").param("color", "black"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].color", is("Black")));
    }

    @Test
    @DisplayName("GET /api/cars/by-fuel should return cars by fuel type")
    void testGetCarsByFuel() throws Exception {
        Car dieselCar = buildValidCar("BMW", "X5");
        dieselCar.setFuelType(FuelType.DIESEL);
        carRepository.save(dieselCar);

        Car petrolCar = buildValidCar("Audi", "A4");
        petrolCar.setFuelType(FuelType.PETROL);
        carRepository.save(petrolCar);

        mockMvc.perform(get("/api/cars/by-fuel").param("fuelType", "DIESEL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].fuelType", is("DIESEL")));
    }

    @Test
    @DisplayName("GET /api/cars/by-fuel should return 400 for invalid fuel type")
    void testGetCarsByFuelInvalid() throws Exception {
        mockMvc.perform(get("/api/cars/by-fuel").param("fuelType", "GHOST_FUEL"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/cars/by-power should return cars by horsepower range")
    void testGetCarsByPower() throws Exception {
        Car car1 = buildValidCar("BMW", "X5");
        car1.setHorsepower(100);
        carRepository.save(car1);

        Car car2 = buildValidCar("Audi", "A4");
        car2.setHorsepower(200);
        carRepository.save(car2);

        Car car3 = buildValidCar("Tesla", "Model S");
        car3.setHorsepower(500);
        carRepository.save(car3);

        mockMvc.perform(get("/api/cars/by-power")
                        .param("minHp", "150")
                        .param("maxHp", "300"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].brand", is("Audi")));
    }
}