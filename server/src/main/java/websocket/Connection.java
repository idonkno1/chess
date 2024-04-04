package websocket;

import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;


public class Connection {
    public Session session;

    public String username;
    public Connection(String username, Session session){
        this.username = username;
        this.session = session;

    }
    public void send(String msg) throws IOException {
        session.getRemote().sendString(msg);
    }
}
