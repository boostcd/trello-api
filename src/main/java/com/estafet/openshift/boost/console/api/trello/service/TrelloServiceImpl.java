package com.estafet.openshift.boost.console.api.trello.service;

import com.estafet.openshift.boost.console.api.trello.dao.TrelloDAO;
import com.estafet.openshift.boost.messages.model.FeatureMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
public class TrelloServiceImpl implements TrelloService {

    private TrelloDAO trelloDAO;

    @Override
    public FeatureMessage getTrelloCardDetails(String url, String commitId) {
        return trelloDAO.getTrelloCardDetails(url,commitId);
    }

    @Autowired
    public void setTrelloDAO(TrelloDAO trelloDAO) {
        this.trelloDAO = trelloDAO;
    }
}
