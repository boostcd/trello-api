package com.estafet.openshift.boost.console.api.trello.dao;

import com.estafet.openshift.boost.console.api.trello.model.Card;
import com.estafet.openshift.boost.messages.model.FeatureMessage;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.ws.rs.core.MultivaluedMap;

@Repository
public class TrelloDAO {

    Logger logger = LoggerFactory.getLogger(TrelloDAO.class);

    public FeatureMessage getTrelloCardDetails(String url, String commitId) {

        Client client = Client.create();
        WebResource webResource =client.resource("https://trello.com/c/KqKTMPzR.json?");
        MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
        queryParams.add("key", "09a45b6ad328484d8ed4c130e99494d4");
        queryParams.add("token", "445bae1e72caf59a8a78fe00c5ce36852a33a2b44a6f4d42e916804059be89ec");
        ClientResponse response = webResource.queryParams(queryParams).get(ClientResponse.class);

        int statusCode = 0;
        if(response!=null){
            statusCode = response.getStatus();
        } else {
            logger.error("no any response");
            return null;
        }

        if (statusCode == 401) {
            logger.error("unauthorized card permission requested");
        }
        Card card =null;
        if (statusCode == 200){
            card = Card.fromJSON(response.getEntity(String.class));
            String cardId = card.getId();

            String cardStatus = getStatus(cardId);
            card.setStatus(cardStatus);
        }

        return mapping(card, commitId);

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
        queryParams.add("key", "09a45b6ad328484d8ed4c130e99494d4");
        queryParams.add("token", "445bae1e72caf59a8a78fe00c5ce36852a33a2b44a6f4d42e916804059be89ec");
        ClientResponse response = webResource.queryParams(queryParams).get(ClientResponse.class);

        JSONObject jsonObj = new JSONObject(response.getEntity(String.class));
        String status = jsonObj.getString("name");

        System.out.println("status = " + status);

        return status;

    }

    public FeatureMessage mapping(Card card, String commitId) {
        if(card == null){
            return null;
        }

        return FeatureMessage.builder()
                .setCommitId(commitId)
                .setTitle(card.getTitle())
                .setDescription(card.getDescription())
//                .setStatus(card.getStatus())
                .setLastUpdated(card.getLastUpdated())
                .build();

    }
}
