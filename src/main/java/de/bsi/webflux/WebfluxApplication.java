package de.bsi.webflux;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import de.bsi.webflux.cache.EmployeeCacheService;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class WebfluxApplication {

	public static void main(String[] args) {
		var context = SpringApplication.run(WebfluxApplication.class, args);
		//testMongoDbAndRedisCache(context);
	}
	
	private static void testMongoDbAndRedisCache(ConfigurableApplicationContext context) {
		var cachedService = context.getBean(EmployeeCacheService.class);
		log.info("QUERY 1 - should be answered by MongoDB, see logs of org.mongodb.driver on INFO or DEBUG level.");
		cachedService.getAllEmployees();
		log.info("QUERY 2 - will be answered by Redis Cache, so there are no org.mongodb.driver logs.");
		cachedService.getAllEmployees();
		log.info("SHUTDOWN...");
		context.close();	
	}
	
}
