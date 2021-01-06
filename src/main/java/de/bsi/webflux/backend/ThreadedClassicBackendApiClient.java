package de.bsi.webflux.backend;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ThreadedClassicBackendApiClient implements BackendApiClient {
	
	private List<Integer> queue = new ArrayList<>();
	@Autowired ClassicBackendApiClient client;
	
	@Override
	public void callApi(final int requestId) {
		queue.add(Integer.valueOf(requestId));
		CompletableFuture.runAsync(() -> {
			client.callApi(requestId);
			queue.remove(Integer.valueOf(requestId));
		});
	}
	
	@Override
	public boolean isQueueEmpty() {
		return queue.isEmpty();
	}

}
