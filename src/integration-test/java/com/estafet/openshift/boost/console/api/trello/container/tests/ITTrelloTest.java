package com.estafet.openshift.boost.console.api.trello.container.tests;

import com.estafet.openshift.boost.commons.lib.properties.PropertyUtils;
import com.estafet.openshift.boost.console.api.trello.model.Card;
import com.estafet.openshift.boost.messages.model.CommitMessage;
import com.estafet.openshift.boost.messages.model.FeatureMessage;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import io.restassured.RestAssured;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import javax.ws.rs.core.MultivaluedMap;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class})
public class ITTrelloTest {

    CardDetailsConsumer topic = new CardDetailsConsumer();

    @Before
    public void before() {
        RestAssured.baseURI = PropertyUtils.instance().getProperty("WALLET_MS_SERVICE_URI");
    }

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
