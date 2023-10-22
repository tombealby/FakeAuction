package bealby.tom.FakeAuction.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@RestController
public class AuctionController {

	private static final int BID_WHEN_ACCEPTED_IN_AUCTION_BUT_NO_BIDS_MADE = -1;

	private String status = "Open";
	private RestTemplate restTemplate = new RestTemplate();
	private int currentPrice;
	private int priceIncrement;
	private String currentWinningBidder;
	private Map<String, Integer> bidsByBidders;

	@RequestMapping("/receiveJoinRequest")
	public ResponseEntity<String> receiveJoinRequest(@RequestParam("bidder") String bidder) {
		System.out.println("Received a request from \"" + bidder + "\" to join the auction");
		if (isValidJoinRequest(bidder)) {
			bidsByBidders.put(bidder, BID_WHEN_ACCEPTED_IN_AUCTION_BUT_NO_BIDS_MADE);
			return ResponseEntity.ok("Your request to join the auction has been accepted");
		} else {
			return new ResponseEntity<String>("Invalid auction join request. It that appears that"
					+ " you are already participating in the aucion.", HttpStatus.BAD_REQUEST);
		}

	}

	private boolean isValidJoinRequest(final String bidder) {
		return !bidsByBidders.containsKey(bidder);
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

	@RequestMapping("/getBidderJoinedStatus")
	public ResponseEntity<String> getReceiveStatus(@RequestParam("bidder") String bidder) {
		final  boolean isRequestReceivedToJoinAuction = bidsByBidders.containsKey(bidder);
		System.out.println("Received a request to check whether bidder \"" + bidder +
			"\" has joined the auction. My answer is " + isRequestReceivedToJoinAuction);
		return ResponseEntity.ok("ReceiveStatus:" + isRequestReceivedToJoinAuction + " for bidder:" + bidder);
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
		if (isValidBid(bid)) {
			currentPrice = bid;
			bidsByBidders.put(bidderId, bid);
			return ResponseEntity.ok("Thanks for your bid of " + bid + ". Your bid has been accepted");
		} else {
			return new ResponseEntity<String>("You have made an invalid bid.", HttpStatus.BAD_REQUEST);
		}

	}

	private boolean isValidBid(int bid) {
		return bid >= (currentPrice + priceIncrement);
	}
}
