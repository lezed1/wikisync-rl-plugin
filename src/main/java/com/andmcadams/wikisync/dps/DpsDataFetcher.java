package com.andmcadams.wikisync.dps;

import com.andmcadams.wikisync.dps.messages.PlayerChanged;
import java.util.Objects;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Player;
import net.runelite.api.events.GameTick;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class DpsDataFetcher
{

	private final Client client;
	private final ClientThread clientThread;
	private final NextTickUtil nextTickUtil;
	private final EventBus eventBus;

	@Getter
	private String playerName;

	@Subscribe
	public void onGameTick(GameTick e)
	{
		checkUsername();
	}

	private void checkUsername()
	{
		String currentName = null;
		if (client.getGameState() == GameState.LOGGED_IN)
		{
			Player p = client.getLocalPlayer();
			if (p != null)
			{
				currentName = p.getName();
			}
		}

		if (!Objects.equals(this.playerName, currentName))
		{
			log.debug("WS player name changed prev=[{}] next=[{}]", this.playerName, currentName);
			this.playerName = currentName;
			eventBus.post(new PlayerChanged(this.playerName));
		}
	}

}
