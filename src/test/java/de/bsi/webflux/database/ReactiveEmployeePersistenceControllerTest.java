package de.bsi.webflux.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.concurrent.CountDownLatch;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.web.reactive.function.client.WebClient;

import de.bsi.webflux.Constants;
import lombok.extern.slf4j.Slf4j;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ReactiveEmployeePersistenceControllerTest {
	
	private WebClient client;
	@Autowired private ReactiveEmployeeRepository repo;
	
	@BeforeEach
	void setup() {
		client = WebClient.create(Constants.URL_BASE);
		repo.deleteAll().block();
		repo.insert(new EmployeeDAO(null, "007", "James Bond", 55)).block();
		repo.insert(new EmployeeDAO(null, "001", "Jane Moneypenny", 39)).block();
		assertEquals(2, repo.count().block());
	}

	@Test
	void reactiveDeleteTest() {
		Mono<Void> mono = client.delete().uri(Constants.URL_PATH_EMPLOYEE)
				.retrieve().bodyToMono(Void.class);
		assertEquals(2, repo.count().block());
		final Disposable disposable = mono.subscribe();
		log.debug("Delete operation sent to database");
		Awaitility.await().until(() -> disposable.isDisposed());
		assertEquals(0, repo.count().block());
	}
	
	private static String empNos = "";
	
	@Test
	void reactiveReadTest() {
		final Disposable disposable = client.get()
				.uri(Constants.URL_PATH_EMPLOYEE + "?minAge=40")
				.retrieve().bodyToFlux(EmployeeDAO.class)
				.subscribe(e -> empNos += e.getEmpNo());
		Awaitility.await().until(() -> disposable.isDisposed());
		assertEquals("007", empNos);
	}
	
	@Test
	void reactiveWriteTest() {
		final Disposable disposable = client.post().uri(Constants.URL_PATH_EMPLOYEE)
				.bodyValue(new EmployeeDAO(null, "008", "Bill", 25))
				.retrieve().bodyToMono(EmployeeDAO.class).subscribe();
		Awaitility.await().until(() -> disposable.isDisposed());
		assertEquals(3, repo.count().block());
	}
	
	// This test is NOT a proper JUnit test, it is demo for reactive database interaction.
	// The test fails sometimes - in case of failure
	// all POST requests have been completed, before the GET request.
	// Success case proves that writing in database happened in reactive style,
	// because main-thread of this test was not blocked.
	@Test
	@Order(1)
	void reactiveWriteAndReadTest() {
		int apiCalls = 250; 
		var counter = new CountDownLatch(apiCalls);
		for (int i = 0; i < apiCalls; i++)
			client.post().uri(Constants.URL_PATH_EMPLOYEE)
					.retrieve().bodyToMono(EmployeeDAO.class)
					.subscribe(e -> {
						assertNotNull(e.getId());
						counter.countDown();
					});
		long numberOfDbEntries = client.get().uri(Constants.URL_PATH_EMPLOYEE)
					.retrieve().bodyToFlux(EmployeeDAO.class).count().block();
		// It is expected, that not all write requests are completed before read request is started.
		assertNotEquals(apiCalls + 2, numberOfDbEntries);
		Awaitility.await().until(() -> counter.getCount() <= 0);
		log.info("Number of employee at time of GET request: " + numberOfDbEntries);
		assertEquals(apiCalls + 2, repo.count().block());
	}

}
