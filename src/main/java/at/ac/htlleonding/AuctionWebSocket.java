package at.ac.htlleonding;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/auctionws/{username}")
@ApplicationScoped
public class AuctionWebSocket {
  @Inject
  AuctionService auctionService;

  Map<String, Session> sessions = new ConcurrentHashMap<>();

  @OnOpen
  public void onOpen(Session session, @PathParam("username") String username) {
    sessions.put(username, session);
    notifyClients("User " + username + " joined");
  }

  @OnClose
  public void onClose(Session session, @PathParam("username") String username) {
    sessions.remove(username);
    notifyClients("User " + username + " left");
  }

  @OnError
  public void onError(Session session, @PathParam("username") String username, Throwable throwable) {
    sessions.remove(username);
    notifyClients("User " + username + " left on error: " + throwable);
  }

  @OnMessage
  public void onMessage(int bid, @PathParam("username") String username) {
    boolean success = auctionService.makeBid(username, bid);
    if(success) {
      notifyClients(username+" made the highest bid: " + bid);
    }
  }

  public void notifyClients(String message) {
    sessions.values().forEach(s -> {
      s.getAsyncRemote().sendObject(message, result ->  {
        if (result.getException() != null) {
          System.out.println("Unable to send message: " + result.getException());
        }
      });
    });
  }

}
