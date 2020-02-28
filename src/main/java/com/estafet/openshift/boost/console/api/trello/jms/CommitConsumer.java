package com.estafet.openshift.boost.console.api.trello.jms;

import com.estafet.openshift.boost.console.api.trello.service.TrelloService;
import com.estafet.openshift.boost.messages.model.CommitMessage;
import com.estafet.openshift.boost.messages.model.UnmatchedCommitMessage;
import io.opentracing.Tracer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class CommitConsumer {

    public final static String TOPIC = "commit-topic";

    private Tracer tracer;
    private TrelloService trelloService;
    private UnmatchedCommitProducer unmatchedCommitProducer;

    @JmsListener(destination = TOPIC, containerFactory = "myFactory")
    public void onMessage(String message) {
        CommitMessage commitMessage = CommitMessage.fromJSON(message);
        String url = getUrl(commitMessage.getMessage());
        if(url==null){
            sendUnmatchedCommit(commitMessage);
        } else {
            try {
                trelloService.getTrelloCardDetails(url, commitMessage);
            } finally {
                if (tracer.activeSpan() != null) {
                    tracer.activeSpan().close();
                }
            }
        }

    }

    // TODO add check that it is just one url within splitString
    public String getUrl(String message) {
        String regex = "\\b(https?|http)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        String[] splitString = (message.split("\\s+"));
        for (String string : splitString) {
            if(string.matches(regex)){
                return string+".json?";
            }
        }
        return null;
    }

    private void sendUnmatchedCommit(CommitMessage commitMessage) {
        unmatchedCommitProducer.sendMessage(UnmatchedCommitMessage.builder()
                .setCommitId(commitMessage.getCommitId())
                .setRepo(commitMessage.getRepo())
                .build());
    }

    @Autowired
    public void setTracer(Tracer tracer) {
        this.tracer = tracer;
    }

    @Autowired
    public void setTrelloService(TrelloService trelloService) {
        this.trelloService = trelloService;
    }

    @Autowired
    public void setUnmatchedCommitProducer(UnmatchedCommitProducer unmatchedCommitProducer) {
        this.unmatchedCommitProducer = unmatchedCommitProducer;
    }
}
