package de.bsi.webflux.oauth;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest(classes = GeoApiClient.class)
@EnableConfigurationProperties
@Slf4j
class GeoApiClientTest {

	@Autowired GeoApiClient geoClient;
	
	static Stream<Arguments> createRealAddresses() {
		return Stream.of(
				Arguments.of("Elfengrund 1", "64367", "Mühltal"),
				Arguments.of("Soderstraße 30", "64", "Darmstadt"),
				Arguments.of("Nieder-Ramstädter Str.", "64285", "Da"));
	}
	
	@ParameterizedTest(name = "{index} => street={0}, postcode={1}, city={2}")
	@MethodSource("createRealAddresses")
	void testRealAddresses(String street, String postcode, String city) {
		long matchingAdrCount = geoClient
				.getRealAddresses(street, postcode, city)
				.doOnEach(adr -> log.info(adr.toString()))
				.count().block();
		assertTrue(matchingAdrCount > 0);
	}
	
	static Stream<Arguments> createFakeAddresses() {
		return Stream.of(
				Arguments.of("Elfengrund 1", "12345", "Mühltal"),
				Arguments.of("Donald", "12345", "Duck"),
				Arguments.of("Lilienstr. 1898", "64285", "Darmstadt"));
	}
	
	@ParameterizedTest(name = "{index} => street={0}, postcode={1}, city={2}")
	@MethodSource("createFakeAddresses")
	void testFakeAddresses(String street, String postcode, String city) {
		long matchingAdrCount = geoClient
				.getRealAddresses(street, postcode, city)
				.count().block();
		assertEquals(0, matchingAdrCount);
	}

}
