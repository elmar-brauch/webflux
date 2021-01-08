package de.bsi.webflux.client;

import java.util.concurrent.TimeUnit;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import de.bsi.webflux.backend.*;
import lombok.extern.slf4j.Slf4j;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
@Slf4j
class ReactiveVsMonoBackendTest {
	
	private static final int NUMBER_OF_CALLS = 500;
	
	@Order(1)
	@Test
	void useRestTemplate(@Autowired ClassicBackendApiClient clientBean) {
		log.info("ClassicBackendApiClient API calls took {} ms", measureCalls(clientBean));
	}
	
	@Order(2)
	@Test
	void useRestTemplateInThreads(@Autowired ThreadedClassicBackendApiClient clientBean) {
		log.info("ThreadedClassicBackendApiClient API calls took {} ms", measureCalls(clientBean));
	}
	
	@Order(3)
	@Test
	void useWebClient(@Autowired ReactiveBackendApiClient clientBean) {
		log.info("ReactiveBackendApiClient API calls took {} ms", measureCalls(clientBean));
	}
	
	private long measureCalls(BackendApiClient clientBean) {
		long before = System.currentTimeMillis();
		for (int i = 1; i < NUMBER_OF_CALLS + 1; i++)
			clientBean.callApi(i);
		Awaitility.await().timeout(1, TimeUnit.MINUTES).until(clientBean::isQueueEmpty);
		return System.currentTimeMillis() - before;
	}

}