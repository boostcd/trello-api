package com.estafet.openshift.boost.console.api.trello.container.tests;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.MultivaluedMap;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.estafet.openshift.boost.console.api.trello.model.Card;
import com.estafet.openshift.boost.messages.features.CommitMessage;
import com.estafet.openshift.boost.messages.features.FeatureMessage;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/ITTrelloTest-context.xml"})
public class ITTrelloTest {

    CardDetailsConsumer topic = new CardDetailsConsumer();

    @After
    public void after() {
        topic.closeConnection();
    }

    @Test
    public void testConsumeNewAccount() {

        CommitMessage commitMessage = CommitMessage.builder()
                .setRepo("estafet-blockchain-demo-bank-ms")
                .setCommitId("Ira")
                .setMessage("coment https://trello.com/c/ILvY4Wxf     ergergerg").build();

        Card card = getTrelloCardDetails("https://trello.com/c/ILvY4Wxf.json?", commitMessage);

        CommitMessageProducer.send("{\"commitId\":\"Ira\",\"repo\":\"estafet-blockchain-demo-bank-ms\",\"message\": \"coment https://trello.com/c/ILvY4Wxf     ergergerg\"}");

        FeatureMessage message = topic.consume();

        assertEquals(commitMessage.getCommitId(), message.getCommitId());
        assertEquals(commitMessage.getRepo(), message.getRepo());
        assertEquals(card.getDescription(), message.getDescription());
        assertEquals(card.getTitle(), message.getTitle());
        assertEquals(card.getId(), message.getFeatureId());
        assertEquals(card.getLastUpdated(), message.getLastUpdated());
        assertEquals(card.getStatus(), message.getStatus().getValue());

    }

    public Card getTrelloCardDetails(String url, CommitMessage commitMessage) {

        Client client = Client.create();
        WebResource webResource =client.resource(url);
        MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
        queryParams.add("key", "09a45b6ad328484d8ed4c130e99494d4");
        queryParams.add("token", "445bae1e72caf59a8a78fe00c5ce36852a33a2b44a6f4d42e916804059be89ec");
        ClientResponse response = webResource.queryParams(queryParams).get(ClientResponse.class);

        int statusCode;
        if(response!=null){
            statusCode = response.getStatus();
        } else {
            return null;
        }

        if (statusCode == 401) {
            return null;
        }

        Card card = null;
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
        queryParams.add("key", "09a45b6ad328484d8ed4c130e99494d4");
        queryParams.add("token", "445bae1e72caf59a8a78fe00c5ce36852a33a2b44a6f4d42e916804059be89ec");
        ClientResponse response = webResource.queryParams(queryParams).get(ClientResponse.class);

        JSONObject jsonObj = new JSONObject(response.getEntity(String.class));
        return jsonObj.getString("name");

    }
}
