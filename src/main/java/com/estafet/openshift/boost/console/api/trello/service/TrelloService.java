package com.estafet.openshift.boost.console.api.trello.service;

import com.estafet.openshift.boost.messages.features.CommitMessage;

public interface TrelloService {
    void getTrelloCardDetails(String url, CommitMessage commitMessage);
}
