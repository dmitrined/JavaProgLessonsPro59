package de.ait.javaproglessonspro.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TestDriveConfirmationEmailRequest {

    public String getClientEmail() {
        return clientEmail;
    }

    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
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

    public String getTestDriveDateTime() {
        return testDriveDateTime;
    }

    public void setTestDriveDateTime(String testDriveDateTime) {
        this.testDriveDateTime = testDriveDateTime;
    }

    public String getDealerAddress() {
        return dealerAddress;
    }

    public void setDealerAddress(String dealerAddress) {
        this.dealerAddress = dealerAddress;
    }

    public String getDealerPhone() {
        return dealerPhone;
    }

    public void setDealerPhone(String dealerPhone) {
        this.dealerPhone = dealerPhone;
    }

    @NotBlank(message = "Client email is mandatory")
    @Email(message = "Invalid email format")
    private String clientEmail;

    @NotBlank(message = "Client name is mandatory")
    private String clientName;

    @NotNull(message = "Car ID is mandatory")
    private Long carId;

    @NotBlank(message = "Test drive date and time is mandatory")
    private String testDriveDateTime;

    @NotBlank(message = "Dealer address is mandatory")
    private String dealerAddress;

    @NotBlank(message = "Dealer phone is mandatory")
    private String dealerPhone;

}
