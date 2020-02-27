package com.estafet.openshift.boost.console.api.trello.jms;

import com.estafet.openshift.boost.messages.model.FeatureMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import java.util.UUID;

@Component
public class CardDetailsProducer {

    @Autowired
    private JmsTemplate jmsTemplate;

    public void sendMessage(FeatureMessage message) {
        jmsTemplate.setPubSubDomain(true);
        jmsTemplate.convertAndSend("feature-topic", message.toJSON(), new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws JMSException {
                message.setStringProperty("message.event.interaction.reference", UUID.randomUUID().toString());
                return message;
            }
        });
    }
}
