package de.ait.controllers;

import de.ait.model.Car;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController //Сообщает Spring, что этот класс является контроллером
@RequestMapping("/api/cars")//Задает базовый путь (URL) для всех методов
public class CarController {

    // Private final : ссылка на список не изменится, но сам список (внутри) можно менять.
    // Я обернул List.of в ArrayList, чтобы сделать его изменяемым (Mutable).
    private final List<Car> allCars = new ArrayList<>(List.of(
            new Car(1L, "BMW", "X5", 2000, 30000, 35000, "AVAILABLE"),
            new Car(2L, "Audi", "A4", 2025, 2000, 25000, "SOLD")
    ));

    @GetMapping//Метод получение всех машин
    public List<Car> getAllCars() {
        return allCars;
    }

    @GetMapping("/{id}")//Получение машины по ID
    public Car getCarById(@PathVariable Long id) {
        if (id == 1L) {
            return new Car(1L, "BMW", "X5", 2000, 30000, 35000, "AVAILABLE");
        } else if (id == 2L) {
            return new Car(2L, "Audi", "A4", 2025, 2000, 25000, "SOLD");
        }
        return null;
    }

    @DeleteMapping("/{id}")//Удаление машины по ID
    public String deleteCar(@PathVariable Long id) {
// Ищем машину с таким ID в списке
        boolean removed = allCars.removeIf(car -> car.getId().equals(id));

        if (removed) {
            return "Car with ID " + id + " deleted successfully";
        } else {
            return "Car with ID " + id + " not found";
        }
    }
}