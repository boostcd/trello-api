package com.estafet.openshift.boost.console.api.trello.jms;

import com.estafet.openshift.boost.messages.model.FeatureMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class CardDetailsProducer {

    @Autowired
    private JmsTemplate jmsTemplate;

    public void sendMessage(FeatureMessage message) {
        jmsTemplate.setPubSubDomain(true);
        jmsTemplate.convertAndSend("feature.topic", message.toJSON());
    }
}
