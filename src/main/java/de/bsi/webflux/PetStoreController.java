package de.bsi.webflux;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class PetStoreController {
	
	private static final String PETSTORE_URL = "https://petstore.swagger.io/v2/pet/findByStatus?status=available&status=pending&status=sold";
	
	record Pet(String name, long id) {}
	
	// TODO Set proxy: -Dhttps.proxyHost=sia-lb.telekom.de -Dhttps.proxyPort=8080
	@GetMapping("/pet1")
	public Pet findPetBlocking() {
		return firstPetWithRestTemplate();
	}
	
	private Pet firstPetWithRestTemplate() {
		var client = new RestTemplate();
		var response = client.getForEntity(PETSTORE_URL, Pet[].class);
		log.info("WAITED FOR RESPONSE");
		return response.getBody()[0];
	}
	
	// TODO 2nd Get endpoint returning Mono use WebClient
//	@GetMapping("/pet2")
//	public Mono<Pet> findPetReactive() {
//		return findPetWithWebClient();
//	}
//	
//	private Mono<Pet> findPetWithWebClient() {
//		var client = WebClient.builder().clientConnector(proxyForWebClient()).build();
//		var mono = client.get()
//				.uri(PETSTORE_URL)
//				.retrieve()
//				.bodyToMono(Pet[].class)
//				.map(pets -> pets[0]);
//		log.info("NOT WAITING FOR RESPONSE");
//		return mono;
//	}
//	
//	private ReactorClientHttpConnector proxyForWebClient() {
//		var httpClient = HttpClient.create().proxy(proxy -> proxy
//                .type(Proxy.HTTP)
//                .host("sia-lb.telekom.de")
//                .port(8080));
//		return new ReactorClientHttpConnector(httpClient);
//	}
	
}
