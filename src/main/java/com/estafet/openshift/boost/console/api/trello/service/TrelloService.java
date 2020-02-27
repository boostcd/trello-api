package com.estafet.openshift.boost.console.api.trello.service;

import com.estafet.openshift.boost.messages.model.CommitMessage;

public interface TrelloService {
    void getTrelloCardDetails(String url, CommitMessage commitMessage);
}
