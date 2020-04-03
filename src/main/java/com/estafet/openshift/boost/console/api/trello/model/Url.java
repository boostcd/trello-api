package com.estafet.openshift.boost.console.api.trello.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Url {


    @JsonProperty("url")
    private String url;

 
    public Url(){

    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public static Url fromJSON(String message) {
        try {
            return (new ObjectMapper()).readValue(message, Url.class);
        } catch (IOException var2) {
            throw new RuntimeException(var2);
        }
    }


}
