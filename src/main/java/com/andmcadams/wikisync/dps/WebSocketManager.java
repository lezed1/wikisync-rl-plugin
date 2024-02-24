package com.andmcadams.wikisync.dps;

import com.andmcadams.wikisync.WikiSyncWebSocketServer;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class WebSocketManager
{

	private final AtomicBoolean serverActive = new AtomicBoolean(false);

	private ScheduledExecutorService wsExecutor;

	private 

	public void startUp()
	{
		wsExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
			Thread wsThread = new Thread(r, "WikiSyncWebsocket");
			wsThread.setDaemon(true); // prevent from hanging shutdown
			return wsThread;
		});
		wsExecutor.schedule(this::ensureActive, 1, TimeUnit.SECONDS);
	}
	
	public void shutDown()
	{
		log.info("Shutting down WikiSync Websocket Manager. Server active = {}", serverActive.getPlain());
		wsExecutor.shutdownNow();
	}
	
	private void ensureActive()
	{
		if (serverActive.get())
		{
			return;
		}


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

}
