package de.bsi.webflux.cache;

import de.bsi.webflux.database.EmployeeDAO;
import de.bsi.webflux.database.ReactiveEmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Cacheable(cacheNames = EmployeeCacheService.EMPLOYEE_CACHE)
public class EmployeeCacheService {

	public static final String EMPLOYEE_CACHE = "employeeCache";
	
    @Autowired private ReactiveEmployeeRepository mongoRepo;

    public List<EmployeeDAO> getAllEmployees() {
        return mongoRepo.findAll().toStream().collect(Collectors.toList());
    }
}
