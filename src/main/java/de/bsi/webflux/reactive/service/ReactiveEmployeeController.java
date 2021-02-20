package de.bsi.webflux.reactive.service;

import java.time.*;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.awaitility.Awaitility;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import de.bsi.webflux.*;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.*;

@Slf4j
@RestController
public class ReactiveEmployeeController {
	
	@ResponseStatus(HttpStatus.OK)
	@GetMapping(Constants.URL_PATH_MONO)
	public Mono<Employee> receiveMonoDelayed() {
		return Mono.fromSupplier(() -> randomEmployee(2))
				.doOnSubscribe(employee -> log.info("Mono subscribed."))
				.doOnSuccess(employee -> log.info("Mono success"));
	}
	
	@ResponseStatus(HttpStatus.OK)
	@GetMapping(path = Constants.URL_PATH_FLUX, produces = MediaType.APPLICATION_NDJSON_VALUE)
	public Flux<Employee> receiveFlux() {
		Supplier<Employee> supplier = () -> randomEmployee(2);
		return Flux.fromStream(Stream.generate(supplier))
				.doOnNext(employee -> log.info("Server generates: " + employee));
	}
	
	private Employee randomEmployee(long sleepInSeconds) {
		Awaitility.await().pollDelay(sleepInSeconds, TimeUnit.SECONDS).until(() -> true);
		var result = new Employee();
		UUID randomId = UUID.randomUUID();
		result.setEmpNo(randomId.toString());
		result.setId(randomId.getMostSignificantBits() + "");
		result.setFullName(Constants.EMPLOYEE_NAME);
		result.setHireDate(Instant.now());
		return result;
	}
	
}