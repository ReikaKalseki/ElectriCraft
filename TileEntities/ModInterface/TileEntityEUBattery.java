/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ElectriCraft.TileEntities.ModInterface;

import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.APIStripper.Strippable;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.ElectriCraft.Auxiliary.BatteryTile;
import Reika.ElectriCraft.Base.ElectriTileEntity;
import Reika.ElectriCraft.Registry.ElectriItems;
import Reika.ElectriCraft.Registry.ElectriTiles;

@Strippable(value={"ic2.api.energy.tile.IEnergySink", "ic2.api.energy.tile.IEnergySource"})
public class TileEntityEUBattery extends ElectriTileEntity implements IEnergySink, IEnergySource, BatteryTile {

	public static final double CAPACITY = 600e6; //MFSU is 40M, each tier is 10x in IC2
	public static final double THROUGHPUT = 65536*2; //MFSU is 2048, MFE is 512

	private double energy;

	@Override
	public String getDisplayEnergy() {
		return String.format("%.3f EU", energy);
	}

	@Override
	public long getStoredEnergy() {
		return (long)energy;
	}

	@Override
	public long getMaxEnergy() {
		return (long)CAPACITY;
	}

	@Override
	public String getFormattedCapacity() {
		return String.format("%d EU", this.getMaxEnergy());
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBT.setDouble("e", energy);
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		energy = NBT.getDouble("e");
	}

	@Override
	public void setEnergyFromNBT(ItemStack is) {
		if (is.getItem() == ElectriItems.EUBATTERY.getItemInstance()) {
			if (is.stackTagCompound != null)
				energy = is.stackTagCompound.getLong("nrg");
			else
				energy = 0;
		}
		else {
			energy = 0;
		}
	}

	@Override
	public int getEnergyColor() {
		return 0xffff00;
	}

	@Override
	public boolean emitsEnergyTo(TileEntity receiver, ForgeDirection dir) {
		return dir == ForgeDirection.UP;
	}

	@Override
	public boolean acceptsEnergyFrom(TileEntity emitter, ForgeDirection dir) {
		return dir != ForgeDirection.UP;
	}

	@Override
	public ElectriTiles getMachine() {
		return ElectriTiles.EUBATTERY;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {

	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public void onFirstTick(World world, int x, int y, int z) {
		if (!world.isRemote && ModList.IC2.isLoaded())
			this.addTileToNet();
	}

	@ModDependent(ModList.IC2)
	private void addTileToNet() {
		MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
	}

	@Override
	protected void onInvalidateOrUnload(World world, int x, int y, int z, boolean invalidate) {
		if (!world.isRemote && ModList.IC2.isLoaded())
			this.removeTileFromNet();
	}

	@ModDependent(ModList.IC2)
	private void removeTileFromNet() {
		MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
	}

	@Override
	public double getOfferedEnergy() {
		return worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord) ? Math.min(THROUGHPUT, energy) : 0;
	}

	@Override
	public void drawEnergy(double amt) {
		//if (amt > 0)
		energy -= amt;
		//ReikaJavaLibrary.pConsole("Drawing "+amt+" from "+this);
		energy = MathHelper.clamp_double(energy, 0, CAPACITY);
	}

	@Override
	public int getSourceTier() {
		return 4;
	}

	@Override
	public double getDemandedEnergy() {
		return CAPACITY-energy;
	}

	@Override
	public int getSinkTier() {
		return this.getSourceTier();
	}

	@Override
	public double injectEnergy(ForgeDirection dir, double amt, double voltage) {
		double add = Math.min(CAPACITY-energy, amt);
		//ReikaJavaLibrary.pConsole("Injecting "+amt+" to "+this);
		if (add > 0) {
			energy += add;
			return add;
		}
		return 0;
	}

}
