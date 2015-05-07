/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ElectriCraft.Auxiliary;

import net.minecraft.world.World;
import cpw.mods.fml.common.eventhandler.Event;

public class ElectriNetworkEvent extends Event {

	public final World networkWorld;
	public final long worldTime;
	public final long systemTime;

	public ElectriNetworkEvent(World world) {
		networkWorld = world;
		worldTime = world.getTotalWorldTime();
		systemTime = System.currentTimeMillis();
	}

	public static class ElectriNetworkTickEvent extends ElectriNetworkEvent {

		private static int currentIndex = 0;
		public final int tickIndex;

		public ElectriNetworkTickEvent(World world) {
			super(world);
			tickIndex = currentIndex;
			currentIndex++;
		}

	}

	public static class ElectriNetworkRepathEvent extends ElectriNetworkEvent {

		public ElectriNetworkRepathEvent(World world) {
			super(world);
		}

	}
}
