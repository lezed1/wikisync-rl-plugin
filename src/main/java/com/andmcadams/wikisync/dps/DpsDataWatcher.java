package com.andmcadams.wikisync.dps;

import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.Subscribe;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class DpsDataWatcher
{

	private final Client client;
	private final ClientThread clientThread;
	private final NextTickUtil nextTick;

	@Subscribe
	public void onGameStateChanged(GameStateChanged e)
	{
		nextTick.queueAction(() ->
		{
			// get username and send
		});
	}

}
