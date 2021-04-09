package de.bsi.webflux.database;

import java.time.LocalDate;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import reactor.core.publisher.Flux;

//String: Type of Employee ID.
public interface ReactiveEmployeeRepository extends ReactiveMongoRepository<EmployeeDAO, String> {
	
	Flux<EmployeeDAO> findAllByHireDateGreaterThan(LocalDate hireDate);
	
}
