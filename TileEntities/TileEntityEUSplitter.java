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

import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.APIStripper.Strippable;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.DragonAPI.ModInteract.ReikaEUHelper;
import Reika.ElectriCraft.Base.ElectriTileEntity;
import Reika.ElectriCraft.Registry.ElectriTiles;
import Reika.RotaryCraft.API.Interfaces.Screwdriverable;

@Strippable(value = {"ic2.api.energy.tile.IEnergySink", "ic2.api.energy.tile.IEnergySource"})
public class TileEntityEUSplitter extends ElectriTileEntity implements IEnergySource, IEnergySink, Screwdriverable {

	private boolean[] out = new boolean[6];
	private int split;
	private double EUin;
	private double EUout;
	private int tierout;
	private int tierin;

	private ForgeDirection facing;

	private void calculate() {
		int s = 0;
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = dirs[i];
			if (this.emitsTo(dir)) {
				s++;
			}
		}
		split = s;
		EUout = split > 0 ? EUin/split : 0;
		tierin = ReikaEUHelper.getIC2TierFromEUVoltage(EUin);
		tierout = ReikaEUHelper.getIC2TierFromEUVoltage(EUout);
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (!world.isRemote) {
			this.calculate();
			EUin = 0; //clear for next tick
		}
	}

	@Override
	public boolean emitsEnergyTo(TileEntity receiver, ForgeDirection direction) {
		return this.emitsTo(direction);
	}

	@Override
	public boolean acceptsEnergyFrom(TileEntity emitter, ForgeDirection direction) {
		return direction == this.getFacing();
	}

	@Override
	public double getDemandedEnergy() {
		return Double.MAX_VALUE;
	}

	@Override
	public int getSinkTier() {
		return tierin;
	}

	@Override
	public double injectEnergy(ForgeDirection directionFrom, double amount, double voltage) {
		EUin = amount;
		return amount;
	}

	@Override
	public double getOfferedEnergy() {
		return EUout;
	}

	@Override
	public void drawEnergy(double amount) {

	}

	@Override
	public int getSourceTier() {
		return tierout;
	}

	@Override
	public ElectriTiles getMachine() {
		return ElectriTiles.EUSPLIT;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public boolean onShiftRightClick(World world, int x, int y, int z, ForgeDirection side) {
		if (!world.isRemote && ModList.IC2.isLoaded())
			this.removeTileFromNet();
		out[side.ordinal()] = !out[side.ordinal()];
		world.markBlockForUpdate(x, y, z);
		ReikaWorldHelper.causeAdjacentUpdates(world, x, y, z);
		if (!world.isRemote && ModList.IC2.isLoaded())
			this.addTileToNet();
		return true;
	}

	@Override
	public boolean onRightClick(World world, int x, int y, int z, ForgeDirection side) {
		if (!world.isRemote && ModList.IC2.isLoaded())
			this.removeTileFromNet();
		facing = side;
		world.markBlockForUpdate(x, y, z);
		ReikaWorldHelper.causeAdjacentUpdates(world, x, y, z);
		if (!world.isRemote && ModList.IC2.isLoaded())
			this.addTileToNet();
		return true;
	}

	public boolean emitsTo(ForgeDirection s) {
		return this.emitsTo(s.ordinal());
	}

	public boolean emitsTo(int s) {
		return out[s] && s != this.getFacing().ordinal();
	}

	public ForgeDirection getFacing() {
		return facing != null ? facing : ForgeDirection.EAST;
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		facing = dirs[NBT.getInteger("face")];

		for (int i = 0; i < 6; i++) {
			out[i] = NBT.getBoolean("emit"+i);
		}

		split = NBT.getInteger("split");
		tierout = NBT.getInteger("tierout");
		tierin = NBT.getInteger("tierin");
		EUin = NBT.getDouble("in");
		EUout = NBT.getDouble("out");
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBT.setInteger("face", this.getFacing().ordinal());

		NBT.setInteger("split", split);
		NBT.setInteger("tierout", tierout);
		NBT.setInteger("tierin", tierin);
		NBT.setDouble("in", EUin);
		NBT.setDouble("out", EUout);

		for (int i = 0; i < 6; i++) {
			NBT.setBoolean("emit"+i, out[i]);
		}
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

}
