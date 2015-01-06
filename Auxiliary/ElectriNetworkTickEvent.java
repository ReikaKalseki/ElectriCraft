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

public class ElectriNetworkTickEvent extends Event {

	private static int currentIndex = 0;

	public final World networkWorld;
	public final long worldTime;
	public final long systemTime;
	public final int tickIndex;

	public ElectriNetworkTickEvent(World world) {
		networkWorld = world;
		tickIndex = currentIndex;
		worldTime = world.getTotalWorldTime();
		systemTime = System.currentTimeMillis();
		currentIndex++;
	}

}
