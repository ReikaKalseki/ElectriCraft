/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ElectriCraft.TileEntities;

import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ElectriCraft.Base.WiringTile;
import Reika.ElectriCraft.Registry.ElectriTiles;

public class TileEntityMeter extends WiringTile {

	@Override
	public int getResistance() {
		return 0;
	}

	@Override
	public int getCurrentLimit() {
		return Integer.MAX_VALUE;
	}

	@Override
	public void overCurrent() {

	}

	@Override
	public ElectriTiles getMachine() {
		return ElectriTiles.METER;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public boolean canNetworkOnSide(ForgeDirection dir) {
		return dir != ForgeDirection.UP;
	}

}
