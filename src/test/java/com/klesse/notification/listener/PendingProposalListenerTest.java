package com.klesse.notification.listener;

import com.klesse.notification.domain.Proposal;
import com.klesse.notification.domain.Users;
import com.klesse.notification.service.NotificationSNSService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PendingProposalListener Unit Tests")
class PendingProposalListenerTest {

    @Mock
    private NotificationSNSService notificationSNSService;

    @InjectMocks
    private PendingProposalListener pendingProposalListener;

    private Proposal proposal;
    private Users users;

    @BeforeEach
    void setUp() {
        users = new Users();
        users.setId(1L);
        users.setName("John");
        users.setPhoneNumber("+5511999999999");

        proposal = new Proposal();
        proposal.setId(1L);
        proposal.setUsers(users);
    }

    @Test
    @DisplayName("Should send pending proposal notification")
    void shouldSendPendingProposalNotification() {
        // When
        pendingProposalListener.pendingProposal(proposal);

        // Then
        verify(notificationSNSService, times(1))
                .notifier(eq(users.getPhoneNumber()), anyString());
    }

    @Test
    @DisplayName("Should include user name in notification message")
    void shouldIncludeUserNameInNotification() {
        // Given
        users.setName("Jane Doe");

        // When
        pendingProposalListener.pendingProposal(proposal);

        // Then
        verify(notificationSNSService, times(1))
                .notifier(eq(users.getPhoneNumber()), anyString());
    }
}



