package bealby.tom.FakeAuction.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuctionController {
	private String status = "Open";
	
	@RequestMapping("/receiveJoinRequest")
	public ResponseEntity<String> receiveJoinRequest() {
		System.out.println("Received a request to join the auction");
		return ResponseEntity.ok("Received a request to join the auction");
	}
	
	@RequestMapping("/closeAuction")
	public ResponseEntity<String> closeAuction() {
		// TODO notify clients that auction is closed
		System.out.println("notify clients that auction is closed");
		status = "Closed";
		return ResponseEntity.ok("notified clients that auction is closed");
	}
	
	@RequestMapping("/getStatus")
	public ResponseEntity<String> getStatus() {
		System.out.println("Received a request to show my status");
		return ResponseEntity.ok("current status:" + status);
	}
}
