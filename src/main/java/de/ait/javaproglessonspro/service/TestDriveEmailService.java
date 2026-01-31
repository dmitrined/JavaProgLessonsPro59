package de.ait.javaproglessonspro.service;

import de.ait.javaproglessonspro.dto.TestDriveConfirmationEmailRequest;
import de.ait.javaproglessonspro.model.Car;
import de.ait.javaproglessonspro.repository.CarRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;

@Service
public class TestDriveEmailService {

    private static final Logger log = LoggerFactory.getLogger(TestDriveEmailService.class);

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;
    private final CarRepository carRepository;

    public TestDriveEmailService(JavaMailSender javaMailSender, TemplateEngine templateEngine, CarRepository carRepository) {
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
        this.carRepository = carRepository;
    }

    @Value("${app.mail.from}")
    private String from;

    @Value("${app.public.base-url}")
    private String baseUrl;

    @Value("${app.dealership.name}")
    private String dealershipName;

    public void sendConfirmation(TestDriveConfirmationEmailRequest request) {
        log.info("Preparing confirmation email for client: {}, carId: {}", request.getClientEmail(), request.getCarId());
        sendEmail(request, "test-drive-confirmation", "Test Drive Confirmation");
    }

    public void sendReminder(TestDriveConfirmationEmailRequest request) {
        log.info("Preparing reminder email for client: {}, carId: {}", request.getClientEmail(), request.getCarId());
        sendEmail(request, "test-drive-reminder", "Test Drive Reminder");
    }

    private void sendEmail(TestDriveConfirmationEmailRequest request, String templateName, String subject) {
        Car car = carRepository.findById(request.getCarId())
                .orElseThrow(() -> new IllegalArgumentException("Car with id " + request.getCarId() + " not found"));

        String cancelUrl = baseUrl + "/api/test-drive/cancel?carId=" + request.getCarId() + "&email=" + request.getClientEmail();

        Context context = new Context();
        context.setVariable("clientName", request.getClientName());
        context.setVariable("carBrand", car.getBrand());
        context.setVariable("carModel", car.getModel());
        context.setVariable("productionYear", car.getProductionYear());
        context.setVariable("color", car.getColor());
        context.setVariable("testDriveDateTime", request.getTestDriveDateTime());
        context.setVariable("dealerAddress", request.getDealerAddress());
        context.setVariable("dealerPhone", request.getDealerPhone());
        context.setVariable("cancelUrl", cancelUrl);
        context.setVariable("dealershipName", dealershipName);

        String html = templateEngine.process(templateName, context);

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,
                    MimeMessageHelper.MULTIPART_MODE_RELATED,
                    StandardCharsets.UTF_8.name());

            helper.setFrom(from);
            helper.setTo(request.getClientEmail());
            helper.setSubject(subject);
            helper.setText(html, true);

            javaMailSender.send(mimeMessage);
            log.info("Email '{}' successfully sent to {}. CarId: {}, DateTime: {}", 
                    subject, request.getClientEmail(), request.getCarId(), request.getTestDriveDateTime());
        } catch (MessagingException e) {
            log.error("Failed to send email '{}' to {}. CarId: {}", subject, request.getClientEmail(), request.getCarId(), e);
            throw new RuntimeException("Email sending failed", e);
        } catch (Exception e) {
            log.error("Unexpected error while sending email '{}' to {}", subject, request.getClientEmail(), e);
            throw e;
        }
    }
}
