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
import net.minecraftforge.common.util.ForgeDirection;
import Reika.DragonAPI.Libraries.MathSci.ReikaEngLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.ElectriCraft.Auxiliary.BatteryTile;
import Reika.ElectriCraft.Auxiliary.WireEmitter;
import Reika.ElectriCraft.Auxiliary.WireReceiver;
import Reika.ElectriCraft.Base.NetworkTileEntity;
import Reika.ElectriCraft.Registry.BatteryType;
import Reika.ElectriCraft.Registry.ElectriItems;
import Reika.ElectriCraft.Registry.ElectriTiles;

public class TileEntityBattery extends NetworkTileEntity implements WireEmitter, WireReceiver, BatteryTile {

	private long energy;
	private long lastE;
	private boolean lastPower;

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);

		if (!world.isRemote && network != null) {/*
			if ((world.getTotalWorldTime()&31) == 0) { what was this for
				network.updateWires();
			}*/

			if (world.getTotalWorldTime()%64 == 0) {
				ReikaWorldHelper.causeAdjacentUpdates(world, x, y, z);
			}

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
				if (energy <= 0) {
					energy = 0;
					network.updateWires();
				}
				else if (lastE == 0) {
					network.updateWires();
				}
			}
			lastPower = flag;
			lastE = energy;
		}
	}

	@Override
	public boolean canEmitPower() {
		boolean red = worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
		return energy > 0 && red && network.getNumberPathsStartingAt(this) > 0;
	}

	private long getGenPower() {
		return (long)this.getGenCurrent()*(long)this.getGenVoltage();
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
		if (is.getItem() == ElectriItems.BATTERY.getItemInstance()) {
			if (is.stackTagCompound != null)
				energy = is.stackTagCompound.getLong("nrg")*20L;
			else
				energy = 0;
		}
		else {
			energy = 0;
		}
	}

	@Override
	public boolean canReceivePower() {
		return energy < this.getMaxEnergy()*20;
	}

	@Override
	public int getRedstoneOverride() {
		return (int)(15D*this.getStoredEnergy()/this.getMaxEnergy());
	}

	@Override
	public String getFormattedCapacity() {
		return this.getBatteryType().getFormattedCapacity();
	}

}
