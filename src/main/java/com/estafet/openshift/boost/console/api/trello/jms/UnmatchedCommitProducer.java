package com.estafet.openshift.boost.console.api.trello.jms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import com.estafet.openshift.boost.messages.features.UnmatchedCommitMessage;

@Component
public class UnmatchedCommitProducer {

    @Autowired
    private JmsTemplate jmsTemplate;

    public void sendMessage(UnmatchedCommitMessage message) {
        jmsTemplate.setPubSubDomain(true);
        jmsTemplate.convertAndSend("unmatched.commit.topic", message.toJSON());
    }
    
}
