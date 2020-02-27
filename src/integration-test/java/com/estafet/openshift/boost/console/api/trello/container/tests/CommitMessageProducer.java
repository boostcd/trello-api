package com.estafet.openshift.boost.console.api.trello.container.tests;

import com.estafet.openshift.boost.commons.lib.jms.TopicProducer;

public class CommitMessageProducer extends TopicProducer {

    public CommitMessageProducer() {
        super("commit-topic");
    }

    public static void send(String message) {
        new CommitMessageProducer().sendMessage(message);
    }
}
