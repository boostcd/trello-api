package com.estafet.boostcd.trello.api.jms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import com.estafet.openshift.boost.messages.features.FeatureMessage;

@Component
public class CardDetailsProducer {

    @Autowired
    private JmsTemplate jmsTemplate;

    public void sendMessage(FeatureMessage message) {
        jmsTemplate.setPubSubDomain(true);
        jmsTemplate.convertAndSend("feature.topic", message.toJSON());
    }
}
