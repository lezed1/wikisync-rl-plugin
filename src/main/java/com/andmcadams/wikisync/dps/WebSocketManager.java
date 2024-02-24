package com.andmcadams.wikisync.dps;

import com.andmcadams.wikisync.dps.messages.PlayerChanged;
import com.andmcadams.wikisync.dps.ws.WSHandler;
import com.andmcadams.wikisync.dps.ws.WSWebsocketServer;
import com.google.gson.Gson;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.eventbus.Subscribe;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class WebSocketManager implements WSHandler
{

	private final static int PORT_MIN = 37767;
	private final static int PORT_MAX = 37776;

	private final AtomicBoolean serverActive = new AtomicBoolean(false);

	private final Gson gson;
	private final DpsDataFetcher dpsDataFetcher;

	private int nextPort;

	private WSWebsocketServer server;

	public void startUp()
	{
		this.nextPort = PORT_MIN;
		this.server = null;
		ensureActive();
	}

	public void shutDown()
	{
		log.info("Shutting down WikiSync Websocket Manager. Server active = {}", serverActive.getPlain());
		stopServer();
	}

	public void ensureActive()
	{
		if (!serverActive.compareAndExchange(false, true))
		{
			this.server = new WSWebsocketServer(this.nextPort++, this);
			log.debug("WSWSS attempting to start at: {}", this.server.getAddress());
			if (this.nextPort > PORT_MAX) {
				this.nextPort = PORT_MIN;
			}
		}
	}

	@Subscribe
	public void onPlayerChanged(PlayerChanged e)
	{
		if (serverActive.get())
		{
			this.server.broadcast(gson.toJson(e));
		}
	}

	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake)
	{
		conn.send(gson.toJson(new PlayerChanged(dpsDataFetcher.getPlayerName())));
	}

	@Override
	public void onMessage(WebSocket conn, String message)
	{
		// todo make a router
	}

	@Override
	public void onError(WebSocket conn, Exception ex)
	{
		log.debug("ws error conn=[{}]", conn == null ? null : conn.getLocalSocketAddress(), ex);
		if (conn == null)
		{
			log.debug("failed to bind to port, trying next");
			stopServer();
			if (this.nextPort != PORT_MIN)
			{
				ensureActive();
			}
		}
	}

	private void stopServer()
	{
		this.serverActive.set(false);
		if (this.server != null)
		{
			try
			{
				this.server.stop();
			}
			catch (InterruptedException e)
			{
				// ignored
			}
		}
	}
}
