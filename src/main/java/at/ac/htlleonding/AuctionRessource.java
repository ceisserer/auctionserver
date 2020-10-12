package at.ac.htlleonding;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/auctionrest/{username}")
public class AuctionRessource {

  @Inject
  AuctionService auctionService;

  @POST
  public boolean makeBid(@PathParam("username") String userName, @QueryParam("bid") int bid) {
    return auctionService.makeBid(userName, bid);
  }
}
