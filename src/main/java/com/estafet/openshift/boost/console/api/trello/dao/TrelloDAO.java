package com.estafet.openshift.boost.console.api.trello.dao;

import com.estafet.openshift.boost.console.api.trello.jms.CardDetailsProducer;
import com.estafet.openshift.boost.console.api.trello.model.Card;
import com.estafet.openshift.boost.messages.model.CommitMessage;
import com.estafet.openshift.boost.messages.model.FeatureMessage;
import com.estafet.openshift.boost.messages.model.FeatureStatus;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.ws.rs.core.MultivaluedMap;

@Repository
public class TrelloDAO {

    @Autowired
    private CardDetailsProducer cardDetailsProducer;

    Logger logger = LoggerFactory.getLogger(TrelloDAO.class);

    public void getTrelloCardDetails(String url, CommitMessage commitMessage) {

        Client client = Client.create();
        WebResource webResource =client.resource(url);
        MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
        queryParams.add("key", getTrelloApiKey());
        queryParams.add("token", getTrelloAccessToken());
        ClientResponse response = webResource.queryParams(queryParams).get(ClientResponse.class);

        int statusCode;
        if(response!=null){
            statusCode = response.getStatus();
        } else {
            logger.error("no any Trello's response");
            return;
        }

        if (statusCode == 401) {
            logger.error("unauthorized card permission requested");
            return;
        }

        if (statusCode == 200){
            Card card = Card.fromJSON(response.getEntity(String.class));
            String cardId = card.getId();

            String cardStatus = getStatus(cardId);
            card.setStatus(cardStatus);

            FeatureMessage featureMessage = mapping(card, commitMessage);
            cardDetailsProducer.sendMessage(featureMessage);
        }

        // status - name -- 				https://api.trello.com/1/cards/5e4e6b708116903d69587195/list?key=09a45b6ad328484d8ed4c130e99494d4

        // lastUpdate - dateLastActivity -- https://trello.com/c/uVJytkMx.json?key=09a45b6ad328484d8ed4c130e99494d4
        // commitId - from input massage
        //description - desc -- 			https://trello.com/c/uVJytkMx.json?key=09a45b6ad328484d8ed4c130e99494d4
        // title - name -- 					https://trello.com/c/uVJytkMx.json?key=09a45b6ad328484d8ed4c130e99494d4

    }

    public String getStatus(String cardId) {

        Client client = Client.create();
        WebResource webResource =client.resource("https://api.trello.com/1/cards/" + cardId + "/list?");
        MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
        queryParams.add("key", getTrelloApiKey());
        queryParams.add("token", getTrelloAccessToken());
        ClientResponse response = webResource.queryParams(queryParams).get(ClientResponse.class);

        JSONObject jsonObj = new JSONObject(response.getEntity(String.class));
        return jsonObj.getString("name");

    }

    public FeatureMessage mapping(Card card, CommitMessage commitMessage) {
        if(card == null){
            return null;
        }

        FeatureStatus status = getFeatureStatus(card);

        return FeatureMessage.builder()
                .setFeatureId(card.getId())
                .setCommitId(commitMessage.getCommitId())
                .setRepo(commitMessage.getRepo())
                .setTitle(card.getTitle())
                .setDescription(card.getDescription())
                .setStatus(status)
                .setLastUpdated(card.getLastUpdated())
                .build();

    }

    private FeatureStatus getFeatureStatus(Card card) {
        FeatureStatus status = null;

        if(card.getStatus().equals(FeatureStatus.DONE.getValue())){
             status=FeatureStatus.DONE;
         }
        if(card.getStatus().equals(FeatureStatus.IN_PROGRESS.getValue())){
            status=FeatureStatus.IN_PROGRESS;
        }

        if(card.getStatus().equals(FeatureStatus.NOT_STARTED.getValue())){
            status=FeatureStatus.NOT_STARTED;
        }

        if(card.getStatus().equals(FeatureStatus.BLOCKED.getValue())){
            status=FeatureStatus.BLOCKED;
        }
        return status;
    }

    private String getTrelloApiKey() {
        return System.getenv("TRELLO_API_KEY");
    }

    private String getTrelloAccessToken() {
        return System.getenv("TRELLO_ACCESS_TOKEN");
    }
}
