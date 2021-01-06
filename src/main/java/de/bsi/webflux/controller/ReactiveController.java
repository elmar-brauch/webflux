package de.bsi.webflux.controller;

import java.util.concurrent.TimeUnit;

import org.awaitility.Awaitility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import de.bsi.webflux.backend.ReactiveBackendApiClient;
import reactor.core.publisher.Mono;

@RestController
public class ReactiveController {
	
	@Autowired private ReactiveBackendApiClient backendClient;
	
	@ResponseStatus(HttpStatus.OK)
	@GetMapping("/mono")
	public Mono<String> callApiForMono() {
		return backendClient.callApiForMono();
	}
	
	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping("/mono")
	public Mono<String> receiveMono(@RequestBody Mono<String> body) {
		Awaitility.await().pollDelay(1, TimeUnit.SECONDS).until(() -> true);
		return body.map(bodyString -> bodyString + "_RESPONSE");
	}
	
	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping("/classic")
	public String receiveString(@RequestBody String body) {
		Awaitility.await().pollDelay(1, TimeUnit.SECONDS).until(() -> true);
		return body + "_RESPONSE";
	}
	
}
