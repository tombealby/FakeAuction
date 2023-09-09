package bealby.tom.FakeAuction.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@RestController
public class AuctionController {
	private String status = "Open";
	private RestTemplate restTemplate = new RestTemplate();

	@RequestMapping("/receiveJoinRequest")
	public ResponseEntity<String> receiveJoinRequest() {
		System.out.println("Received a request to join the auction");
		return ResponseEntity.ok("Received a request to join the auction");
	}
	
	@RequestMapping("/closeAuction")
	public ResponseEntity<String> closeAuction() {
		System.out.println("notify clients that auction is closed");
		status = "Closed";
		return sendRequest();
	}
	
	@RequestMapping("/getStatus")
	public ResponseEntity<String> getStatus() {
		System.out.println("Received a request to show my status");
		return ResponseEntity.ok("current status:" + status);
	}

	private ResponseEntity<String> sendRequest() throws HttpClientErrorException {
	    final String url = "http://localhost:8092/receiveAuctionMessage";
		return restTemplate.getForEntity(url, String.class);
	}
}
