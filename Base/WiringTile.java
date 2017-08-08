/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ElectriCraft.Base;

import net.minecraft.nbt.NBTTagCompound;

public abstract class WiringTile extends NetworkTileEntity {

	private int voltage;
	private int current;

	public abstract int getResistance();

	@Override
	public int getRedstoneOverride() {
		return 0;
	}

	@Override
	protected void onJoinNetwork() {
		current = network.getPointCurrent(this);
		voltage = network.getPointVoltage(this);
	}

	@Override
	public void onNetworkChanged() {
		current = network.getPointCurrent(this);
		voltage = network.getPointVoltage(this);
	}

	public final int getWireVoltage() {
		return voltage;
	}

	public final int getWireCurrent() {
		return current;
	}

	public final long getWirePower() {
		return (long)current*(long)voltage;
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT)
	{
		super.readSyncTag(NBT);

		voltage = NBT.getInteger("v");
		current = NBT.getInteger("a");
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT)
	{
		super.writeSyncTag(NBT);

		NBT.setInteger("a", current);
		NBT.setInteger("v", voltage);
	}

}
