package de.ait.javaproglessonspro.controllers;

import de.ait.javaproglessonspro.dto.TestDriveConfirmationEmailRequest;
import de.ait.javaproglessonspro.service.TestDriveEmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email/test-drive")
public class TestDriveEmailController {

    private static final Logger log = LoggerFactory.getLogger(TestDriveEmailController.class);

    private final TestDriveEmailService testDriveEmailService;

    public TestDriveEmailController(TestDriveEmailService testDriveEmailService) {
        this.testDriveEmailService = testDriveEmailService;
    }

    @PostMapping("/confirmation")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void sendConfirmation(@RequestBody @Valid TestDriveConfirmationEmailRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.warn("Validation failed for test drive confirmation request: {}", bindingResult.getAllErrors());
        }
        log.info("Request for test drive confirmation received: {}", request);
        testDriveEmailService.sendConfirmation(request);
    }

    @PostMapping("/reminder")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void sendReminder(@RequestBody @Valid TestDriveConfirmationEmailRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.warn("Validation failed for test drive reminder request: {}", bindingResult.getAllErrors());
        }
        log.info("Request for test drive reminder received: {}", request);
        testDriveEmailService.sendReminder(request);
    }
}
