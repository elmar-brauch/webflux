package de.bsi.webflux.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;

import de.bsi.webflux.database.EmployeeDAO;
import de.bsi.webflux.database.ReactiveEmployeeRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootTest(classes = {EmployeeCacheService.class, EmployeeCacheConfiguration.class})
@ImportAutoConfiguration(classes = {CacheAutoConfiguration.class, RedisAutoConfiguration.class })
class EmployeeCacheServiceTest {

    @MockBean private ReactiveEmployeeRepository dbMock;
    @Autowired private EmployeeCacheService cachedService;
    @Autowired private CacheManager cacheManager;

    @BeforeEach
    void setup() {
    	when(dbMock.findAll()).thenReturn(Flux.fromArray(new EmployeeDAO[] {
                (new EmployeeDAO("1", "1", "Hansi", 5))}));
    	cacheManager.getCache(EmployeeCacheService.EMPLOYEE_CACHE).clear();
    }

    @Test
    void cacheInteractionTest() {
        cachedService.getAllEmployees();
        var employees = cachedService.getAllEmployees();
        verify(dbMock, times(1)).findAll();
        assertEquals("Hansi", employees.get(0).getFullName());
    }
    
    @Test
    void cacheInteractionWithParameterTest() {
    	when(dbMock.findById("1")).thenReturn(Mono.just(
                new EmployeeDAO("1", "1", "Hansi", 5)));
    	when(dbMock.findById("2")).thenReturn(Mono.just(
                new EmployeeDAO("2", "2", "Fritzi", 10)));
    	
    	cachedService.getEmployee("1");
    	var hit1 = cachedService.getEmployee("1");
    	
    	cachedService.getEmployee("2");
    	var hit2 = cachedService.getEmployee("2");
    	
        verify(dbMock, times(2)).findById(anyString());
        assertEquals("Hansi", hit1.getFullName());
        assertEquals("Fritzi", hit2.getFullName());
    }
}