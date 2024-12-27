package com.klesse.notification.listener;

import com.klesse.notification.constants.Message;
import com.klesse.notification.domain.Proposal;
import com.klesse.notification.service.NotificationSNSService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class PendingProposalListener {

    private NotificationSNSService notificationSNSService;

    @RabbitListener(queues = "${rabbitmq.queue.proposal.notification}")
    public void pendingProposal(Proposal proposal) {
        String message = String.format(Message.PROPOSAL_ANALYSIS, proposal.getUsers().getName());
        notificationSNSService.notifier(message);
    }
}