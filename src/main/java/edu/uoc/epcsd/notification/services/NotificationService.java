package edu.uoc.epcsd.notification.services;

import edu.uoc.epcsd.notification.kafka.ProductMessage;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import java.util.List;
import java.time.LocalDate;

@Log4j2
@Component
public class NotificationService {

    @Value("${userService.getUsersToAlert.url}")
    private String userServiceUrl;

    public final RestTemplate restTemplate;

    public NotificationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void notifyProductAvailable(ProductMessage productMessage) {
        // TODO: Use RestTemplate with the above userServiceUrl to query the User microservice in order to get the users that have an alert for the specified product (the date specified in the parameter may be the actual date: LocalDate.now()).
        //  Then simulate the email notification for the alerted users by logging a line with INFO level for each user saying "Sending an email to user " + the user fullName
        try {
            //Create a request to the User Microservice to get users to alert for the specified product and date.
            ResponseEntity<List<Long>> response = restTemplate.exchange(
                    userServiceUrl + "?productId={productId}&date={date}",
                    org.springframework.http.HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Long>>() {},
                    productMessage.getProductId(),
                    LocalDate.now());

            List<Long> alertedUserIds = response.getBody();

            if (alertedUserIds != null && !alertedUserIds.isEmpty()) {
                for (Long userId : alertedUserIds) {
                    log.info("Sending an email to user " + userId);
                    sendEmailToUser(userId);
                }
            } else {
                log.info("No users to alert for product " + productMessage.getProductId());
            }
        } catch (Exception e) {
            log.error("Error getting users to alert for product " + productMessage.getProductId(), e);
        }
    }
    // Simulate sending an email and print the action.
    private void sendEmailToUser(Long userId) {
        String emailContent = "Your product is available!";
        System.out.println("Email sent to user " + userId + ": " + emailContent);
    }
}