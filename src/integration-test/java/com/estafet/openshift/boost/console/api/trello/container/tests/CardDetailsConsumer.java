package com.estafet.openshift.boost.console.api.trello.container.tests;

import com.estafet.openshift.boost.commons.lib.jms.TopicConsumer;
import com.estafet.openshift.boost.messages.features.FeatureMessage;

public class CardDetailsConsumer extends TopicConsumer {

    public CardDetailsConsumer() {
        super("feature-topic");
    }

    public FeatureMessage consume() {
        return super.consume(FeatureMessage.class);
    }
}

