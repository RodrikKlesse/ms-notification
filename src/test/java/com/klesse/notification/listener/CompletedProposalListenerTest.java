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
@DisplayName("CompletedProposalListener Unit Tests")
class CompletedProposalListenerTest {

    @Mock
    private NotificationSNSService notificationSNSService;

    @InjectMocks
    private CompletedProposalListener completedProposalListener;

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
    @DisplayName("Should send approved message when proposal is approved")
    void shouldSendApprovedMessageWhenProposalApproved() {
        // Given
        proposal.setStatus(true);

        // When
        completedProposalListener.completedProposal(proposal);

        // Then
        verify(notificationSNSService, times(1))
                .notifier(eq(users.getPhoneNumber()), anyString());
    }

    @Test
    @DisplayName("Should send denied message with description when proposal is denied with description")
    void shouldSendDeniedMessageWithDescription() {
        // Given
        proposal.setStatus(false);
        proposal.setDescription("Low score");

        // When
        completedProposalListener.completedProposal(proposal);

        // Then
        verify(notificationSNSService, times(1))
                .notifier(eq(users.getPhoneNumber()), anyString());
    }

    @Test
    @DisplayName("Should send denied message without description when proposal is denied")
    void shouldSendDeniedMessageWithoutDescription() {
        // Given
        proposal.setStatus(false);
        proposal.setDescription(null);

        // When
        completedProposalListener.completedProposal(proposal);

        // Then
        verify(notificationSNSService, times(1))
                .notifier(eq(users.getPhoneNumber()), anyString());
    }
}



