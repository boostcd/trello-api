package com.estafet.openshift.boost.console.api.trello.controller;

import com.estafet.openshift.boost.console.api.trello.service.TrelloService;
import com.estafet.openshift.boost.messages.model.FeatureMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.estafet.openshift.boost.commons.lib.model.API;

@RestController
public class TrelloController {

	@Value("${app.version}")
	private String appVersion;

	@Autowired
	private TrelloService trelloService;
	
	@GetMapping("/api")
	public API getAPI() {
		return new API(appVersion);
	}

	// for testing purpose
	@GetMapping("/get-card-details")
	public ResponseEntity<FeatureMessage> getTrelloCardDetails(String url, String commitId) {
		return new ResponseEntity<>(trelloService.getTrelloCardDetails(url,commitId), HttpStatus.OK);
	}

}
