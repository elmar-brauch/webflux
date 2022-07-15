package de.bsi.webflux.cache;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import de.bsi.webflux.database.EmployeeDAO;
import de.bsi.webflux.database.ReactiveEmployeeRepository;

@Service
@Cacheable(cacheNames = EmployeeCacheService.EMPLOYEE_CACHE)
public class EmployeeCacheService {

	public static final String EMPLOYEE_CACHE = "employeeCache";
	
    @Autowired private ReactiveEmployeeRepository mongoRepo;

    public List<EmployeeDAO> getAllEmployees() {
        return mongoRepo.findAll().toStream().toList();
    }
}
