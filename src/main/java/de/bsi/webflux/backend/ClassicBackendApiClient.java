package de.bsi.webflux.backend;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ClassicBackendApiClient implements BackendApiClient {
	
	private RestTemplate rest;
	
	@PostConstruct
	private void init() {
		this.rest = new RestTemplate();
	}
	
	@Override
	public void callApi(int requestId) {
		String responseRT = rest.getForEntity(API_HOST + API_URI, String.class).getBody();
		log.debug("{}. response to RestTemplate: {}", requestId, responseRT.substring(0, 100));
	}
}