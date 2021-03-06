package com.estafet.boostcd.trello.api.jms;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.estafet.boostcd.trello.api.service.TrelloService;
import com.estafet.openshift.boost.messages.features.CommitMessage;

import io.opentracing.Tracer;

@Component
public class CommitConsumer {

    public final static String TOPIC = "commit.topic";

    private Tracer tracer;
    private TrelloService trelloService;

    @JmsListener(destination = TOPIC, containerFactory = "myFactory")
    public void onMessage(String message) {
    	if (System.getenv("TASK_MANAGER").equals("trello")) {
            CommitMessage commitMessage = CommitMessage.fromJSON(message);
            String url = getUrl(commitMessage.getMessage());
            if(url != null){
            	try {
                    trelloService.getTrelloCardDetails(url, commitMessage);
                } finally {
                    if (tracer.activeSpan() != null) {
                        tracer.activeSpan().close();
                    }
                }
            }
    	}
    }

    public String getUrl(String message) {
        String regex = "\\b(https?|http)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        String[] splitString = (message.split("\\s+"));
        List<String> matchesList = new ArrayList<>();
        for (String string : splitString) {
            if(string.matches(regex)){
                matchesList.add(string);
            }
        }
        if (matchesList.size()==1){
            String url = matchesList.get(0);
            return url+".json?";
        } else {
            return null;
        }
    }


    @Autowired
    public void setTracer(Tracer tracer) {
        this.tracer = tracer;
    }

    @Autowired
    public void setTrelloService(TrelloService trelloService) {
        this.trelloService = trelloService;
    }

}
