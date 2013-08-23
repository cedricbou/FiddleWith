package domain;

public class BidouilleRepository {

	public Bidouille find(String id) {
		return new JRubyBidouille();
	}

}
