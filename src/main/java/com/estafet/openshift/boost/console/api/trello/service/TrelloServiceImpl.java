package com.estafet.openshift.boost.console.api.trello.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.estafet.openshift.boost.console.api.trello.dao.TrelloDAO;
import com.estafet.openshift.boost.messages.features.CommitMessage;
@Service
public class TrelloServiceImpl implements TrelloService {

    private TrelloDAO trelloDAO;

    @Override
    public void getTrelloCardDetails(String url, CommitMessage commitMessage) {
        trelloDAO.getTrelloCardDetails(url,commitMessage);
    }

    @Autowired
    public void setTrelloDAO(TrelloDAO trelloDAO) {
        this.trelloDAO = trelloDAO;
    }
}
