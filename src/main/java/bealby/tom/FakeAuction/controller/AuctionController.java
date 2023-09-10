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
	private boolean isRequestReceivedToJoinAuction = false;

	@RequestMapping("/receiveJoinRequest")
	public ResponseEntity<String> receiveJoinRequest() {
		System.out.println("Received a request to join the auction");
		isRequestReceivedToJoinAuction = true;
		return ResponseEntity.ok("Received a request to join the auction");
	}

	@RequestMapping("/openAuction")
	public ResponseEntity<String> openAuction() {
		System.out.println("start auction with status open");
		status = "Open";
		return ResponseEntity.ok("successfully opened the auction");
	}
	
	@RequestMapping("/closeAuction")
	public ResponseEntity<String> closeAuction() {
		System.out.println("Received a request to close the auction. I will notify "
				+ "clients that auction is closed");
		status = "Closed";
		return sendRequest();
	}

	@RequestMapping("/getReceiveStatus")
	public ResponseEntity<String> getReceiveStatus() {
		System.out.println("Received a request to check my receive status:" + isRequestReceivedToJoinAuction);
		return ResponseEntity.ok("ReceiveStatus:" + isRequestReceivedToJoinAuction);
	}

	@RequestMapping("/getStatus")
	public ResponseEntity<String> getStatus() {
		System.out.println("Received a request to show my status. My current status is:" + status);
		return ResponseEntity.ok("current status:" + status);
	}

	private ResponseEntity<String> sendRequest() throws HttpClientErrorException {
	    final String url = "http://localhost:8092/receiveAuctionMessage";
		return restTemplate.getForEntity(url, String.class);
	}
}
