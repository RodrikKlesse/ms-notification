package com.klesse.notification.listener;

import com.klesse.notification.constants.Message;
import com.klesse.notification.domain.Proposal;
import com.klesse.notification.service.NotificationSNSService;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Objects;

@AllArgsConstructor
@Component
public class CompletedProposalListener {

    private NotificationSNSService notificationSNSService;

    @RabbitListener(queues = "${rabbitmq.queue.completed.proposal.notification}")
    public void completedProposal(Proposal proposal) {
        String name = proposal.getUsers().getName();
        String message;

        if(proposal.getStatus()) {
             message = String.format(Message.COMPLETED_PROPOSAL_ANALYSIS_APPROVED, proposal.getUsers().getName());
        } else if (Objects.nonNull(proposal.getDescription())){
            message = String.format(Message.COMPLETED_PROPOSAL_ANALYSIS_DENIED, proposal.getUsers().getName(), proposal.getDescription());
        } else {
            message = String.format(Message.PROPOSAL_ANALYSIS_DENIED, name);
        }

        notificationSNSService.notifier(proposal.getUsers().getPhoneNumber(), message);
    }
}
