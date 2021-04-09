package de.bsi.webflux.database;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import de.bsi.webflux.Constants;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.*;

@Slf4j
@RestController
@RequestMapping(Constants.URL_PATH_EMPLOYEE)
public class ReactiveEmployeePersistenceController {
	
	@Autowired private ReactiveEmployeeRepository repo;
	
	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public Flux<EmployeeDAO> readDbEntries(
			@RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate hiredAtOrLater) {
		if (hiredAtOrLater == null)
			return repo.findAll();
		return repo.findAllByHireDateGreaterThan(hiredAtOrLater);
	}
	
	@DeleteMapping
	@ResponseStatus(HttpStatus.OK)
	public Mono<Void> deleteAllEmployees() {
		return repo.deleteAll()
				.doOnSuccess(ignored -> log.info("All database entries deleted."));
	}
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Mono<EmployeeDAO> generateDbEntries(@RequestBody(required = false) EmployeeDAO newEmployee) {
		if (newEmployee == null)
			return repo.insert(generateEmployee());
		else
			return repo.insert(newEmployee);
	}
	
	private EmployeeDAO generateEmployee() {
		var result = new EmployeeDAO();
		UUID randomId = UUID.randomUUID();
		result.setEmpNo(randomId.toString());
		result.setFullName(Constants.EMPLOYEE_NAME);
		result.setHireDate(LocalDate.now());
		return result;
	}

}
