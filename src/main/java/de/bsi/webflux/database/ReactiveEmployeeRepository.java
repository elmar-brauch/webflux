package de.bsi.webflux.database;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import reactor.core.publisher.Flux;

//String: Type of Employee ID.
public interface ReactiveEmployeeRepository extends ReactiveMongoRepository<EmployeeDAO, String> {
	
	Flux<EmployeeDAO> findAllByAgeGreaterThan(int age);
	
}
