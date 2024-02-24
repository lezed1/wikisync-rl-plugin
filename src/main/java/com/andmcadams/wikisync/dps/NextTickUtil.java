package com.andmcadams.wikisync.dps;

import java.util.ArrayList;
import java.util.concurrent.SynchronousQueue;
import javax.inject.Singleton;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;

@Singleton
public class NextTickUtil
{

	private final SynchronousQueue<Runnable> nextTickQueue = new SynchronousQueue<>();

	public void queueAction(Runnable r)
	{
		try
		{
			nextTickQueue.put(r);
		}
		catch (InterruptedException e)
		{
			// ignored
		}
	}

	@Subscribe
	public void onGameTick(GameTick e)
	{
		if (nextTickQueue.isEmpty())
		{
			return;
		}

		ArrayList<Runnable> thisTickActions = new ArrayList<>();
		nextTickQueue.drainTo(thisTickActions);

		thisTickActions.forEach(Runnable::run);
	}

}
