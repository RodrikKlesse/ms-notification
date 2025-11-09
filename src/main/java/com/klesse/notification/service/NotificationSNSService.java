package com.klesse.notification.service;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.AmazonSNSException;
import com.amazonaws.services.sns.model.InvalidParameterException;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.klesse.notification.exception.NotificationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationSNSService {

    @Autowired
    private AmazonSNS amazonSNS;

    @Retryable(value = {AmazonSNSException.class, Exception.class},
    maxAttempts = 3,
    backoff = @Backoff(delay = 1000, multiplier = 2))
    public void notifier(String phoneNumber, String message) {
        try {
           validateInputs(phoneNumber, message);

            PublishRequest publishRequest = new PublishRequest().withMessage(message).withPhoneNumber(phoneNumber);

            PublishResult result = amazonSNS.publish(publishRequest);

            log.info("SMS sent successfully. MessageId: {}, PhoneNumber: {}",
                    result.getMessageId(), maskPhoneNumber(phoneNumber));
        } catch (InvalidParameterException e) {
            log.error("Invalid phone number format: {}", maskPhoneNumber(phoneNumber), e);
            throw new NotificationException("Invalid phone number format", e);
        } catch (AmazonSNSException e) {
            log.error("AWS SNS error sending SMS to: {}", maskPhoneNumber(phoneNumber), e);
            throw new NotificationException("Failed to send SMS via AWS SNS", e);
        } catch (IllegalArgumentException e) {
            log.error("Validation error: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error sending SMS", e);
            throw new NotificationException("Unexpected error sending SMS", e);
        }

    }

    private void validateInputs(String phoneNumber, String message) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone number cannot be null or empty");
        }
        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("Message cannot be null or empty");
        }

        if (!phoneNumber.matches("^\\+?[1-9]\\d{1,14}$")) {
            throw new IllegalArgumentException("Invalid phone number format");
        }
    }

    private String maskPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() < 4) {
            return "***";
        }
        return phoneNumber.substring(0, 2) + "***" + phoneNumber.substring(phoneNumber.length() - 2);
    }
}
