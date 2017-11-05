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

import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergyAcceptor;
import ic2.api.energy.tile.IEnergyConductor;
import ic2.api.energy.tile.IEnergyEmitter;
import ic2.api.energy.tile.IEnergyTile;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.API.Interfaces.WorldRift;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.APIStripper.Strippable;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.ElectriCraft.Base.ElectriCable;
import Reika.ElectriCraft.Registry.ElectriTiles;

@Strippable(value="ic2.api.energy.tile.IEnergyConductor")
public class TileEntityEUCable extends ElectriCable implements IEnergyConductor {

	public static final double CAPACITY = Double.MAX_VALUE;//10e9;//Double.POSITIVE_INFINITY;

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
	public boolean acceptsEnergyFrom(TileEntity te, ForgeDirection dir) {
		if (!(te instanceof IEnergyTile))
			return false;
		return true;
	}

	@Override
	public boolean emitsEnergyTo(TileEntity te, ForgeDirection dir) {
		if (!(te instanceof IEnergyTile))
			return false;
		return true;
	}

	@Override
	public double getConductionLoss() {
		return 1e-12;
	}

	@Override
	public double getInsulationEnergyAbsorption() {
		return Double.POSITIVE_INFINITY;
	}

	@Override
	public double getInsulationBreakdownEnergy() {
		return CAPACITY;
	}

	@Override
	public double getConductorBreakdownEnergy() {
		return CAPACITY;
	}

	@Override
	public void removeInsulation() {

	}

	@Override
	public void removeConductor() {
		this.delete();
	}

	@Override
	public ElectriTiles getMachine() {
		return ElectriTiles.EUCABLE;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {

	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	protected boolean connectsToTile(TileEntity te, ForgeDirection dir) {
		if (te instanceof IEnergyEmitter && ((IEnergyEmitter)te).emitsEnergyTo(this, dir.getOpposite()))
			return true;
		if (te instanceof IEnergyAcceptor && ((IEnergyAcceptor)te).acceptsEnergyFrom(this, dir.getOpposite()))
			return true;
		if (te instanceof WorldRift)
			return true;
		return false;
	}
}
