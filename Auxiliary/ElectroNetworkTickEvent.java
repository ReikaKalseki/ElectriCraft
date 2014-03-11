/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ElectroCraft.Auxiliary;

import net.minecraft.world.World;
import net.minecraftforge.event.Event;

public class ElectroNetworkTickEvent extends Event {

	private static int currentIndex = 0;

	public final World networkWorld;
	public final long worldTime;
	public final long systemTime;
	public final int tickIndex;

	public ElectroNetworkTickEvent(World world) {
		networkWorld = world;
		tickIndex = currentIndex;
		worldTime = world.getTotalWorldTime();
		systemTime = System.currentTimeMillis();
		currentIndex++;
	}

}
