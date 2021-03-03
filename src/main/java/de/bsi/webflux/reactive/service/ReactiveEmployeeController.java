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
	
	private static final long WAITING_TIME_IN_SECONDS = 2;
	
	@ResponseStatus(HttpStatus.OK)
	@GetMapping(Constants.URL_PATH_MONO)
	public Mono<Employee> receiveMonoDelayed() {
		Mono<Employee> result = Mono.fromSupplier(() -> generateEmployee(WAITING_TIME_IN_SECONDS))
				.doOnSuccess(employee -> log.info("Mono published: " + employee));
		log.info("Returning Mono.");
		return result;
	}
	
	@ResponseStatus(HttpStatus.OK)
	@GetMapping(path = Constants.URL_PATH_FLUX, produces = MediaType.APPLICATION_NDJSON_VALUE)
	public Flux<Employee> receiveFlux() {
		Supplier<Employee> supplier = () -> generateEmployee(WAITING_TIME_IN_SECONDS);
		return Flux.fromStream(Stream.generate(supplier))
				.doOnNext(employee -> log.info("Flux emits: " + employee));
	}
	
	/**
	 * Method generates Employee.
	 * To simulate long running processes, it waits the given time.
	 * 
	 * @param waitInSeconds before generating {@link Employee}.
	 * @return Generated {@link Employee} with random identifiers.
	 */
	private Employee generateEmployee(long waitInSeconds) {
		Awaitility.await().pollDelay(waitInSeconds, TimeUnit.SECONDS).until(() -> true);
		var result = new Employee();
		UUID randomId = UUID.randomUUID();
		result.setEmpNo(randomId.toString());
		result.setId(randomId.getMostSignificantBits() + "");
		result.setFullName(Constants.EMPLOYEE_NAME);
		result.setHireDate(Instant.now());
		return result;
	}
	
}