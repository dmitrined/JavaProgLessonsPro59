package de.ait.javaproglessonspro.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CarOfferEmailRequest {

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
    }

    public Integer getOfferPrice() {
        return offerPrice;
    }

    public void setOfferPrice(Integer offerPrice) {
        this.offerPrice = offerPrice;
    }

    @NotBlank(message = "Email is mandatory")
    private String email;

    @NotBlank(message = "Client name is mandatory")
    private String clientName;

    @NotNull(message = "Car ID is mandatory")
    private Long carId;

    @Positive(message = "Offer price must be positive")
    private Integer offerPrice;

}