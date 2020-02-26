package com.estafet.openshift.boost.console.api.trello.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Card {

    private String id;

    @JsonProperty("name")
    private String title;

    @JsonProperty("desc")
    private String description;

    @JsonProperty("dateLastActivity")
    private String lastUpdated;

    private String status;

    public Card(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static Card fromJSON(String message) {
        try {
            return (new ObjectMapper()).readValue(message, Card.class);
        } catch (IOException var2) {
            throw new RuntimeException(var2);
        }
    }

    public String toJSON() {
        try {
            return (new ObjectMapper()).writeValueAsString(this);
        } catch (IOException var2) {
            throw new RuntimeException(var2);
        }
    }
}
