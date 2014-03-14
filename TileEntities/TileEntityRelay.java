/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ElectriCraft.TileEntities;

import net.minecraft.world.World;
import Reika.ElectriCraft.Base.TileEntityWireComponent;
import Reika.ElectriCraft.Registry.ElectriTiles;

public class TileEntityRelay extends TileEntityWireComponent {

	private boolean lastPower;

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);

		boolean pwr = world.isBlockIndirectlyGettingPowered(x, y, z);
		if (!world.isRemote)
			if (pwr != lastPower)
				network.updateWires();
		lastPower = pwr;
	}

	@Override
	public int getResistance() {
		return this.isEnabled() ? 0 : Integer.MAX_VALUE;
	}

	@Override
	public void onNetworkChanged() {

	}

	@Override
	public int getCurrentLimit() {
		return this.isEnabled() ? Integer.MAX_VALUE : 0;
	}

	@Override
	public void overCurrent() {

	}

	@Override
	public int getIndex() {
		return ElectriTiles.RELAY.ordinal();
	}

	@Override
	public void animateWithTick(World world, int x, int y, int z) {

	}

	public boolean isEnabled() {
		return worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
	}

	@Override
	public float getHeight() {
		return 0.75F;
	}

	@Override
	public float getWidth() {
		return 0.75F;
	}

}
