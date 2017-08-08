/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ElectriCraft.TileEntities;

import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.ElectriCraft.Base.WiringTile;
import Reika.ElectriCraft.Registry.ElectriTiles;

public class TileEntityMeter extends WiringTile {

	@Override
	public int getResistance() {
		return 0;
	}

	@Override
	public void onNetworkChanged() {
		super.onNetworkChanged();
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		ReikaWorldHelper.causeAdjacentUpdates(worldObj, xCoord, yCoord, zCoord);
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

	@Override
	public int getRedstoneOverride() {
		long p = this.getWirePower();
		return p > 0 ? Math.max(1, Math.min(15, (int)(p/1048576))) : 0;
	}

}
