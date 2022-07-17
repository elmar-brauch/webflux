package de.bsi.webflux.cache;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.bsi.webflux.database.EmployeeDAO;
import de.bsi.webflux.database.ReactiveEmployeeRepository;

@Service
// TODO Activate Cache for this bean by annotation
public class EmployeeCacheService {

	@Autowired private ReactiveEmployeeRepository mongoRepo;

    public List<EmployeeDAO> getAllEmployees() {
        return mongoRepo.findAll().toStream().toList();
    }
}
