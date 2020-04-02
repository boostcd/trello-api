package com.estafet.openshift.boost.console.api.trello.service;

import com.estafet.openshift.boost.console.api.trello.model.Card;
import com.estafet.openshift.boost.messages.features.CommitMessage;

public interface TrelloService {
    void getTrelloCardDetails(String url, CommitMessage commitMessage);
    Card getTrelloCardDetails(String url);
}

