package de.bsi.webflux.oauth;

import javax.annotation.PostConstruct;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.oauth2.client.AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.InMemoryReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.Setter;
import reactor.core.publisher.Flux;

@Service
@ConfigurationProperties(prefix = "geo-api.oauth2")
@Setter
public class GeoApiClient {

	private String tokenUri;
	private String clientId;
	private String clientSecret;
	private String serviceBaseUrl;
	
	private WebClient client;
	
	@PostConstruct
	private void initClient() {
		String regId = "any_id_to_identify_oauth_registration";
		var repo = createRegistrationRepo(regId);
		this.client = WebClient.builder()
				.baseUrl(serviceBaseUrl)
				.filter(createOauthFilter(regId, repo))
				.build();
	}
	
	private ReactiveClientRegistrationRepository createRegistrationRepo(String regId) {
		var registration = ClientRegistration
				.withRegistrationId(regId)
				.tokenUri(tokenUri)
				.clientId(clientId)
				.clientSecret(clientSecret)
				.authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
				.build();
		return new InMemoryReactiveClientRegistrationRepository(registration);
	}
	
	private ExchangeFilterFunction createOauthFilter(
			String regId, ReactiveClientRegistrationRepository repo) {
		var manager = new AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(
				repo, new InMemoryReactiveOAuth2AuthorizedClientService(repo));
		var oauthFilter = new ServerOAuth2AuthorizedClientExchangeFilterFunction(manager);
		oauthFilter.setDefaultClientRegistrationId(regId);
		return oauthFilter;
	}
	
	public Flux<GeographicAddress> getRealAddresses(
			String street, String postcode, String city) {
		return client.get().uri(builder -> 
				builder.path("/v2/geographicAddress")
				.queryParam(".fullText", postcode + " " + city + " " + street)
				.queryParam("limit", 5)
				.queryParam(".searchtype", "exact")
				.build())
			.retrieve().bodyToFlux(GeographicAddress.class);
	}
	
}
