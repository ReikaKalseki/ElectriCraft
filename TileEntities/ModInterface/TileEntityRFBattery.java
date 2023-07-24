/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ElectriCraft.TileEntities.ModInterface;

import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.DragonAPI.Libraries.MathSci.ReikaEngLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.ElectriCraft.Base.BatteryTileBase;
import Reika.ElectriCraft.Registry.ElectriItems;
import Reika.ElectriCraft.Registry.ElectriTiles;

import cofh.api.energy.IEnergyConnection;
import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyReceiver;

public class TileEntityRFBattery extends BatteryTileBase implements IEnergyHandler {

	private long energy;
	public static final long CAPACITY = 60000000000000L;//1099511627775L;//;

	@Override
	public String getDisplayEnergy() {
		return formatNumber(energy);
	}

	@Override
	public long getStoredEnergy() {
		return energy;
	}

	@Override
	public long getMaxEnergy() {
		return CAPACITY;
	}

	@Override
	public String getFormattedCapacity() {
		return formatNumber(CAPACITY);
	}

	private static String formatNumber(long num) {
		return String.format("%.3f %sRF", ReikaMathLibrary.getThousandBase(num), ReikaEngLibrary.getSIPrefix(num));
	}

	@Override
	public ElectriTiles getTile() {
		return ElectriTiles.RFBATTERY;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);

		if (world.getTotalWorldTime()%64 == 0) {
			world.markBlockForUpdate(x, y, z);
		}

		if (!world.isRemote && this.hasRedstoneSignal()) {
			int exp = (int)Math.min(energy, Integer.MAX_VALUE);
			if (exp > 0) {
				TileEntity te = this.getAdjacentTileEntity(ForgeDirection.UP);
				if (te instanceof IEnergyConnection) {
					if (((IEnergyConnection)te).canConnectEnergy(ForgeDirection.DOWN)) {
						if (te instanceof IEnergyReceiver) {
							IEnergyReceiver ier = (IEnergyReceiver)te;
							int added = ier.receiveEnergy(ForgeDirection.DOWN, exp, false);
							energy -= added;
						}
						else if (te instanceof IEnergyHandler) {
							IEnergyHandler ieh = (IEnergyHandler)te;
							int added = ieh.receiveEnergy(ForgeDirection.DOWN, exp, false);
							energy -= added;
						}
					}
				}
			}
		}
	}

	@Override
	public boolean canConnectEnergy(ForgeDirection dir) {
		return true;
	}

	@Override
	public int receiveEnergy(ForgeDirection dir, int amt, boolean simulate) {
		long ret = dir != ForgeDirection.UP ? Math.min(amt, CAPACITY-energy) : 0;
		if (ret > 0 && !simulate) {
			energy += ret;
		}
		return (int)ret;
	}

	@Override
	public int extractEnergy(ForgeDirection dir, int amt, boolean simulate) {
		long ret = dir == ForgeDirection.UP ? Math.min(amt, energy) : 0;
		if (ret > 0 && !simulate) {
			energy -= ret;
		}
		return (int)ret;
	}

	@Override
	public int getEnergyStored(ForgeDirection dir) {
		return energy == this.getMaxEnergy() ? Integer.MAX_VALUE : (int)Math.min(energy, Integer.MAX_VALUE-1);
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection dir) {
		return Integer.MAX_VALUE;
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBT.setLong("e", energy);
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		energy = NBT.getLong("e");
	}

	@Override
	public int getEnergyColor() {
		return 0xff1111;
	}

	@Override
	public String getUnitName() {
		return "RF";
	}

	@Override
	public boolean isDecimalSystem() {
		return false;
	}

	@Override
	protected Item getPlacerItem() {
		return ElectriItems.RFBATTERY.getItemInstance();
	}

	@Override
	protected void setEnergy(long val) {
		energy = val;
	}

}
