package com.estafet.openshift.boost.console.api.trello.controller;

import com.estafet.openshift.boost.console.api.trello.model.Card;
import com.estafet.openshift.boost.console.api.trello.model.Url;
import com.estafet.openshift.boost.console.api.trello.service.TrelloServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.estafet.openshift.boost.commons.lib.model.API;

@RestController
public class TrelloController {

	@Value("${app.version}")
	private String appVersion;

	@Autowired
	private TrelloServiceImpl trelloService;
	
	@GetMapping("/api")
	public API getAPI() {
		return new API(appVersion);
	}
	
	@GetMapping("/card")
	public Card getTrelloCard(@RequestBody Url url ) {
		System.out.println("URL: " + url.getUrl());
		return trelloService.getTrelloCardDetails(url.getUrl());
	}
}
