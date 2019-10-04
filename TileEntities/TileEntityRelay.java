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

import Reika.ElectriCraft.Auxiliary.Interfaces.ToggledConnection;
import Reika.ElectriCraft.Base.TileEntityWireComponent;
import Reika.ElectriCraft.Registry.ElectriTiles;

public class TileEntityRelay extends TileEntityWireComponent implements ToggledConnection {

	private boolean lastPower;

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);

		if (!world.isRemote)
			network.toString();

		boolean pwr = this.hasRedstoneSignal();
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
	public ElectriTiles getMachine() {
		return ElectriTiles.RELAY;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	public boolean isEnabled() {
		return this.hasRedstoneSignal();
	}

	@Override
	public float getHeight() {
		return 0.75F;
	}

	@Override
	public float getWidth() {
		return 0.75F;
	}

	@Override
	public boolean canConnect() {
		return this.isEnabled();
	}

}
