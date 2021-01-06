package de.bsi.webflux.backend;

public interface BackendApiClient {
	
	static final String API_HOST = "https://petstore.swagger.io";
	static final String API_URI = "/v2/pet/findByStatus?status=available&status=pending&status=sold";
	
	default boolean isQueueEmpty() {
		return true;
	}
	
	void callApi(int requestId);
}
