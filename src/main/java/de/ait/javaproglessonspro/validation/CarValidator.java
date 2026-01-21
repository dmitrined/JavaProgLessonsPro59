package de.ait.javaproglessonspro.validation;

import de.ait.javaproglessonspro.model.Car;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CarValidator {

    private static final  Logger log = LoggerFactory.getLogger(CarValidator.class);

    public boolean isValid(Car car) {
        if (car == null) {
            log.error("Car object is null");
            return false;
        }

        boolean isValid = true;
        StringBuilder errors = new StringBuilder();

        if (car.getBrand() == null || car.getBrand().trim().isEmpty()) {
            errors.append("brand is null or empty; ");
            isValid = false;
        }
        if (car.getModel() == null || car.getModel().trim().isEmpty()) {
            errors.append("model is null or empty; ");
            isValid = false;
        }
        if (car.getProductionYear() < 1900) {
            errors.append("productionYear < 1900; ");
            isValid = false;
        }
        if (car.getMileage() < 0) {
            errors.append("mileage < 0; ");
            isValid = false;
        }
        if (car.getPrice() < 1) {
            errors.append("price < 1; ");
            isValid = false;
        }
        if (car.getStatus() == null) {
            errors.append("status is null; ");
            isValid = false;
        }
        if (car.getColor() == null || car.getColor().trim().isEmpty()) {
            errors.append("color is null or empty; ");
            isValid = false;
        }
        if (car.getHorsepower() < 1) {
            errors.append("horsepower < 1; ");
            isValid = false;
        }
        if (car.getFuelType() == null) {
            errors.append("fuelType is null; ");
            isValid = false;
        }
        if (car.getTransmission() == null) {
            errors.append("transmission is null; ");
            isValid = false;
        }

        if (!isValid) {
            log.warn("Invalid car object received: {}. Data: {}", errors.toString(), car);
        }

        return isValid;
    }
}
