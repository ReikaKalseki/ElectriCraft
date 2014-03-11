/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ElectroCraft;

import java.util.EnumSet;

import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import Reika.ElectroCraft.Auxiliary.ElectroNetworkTickEvent;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class NetworkTicker implements ITickHandler {

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
		World world = (World)tickData[0];
		if (world != null)
			MinecraftForge.EVENT_BUS.post(new ElectroNetworkTickEvent(world));
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {

	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.WORLD);
	}

	@Override
	public String getLabel() {
		return "Electro Network";
	}

}
