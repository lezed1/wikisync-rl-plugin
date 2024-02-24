package com.andmcadams.wikisync;

import java.util.HashSet;
import java.util.Set;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

@Singleton
@Slf4j
public class WikiSyncWebSocketServer extends WebSocketServer {
	private final Set<WebSocket> activeConnections = new HashSet<>();

	public WikiSyncWebSocketServer(InetSocketAddress inetSocketAddress, LifeCycleHandler lifeCycleHandler)
	{
		super(inetSocketAddress);
		this.lifeCycleHandler = lifeCycleHandler;
	}

	@Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        log.info("onOpen(" + conn + ", " + handshake + ")");
		synchronized (this) {
			activeConnections.add(conn);
			this.lifeCycleHandler.onOpen(conn);
		}
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        log.info("onClose(" + conn + ", " + code + ", " + reason + ", " + remote + ")");
		synchronized (this) {
			activeConnections.remove(conn);
		}
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        log.info("onMessage(" + conn + ", " + message + ")");
		broadcast(message);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        log.info("onError(" + conn + ", " + ex + ")");
		if (conn == null)
		{
			this.lifeCycleHandler.onServerError();
		}
    }

    @Override
    public void onStart() {
        log.info("onStart WSWSS! Port: " + getPort());
		this.lifeCycleHandler.onStart(this);
    }

	public void broadcast(String text) {
		synchronized (this) {
			for (WebSocket conn : activeConnections) {
				conn.send(text);
			}
		}
	}
}
