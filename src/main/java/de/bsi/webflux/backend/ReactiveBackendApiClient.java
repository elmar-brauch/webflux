package de.bsi.webflux.backend;

import java.util.*;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class ReactiveBackendApiClient implements BackendApiClient {

	private WebClient client;
	private List<Integer> queue;
	
	@PostConstruct
	private void init() {
		this.client = WebClient.create(API_HOST);
		this.queue = new ArrayList<>();
	}
	
	@Override
	public void callApi(int requestId) {
		queue.add(Integer.valueOf(requestId));
		Mono<String> responseMono = callApiForMono();
		responseMono.subscribe(responseString -> {
			log.debug("{}. response to WebClient: {}", requestId, responseString.substring(0, 100));
			queue.remove(Integer.valueOf(requestId));
		});
	}
	
	public Mono<String> callApiForMono() {
		return client.get().uri(API_URI).retrieve().bodyToMono(String.class);
	}
	
	@Override
	public boolean isQueueEmpty() {
		return queue.isEmpty();
	}
}
