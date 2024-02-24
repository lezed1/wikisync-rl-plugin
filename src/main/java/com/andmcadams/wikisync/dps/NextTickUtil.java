package com.andmcadams.wikisync.dps;

import java.util.ArrayList;
import java.util.concurrent.SynchronousQueue;
import javax.inject.Singleton;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;

@Singleton
public class NextTickUtil
{

	private final SynchronousQueue<Runnable> runQueue = new SynchronousQueue<>();

	public void queueAction(Runnable r)
	{
		runQueue.put(r);
	}

	@Subscribe
	public void onGameTick(GameTick e)
	{
		if (runQueue.isEmpty())
		{
			return;
		}

		ArrayList<Runnable> thisTickActions = new ArrayList<>();
		runQueue.drainTo(thisTickActions);

		thisTickActions.forEach(Runnable::run);
	}

}
