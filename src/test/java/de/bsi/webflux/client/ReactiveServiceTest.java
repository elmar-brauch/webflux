package de.bsi.webflux.client;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.TimeUnit;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.web.reactive.function.client.WebClient;

import de.bsi.webflux.Constants;
import de.bsi.webflux.Employee;
import lombok.extern.slf4j.Slf4j;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

@Slf4j
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
class ReactiveServiceTest {
	
	private WebClient reactiveClient;
	private int openCalls = 0;
	
	@BeforeEach
	void setup() {
		reactiveClient = WebClient.builder().baseUrl(Constants.URL_BASE).build();
	}
	
	@Test
	void readMono() {
		openCalls++;
		reactiveClient.get().uri(Constants.URL_PATH_MONO).retrieve().bodyToMono(Employee.class).subscribe(employee -> {
			assertEquals(Constants.EMPLOYEE_NAME, employee.getFullName());
			log.info("Response received: " + employee);
			openCalls--;
		});
		log.info("Request send. Data published in Mono will be handled in another thread.");
		Awaitility.await().timeout(10, TimeUnit.SECONDS).until(() -> openCalls <= 0);
	}
	
	@Test
	void readFlux() {
		openCalls = 5;
		Flux<Employee> employees = reactiveClient.get().uri(Constants.URL_PATH_FLUX)
				.retrieve().bodyToFlux(Employee.class);
		Disposable employeesDisposable = employees.subscribe(employee -> {
			assertEquals(Constants.EMPLOYEE_NAME, employee.getFullName());
			log.info("Parts of response received: " + employee);
			openCalls--;
		});
		log.info("Request send. Flux responses will be handled in other threads.");
		Awaitility.await().timeout(20, TimeUnit.SECONDS).until(() -> openCalls <= 0);
		employeesDisposable.dispose();
	}
	
}