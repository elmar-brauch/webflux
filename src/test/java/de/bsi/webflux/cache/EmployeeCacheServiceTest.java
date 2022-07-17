package de.bsi.webflux.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import de.bsi.webflux.database.EmployeeDAO;
import de.bsi.webflux.database.ReactiveEmployeeRepository;
import reactor.core.publisher.Flux;

@SpringBootTest(classes = {EmployeeCacheService.class, EmployeeCacheConfiguration.class})
// TODO Import auto configuration
class EmployeeCacheServiceTest {

    @MockBean private ReactiveEmployeeRepository dbMock;
    @Autowired private EmployeeCacheService cachedService;

    @BeforeEach
    void setup() {
    	when(dbMock.findAll()).thenReturn(Flux.fromArray(new EmployeeDAO[] {
                (new EmployeeDAO("1", "1", "Hansi", 5))}));
    	// TODO Clear Cache
    }

    @Test
    void getAllEmployees() {
        cachedService.getAllEmployees();
        var employees = cachedService.getAllEmployees();
        verify(dbMock, times(1)).findAll();
        assertEquals("Hansi", employees.get(0).getFullName());
    }
}