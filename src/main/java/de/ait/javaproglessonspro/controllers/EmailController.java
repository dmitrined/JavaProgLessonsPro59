package de.ait.javaproglessonspro.controllers;

import de.ait.javaproglessonspro.dto.CarOfferEmailRequest;
import de.ait.javaproglessonspro.service.CarOfferEmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    private static final Logger log = LoggerFactory.getLogger(EmailController.class);

    private final CarOfferEmailService carOfferEmailService;

    public EmailController(CarOfferEmailService carOfferEmailService) {
        this.carOfferEmailService = carOfferEmailService;
    }

    @PostMapping("/car-offer")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void sendCarOfferEmail(@RequestBody @Valid CarOfferEmailRequest carOfferEmailRequest) {
        log.info("Sending car offer email for request: {}", carOfferEmailRequest);
        carOfferEmailService.sendCarOfferEmail(carOfferEmailRequest);
    }
}