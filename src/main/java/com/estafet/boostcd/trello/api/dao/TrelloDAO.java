package com.estafet.boostcd.trello.api.dao;

import javax.ws.rs.core.MultivaluedMap;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.estafet.boostcd.trello.api.jms.CardDetailsProducer;
import com.estafet.boostcd.trello.api.model.Card;
import com.estafet.openshift.boost.messages.features.CommitMessage;
import com.estafet.openshift.boost.messages.features.FeatureMessage;
import com.estafet.openshift.boost.messages.features.FeatureStatus;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

@Repository
public class TrelloDAO {

    @Autowired
    private CardDetailsProducer cardDetailsProducer;

    Logger logger = LoggerFactory.getLogger(TrelloDAO.class);

    public void getTrelloCardDetails(String url, CommitMessage commitMessage) {

    		Card card = getTrelloCardDetails(url);
            FeatureMessage featureMessage = mapping(card, commitMessage, url);
            cardDetailsProducer.sendMessage(featureMessage);
        
    }

    public Card getTrelloCardDetails(String url) {
        Client client = Client.create();
        WebResource webResource =client.resource(url);
        MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
        queryParams.add("key", getTrelloApiKey());
        queryParams.add("token", getTrelloAccessToken());
        ClientResponse response = webResource.queryParams(queryParams).get(ClientResponse.class);
        Card card = new Card();
        int statusCode;
        if(response!=null){
            statusCode = response.getStatus();
        } else {
            logger.error("no any Trello's response");
            return null;
        }

        if (statusCode == 401) {
            logger.error("unauthorized card permission requested");
            return null;
        }

        if (statusCode == 200){
            card = Card.fromJSON(response.getEntity(String.class));
            String cardId = card.getId();

            String cardStatus = getStatus(cardId);
            card.setStatus(cardStatus);
        }
        return card;
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

    public FeatureMessage mapping(Card card, CommitMessage commitMessage, String url) {
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
                .setFeatureURL(url.replaceAll("\\.json\\?", ""))
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
