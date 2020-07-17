package com.estafet.boostcd.trello.api.service;

import com.estafet.boostcd.trello.api.model.Card;
import com.estafet.openshift.boost.messages.features.CommitMessage;

public interface TrelloService {
    void getTrelloCardDetails(String url, CommitMessage commitMessage);
    Card getTrelloCardDetails(String url);
}

