package com.andmcadams.wikisync;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.net.InetSocketAddress;
import java.util.Objects;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import org.java_websocket.WebSocket;

@Slf4j
@Singleton
public class WebSocketManager
{
	@Inject
	private Client client;

	@Inject
	private Gson gson;

	private WikiSyncWebSocketServer wikiSyncWebSocketServer;
	private final int minimumPort = 37767;
	private final int maximumPort = 37776;
	private int nextPortToTry = minimumPort;

	private String username;


	public void start()
	{
		tryStartingNextWebSocketServer();
	}

	private synchronized void tryStartingNextWebSocketServer()
	{
		InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1", nextPortToTry);
		WikiSyncWebSocketServer attemptedServer = new WikiSyncWebSocketServer(inetSocketAddress, this);
		attemptedServer.start();
		log.debug("WSWSS attempted to start on port: " + attemptedServer.getPort());
		nextPortToTry++;
		if (nextPortToTry > maximumPort) {
			nextPortToTry = minimumPort;
		}
	}

	public void stop()
	{
		stopCurrentServer();
	}

	private void stopCurrentServer()
	{
		try
		{
			if (wikiSyncWebSocketServer != null) {
				wikiSyncWebSocketServer.stop();
			}
		}
		catch (InterruptedException e)
		{
			log.debug("Failed to stop WSWSS server: " + e);
		}
	}

	public void onUsernameMaybeUpdated() {
		synchronized (this)
		{
			String previousUsername = this.username;
			if (client.getGameState() == GameState.LOGGED_IN)
			{
				this.username = client.getLocalPlayer().getName();
				client.getLocalPlayer().getName();
			}
			else
			{
				this.username = null;
			}

			if (!Objects.equals(previousUsername, username))
			{
				log.info("sending username update");
				this.wikiSyncWebSocketServer.broadcast(usernameUpdateMessage());
			}
		}
	}

	private String usernameUpdateMessage()
	{
		JsonObject message = new JsonObject();
		message.add("type", gson.toJsonTree("usernameUpdate"));
		message.add("username", gson.toJsonTree(username));
		return message.toString();
	}

	@Override
	public void onStart(WikiSyncWebSocketServer startedServer)
	{
		this.wikiSyncWebSocketServer = startedServer;
		log.debug("Successfully started WSWSS server");
	}

	@Override
	public void onServerError()
	{
		stopCurrentServer();
		tryStartingNextWebSocketServer();
	}

	@Override
	public void onOpen(WebSocket conn)
	{
		conn.send(usernameUpdateMessage());
	}
}
