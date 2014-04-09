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

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import Reika.DragonAPI.Libraries.MathSci.ReikaEngLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.ElectriCraft.Auxiliary.WireEmitter;
import Reika.ElectriCraft.Auxiliary.WireReceiver;
import Reika.ElectriCraft.Base.NetworkTileEntity;
import Reika.ElectriCraft.Registry.BatteryType;
import Reika.ElectriCraft.Registry.ElectriItems;
import Reika.ElectriCraft.Registry.ElectriTiles;

public class TileEntityBattery extends NetworkTileEntity implements WireEmitter, WireReceiver {

	private long energy;
	private boolean lastPower;

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);

		if (!world.isRemote) {
			if (this.canReceivePower()) {
				int v = network.getTerminalVoltage(this);
				int a = network.getTerminalCurrent(this);
				long p = (long)v*(long)a;
				if (energy/20 < this.getMaxEnergy())
					energy += p;
				if (energy/20 >= this.getMaxEnergy()) {
					energy = this.getMaxEnergy()*20;
					network.updateWires();
				}
			}
			boolean flag = world.isBlockIndirectlyGettingPowered(x, y, z);
			if (flag != lastPower) {
				network.updateWires();
			}
			if (this.canEmitPower()) {
				int v = this.getGenVoltage();
				int a = this.getGenCurrent();
				long p = (long)v*(long)a;
				energy -= p;
				if (energy < 0) {
					energy = 0;
					network.updateWires();
				}
			}
			lastPower = flag;
		}
	}

	@Override
	public boolean canEmitPower() {
		return energy > 0 && worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord) && network.getNumberPathsStartingAt(this) > 0;
	}

	public long getStoredEnergy() {
		return energy/20;
	}

	public long getMaxEnergy() {
		return this.getBatteryType().maxCapacity;
	}

	public BatteryType getBatteryType() {
		return BatteryType.batteryList[worldObj.getBlockMetadata(xCoord, yCoord, zCoord)];
	}

	public String getDisplayEnergy() {
		long e = this.getStoredEnergy();
		String pre = ReikaEngLibrary.getSIPrefix(e);
		double b = ReikaMathLibrary.getThousandBase(e);
		return String.format("%.3f%sJ", b, pre);
	}

	@Override
	public boolean canNetworkOnSide(ForgeDirection dir) {
		return true;
	}

	@Override
	public int getCurrentLimit() {
		return 0;
	}

	@Override
	public void overCurrent() {

	}

	@Override
	public ElectriTiles getMachine() {
		return ElectriTiles.BATTERY;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT)
	{
		super.writeSyncTag(NBT);

		NBT.setLong("e", energy);
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT)
	{
		super.readSyncTag(NBT);

		energy = NBT.getLong("e");
	}

	@Override
	public boolean canReceivePowerFromSide(ForgeDirection dir) {
		return dir != ForgeDirection.UP;
	}

	@Override
	public boolean canEmitPowerToSide(ForgeDirection dir) {
		return dir == ForgeDirection.UP;
	}

	@Override
	public int getGenVoltage() {
		return this.canEmitPower() ? this.getBatteryType().outputVoltage : 0;
	}

	@Override
	public int getGenCurrent() {
		return this.canEmitPower() ? this.getBatteryType().outputCurrent : 0;
	}

	public void setEnergyFromNBT(ItemStack is) {
		if (is.itemID == ElectriItems.BATTERY.getShiftedID()) {
			if (is.stackTagCompound != null)
				energy = is.stackTagCompound.getLong("nrg")*20L;
		}
	}

	@Override
	public boolean canReceivePower() {
		return energy < this.getMaxEnergy()*20;
	}

}
