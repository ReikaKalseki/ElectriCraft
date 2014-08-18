/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ElectriCraft;

import Reika.DragonAPI.Auxiliary.TickRegistry.TickHandler;
import Reika.DragonAPI.Auxiliary.TickRegistry.TickType;
import Reika.ElectriCraft.Auxiliary.ElectriNetworkTickEvent;

import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;

public class NetworkTicker implements TickHandler {

	@Override
	public void tick(Object... tickData) {
		World world = (World)tickData[0];
		if (world != null)
			MinecraftForge.EVENT_BUS.post(new ElectriNetworkTickEvent(world));
	}

	@Override
	public TickType getType() {
		return TickType.WORLD;
	}

	@Override
	public Phase getPhase() {
		return Phase.START;
	}

	@Override
	public String getLabel() {
		return "Electri Network";
	}

}
