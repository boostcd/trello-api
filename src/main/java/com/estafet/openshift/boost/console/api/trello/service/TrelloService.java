package com.estafet.openshift.boost.console.api.trello.service;

import com.estafet.openshift.boost.console.api.trello.model.Card;
import com.estafet.openshift.boost.messages.model.FeatureMessage;

public interface TrelloService {
    FeatureMessage getTrelloCardDetails(String url, String commitId);
}
