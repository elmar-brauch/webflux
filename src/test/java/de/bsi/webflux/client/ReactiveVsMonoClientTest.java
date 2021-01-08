package de.bsi.webflux.client;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.TimeUnit;
import java.util.function.*;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class ReactiveVsMonoClientTest {
	
	@Value("http://localhost:${local.server.port}/mono")
	private String url2Mono;
	@Value("http://localhost:${local.server.port}/classic")
	private String url2String;
	
	private WebClient reactiveClient;
	private RestTemplate classicClient;
	
	private static final int NUMBER_OF_CALLS = 20;
	private static final String TEST_BODY = "ABCDEFGHIJ0123456789ABCDEFGHIJ0123456789ABCDEFGHIJ0123456789"; 
	
	private int openCalls = 0;
	
	@BeforeEach
	void setup() {
		reactiveClient = WebClient.create(url2Mono);
		classicClient = new RestTemplate();
	}
	
	@Order(1)
	@Test
	void classicGetMono() {
		long ms = meassureTimeInMsForConsumer(id -> {
			String responseBody = classicClient.getForEntity(url2Mono, String.class).getBody();
			System.out.println(id + ". received String of size: " + responseBody.length());
		});
		System.out.println("Classic GET to reactive controller calls took ms: " + ms);
	}
	
	@Order(2)
	@Test
	void reactiveGetMono() {
		long ms = meassureTimeInMsForConsumer(id -> {
			openCalls++;
			reactiveClient.get().retrieve().bodyToMono(String.class).subscribe(responseBody -> {
				System.out.println(id + ". received String of size: " + responseBody.length());
				openCalls--;
			});
		});
		System.out.println("Reactive GET to reactive controller took ms: " + ms);
	}
	
	@Order(3)
	@Test
	void classicPost2String() {
		long ms = meassureTimeInMsForConsumer(id -> {
			var response = classicClient.postForEntity(url2String, new HttpEntity<String>(TEST_BODY), String.class);
			assertEquals(TEST_BODY + "_RESPONSE", response.getBody());
		});
		System.out.println("Classic POST to classic controller took ms: " + ms);
	}
	
	@Order(4)
	@Test
	void classicPost2Mono() {
		long ms = meassureTimeInMsForConsumer(id -> {
			var response = classicClient.postForEntity(url2Mono, new HttpEntity<String>(TEST_BODY), String.class);
			assertEquals(TEST_BODY + "_RESPONSE", response.getBody());
		});
		System.out.println("Classic POST to reactive controller took ms: " + ms);
	}
	
	@Order(5)
	@Test
	void reactivePost2Mono() {
		long ms = meassureTimeInMsForConsumer(id -> {
			openCalls++;
			reactiveClient.post().bodyValue(TEST_BODY).retrieve().toEntity(String.class).subscribe(response -> {
				assertEquals(TEST_BODY + "_RESPONSE", response.getBody());
				openCalls--;
			});
		});
		System.out.println("Reactive POST to reactive controller took ms: " + ms);
	}
	
	private long meassureTimeInMsForConsumer(Consumer<Integer> consumer) {
		long before = System.currentTimeMillis();
		for (int i = 1; i <= NUMBER_OF_CALLS; i++) {
			final int requestId = i;
			consumer.accept(requestId);
		}
		Awaitility.await().timeout(1, TimeUnit.MINUTES).until(() -> openCalls <= 0);
		long after = System.currentTimeMillis();
		return after - before;
	}

}