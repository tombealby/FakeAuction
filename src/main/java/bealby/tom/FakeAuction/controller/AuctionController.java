package bealby.tom.FakeAuction.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@RestController
public class AuctionController {
	private String status = "Open";
	private RestTemplate restTemplate = new RestTemplate();
	private boolean isRequestReceivedToJoinAuction = false;
	private int currentPrice;
	private int priceIncrement;
	private String currentWinningBidder;
	private Map<String, Integer> bidsByBidders;

	@RequestMapping("/receiveJoinRequest")
	public ResponseEntity<String> receiveJoinRequest() {
		System.out.println("Received a request to join the auction");
		isRequestReceivedToJoinAuction = true;
		return ResponseEntity.ok("Received a request to join the auction");
	}

	@RequestMapping("/openAuction")
	public ResponseEntity<String> openAuction() {
		this.bidsByBidders = new HashMap<>();
		this.currentWinningBidder = "other bidder";
		System.out.println("start auction with status open");
		status = "Open";
		return ResponseEntity.ok("successfully opened the auction");
	}
	
	@RequestMapping("/closeAuction")
	public ResponseEntity<String> closeAuction() {
		System.out.println("Received a request to close the auction. I will notify "
				+ "clients that auction is closed");
		status = "Closed";
		return notifyParticipantsThatAuctionHasClosed();
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

	private ResponseEntity<String> notifyParticipantsThatAuctionHasClosed() throws HttpClientErrorException {
	    final String url = "http://localhost:8092/receiveAuctionMessage";
		return restTemplate.getForEntity(url, String.class);
	}

	@RequestMapping("/reportPriceAndIncrement")
	public ResponseEntity<String> reportPriceAndIncrementAndWinningBidderToParticipants(
			@RequestParam("currentPrice") Integer currentPrice, @RequestParam("priceIncrement") Integer priceIncrement,
			@RequestParam("winningBidder") String winningBidder) {
		this.currentPrice = currentPrice;
		this.priceIncrement = priceIncrement;
		System.out.println("Auction has been told that current price is " + currentPrice + ", and"
				+ " price increment is " + priceIncrement + " and winning bidder of \"" + winningBidder
				+ "\". Auction will notify participants of this information.");
		notifyParticipantsOfPriceAndIncrementAndWinningBidder();
		return ResponseEntity.ok("");
	}

	private ResponseEntity<String> notifyParticipantsOfPriceAndIncrementAndWinningBidder()
			throws HttpClientErrorException {
		final String url = "http://localhost:8092/priceNotification?currentPrice=" + this.currentPrice
				+ "&priceIncrement=" + this.priceIncrement + "&winningBidder=" + this.currentWinningBidder;
		return restTemplate.getForEntity(url, String.class);
	}

	@RequestMapping("/getLatestBid")
	public ResponseEntity<String> getBidForBidder(@RequestParam("bidder") String bidder) {
		System.out.println("Received a request to show the latest bid for \"" + bidder + "\". The latest bid"
				+ " that I have received for \"" + bidder + "\" is:"
				+ bidsByBidders.get(bidder));
		return ResponseEntity.ok("The latest bid received from \"" + bidder + "\" is:" + bidsByBidders.get(bidder));
	}

	@RequestMapping("/receiveBid")
	public ResponseEntity<String> receiveBid(@RequestParam("bid") int bid, @RequestParam("bidderId") String bidderId) {
		System.out.println("I have received a bid of " + bid + " from " + bidderId);
		bidsByBidders.put(bidderId, bid);
		return ResponseEntity.ok("Thanks for your bid of " + bid);
	}
}
