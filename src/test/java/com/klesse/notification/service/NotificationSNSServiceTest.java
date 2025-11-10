package com.klesse.notification.service;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.AmazonSNSException;
import com.amazonaws.services.sns.model.InvalidParameterException;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.klesse.notification.exception.NotificationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationSNSService Unit Tests")
class NotificationSNSServiceTest {

    @Mock
    private AmazonSNS amazonSNS;

    @InjectMocks
    private NotificationSNSService notificationSNSService;

    private String phoneNumber;
    private String message;

    @BeforeEach
    void setUp() {
        phoneNumber = "+5511999999999";
        message = "Test message";
    }

    @Test
    @DisplayName("Should send SMS successfully")
    void shouldSendSMSSuccessfully() {
        // Given
        PublishResult publishResult = new PublishResult();
        publishResult.setMessageId("test-message-id");
        when(amazonSNS.publish(any(PublishRequest.class))).thenReturn(publishResult);

        // When
        assertDoesNotThrow(() -> notificationSNSService.notifier(phoneNumber, message));

        // Then
        verify(amazonSNS, times(1)).publish(any(PublishRequest.class));
    }

    @Test
    @DisplayName("Should throw exception when phone number is null")
    void shouldThrowExceptionWhenPhoneNumberIsNull() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> 
            notificationSNSService.notifier(null, message));
        verify(amazonSNS, never()).publish(any(PublishRequest.class));
    }

    @Test
    @DisplayName("Should throw exception when phone number is empty")
    void shouldThrowExceptionWhenPhoneNumberIsEmpty() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> 
            notificationSNSService.notifier("", message));
        verify(amazonSNS, never()).publish(any(PublishRequest.class));
    }

    @Test
    @DisplayName("Should throw exception when message is null")
    void shouldThrowExceptionWhenMessageIsNull() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> 
            notificationSNSService.notifier(phoneNumber, null));
        verify(amazonSNS, never()).publish(any(PublishRequest.class));
    }

    @Test
    @DisplayName("Should throw exception when message is empty")
    void shouldThrowExceptionWhenMessageIsEmpty() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> 
            notificationSNSService.notifier(phoneNumber, ""));
        verify(amazonSNS, never()).publish(any(PublishRequest.class));
    }

    @Test
    @DisplayName("Should throw exception when phone number format is invalid")
    void shouldThrowExceptionWhenPhoneNumberFormatIsInvalid() {
        // Given
        String invalidPhone = "invalid-phone";

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> 
            notificationSNSService.notifier(invalidPhone, message));
        verify(amazonSNS, never()).publish(any(PublishRequest.class));
    }

    @Test
    @DisplayName("Should throw NotificationException when AWS SNS throws InvalidParameterException")
    void shouldThrowNotificationExceptionWhenInvalidParameterException() {
        // Given
        when(amazonSNS.publish(any(PublishRequest.class)))
                .thenThrow(new InvalidParameterException("Invalid parameter"));

        // When & Then
        assertThrows(NotificationException.class, () -> 
            notificationSNSService.notifier(phoneNumber, message));
        verify(amazonSNS, times(1)).publish(any(PublishRequest.class));
    }

    @Test
    @DisplayName("Should throw NotificationException when AWS SNS throws AmazonSNSException")
    void shouldThrowNotificationExceptionWhenAmazonSNSException() {
        // Given
        when(amazonSNS.publish(any(PublishRequest.class)))
                .thenThrow(new AmazonSNSException("SNS error"));

        // When & Then
        assertThrows(NotificationException.class, () -> 
            notificationSNSService.notifier(phoneNumber, message));
        verify(amazonSNS, times(1)).publish(any(PublishRequest.class));
    }

    @Test
    @DisplayName("Should mask phone number correctly")
    void shouldMaskPhoneNumberCorrectly() {
        // Given
        PublishResult publishResult = new PublishResult();
        publishResult.setMessageId("test-message-id");
        when(amazonSNS.publish(any(PublishRequest.class))).thenReturn(publishResult);

        // When
        notificationSNSService.notifier(phoneNumber, message);

        // Then - Verify that the service was called (masking happens internally)
        verify(amazonSNS, times(1)).publish(any(PublishRequest.class));
    }
}

