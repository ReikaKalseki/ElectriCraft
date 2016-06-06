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

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ElectriCraft.ElectriCraft;
import Reika.ElectriCraft.Base.ElectriCable;
import Reika.ElectriCraft.Network.RF.RFNetwork;
import Reika.ElectriCraft.Registry.ElectriTiles;
import cofh.api.energy.IEnergyHandler;

public class TileEntityRFCable extends ElectriCable implements IEnergyHandler {

	protected RFNetwork network;
	private int RFlimit;

	public void setRFLimit(int limit) {
		if (RFlimit != limit) {
			RFlimit = limit;
			if (network != null)
				network.setIOLimit(limit);
		}
	}

	public int getRFLimit() {
		return RFlimit;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if ((this.getTicksExisted() == 0 || network == null) && !world.isRemote) {
			this.findAndJoinNetwork(world, x, y, z);
			//ReikaJavaLibrary.pConsole(network, Side.SERVER);
		}
		if (network != null && network.getIOLimit() != RFlimit) {
			RFlimit = network.getIOLimit();
		}
	}

	public final void findAndJoinNetwork(World world, int x, int y, int z) {
		network = new RFNetwork();
		network.setIOLimit(this.getRFLimit());
		network.addElement(this);
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = dirs[i];
			TileEntity te = this.getAdjacentTileEntity(dir);
			if (te instanceof TileEntityRFCable) {
				//ReikaJavaLibrary.pConsole(te, Side.SERVER);
				TileEntityRFCable n = (TileEntityRFCable)te;
				RFNetwork w = n.network;
				if (w != null) {
					//ReikaJavaLibrary.pConsole(dir+":"+te, Side.SERVER);
					w.merge(network);
				}
			}
			else if (te instanceof IEnergyHandler) {
				//ReikaJavaLibrary.pConsole(te, Side.SERVER);
				IEnergyHandler n = (IEnergyHandler)te;
				if (n.canConnectEnergy(dir.getOpposite()))
					network.addConnection(n, dir.getOpposite());
			}
		}
		this.onJoinNetwork();
	}

	protected void onJoinNetwork() {

	}

	public final RFNetwork getNetwork() {
		return network;
	}

	public final void setNetwork(RFNetwork n) {
		if (n == null) {
			ElectriCraft.logger.logError(this+" was told to join a null network!");
		}
		else {
			network = n;
			network.addElement(this);
		}
	}

	public final void removeFromNetwork() {
		if (network == null)
			ElectriCraft.logger.logError(this+" was removed from a null network!");
		else
			network.removeElement(this);
	}

	public final void rebuildNetwork() {
		this.removeFromNetwork();
		this.resetNetwork();
		this.findAndJoinNetwork(worldObj, xCoord, yCoord, zCoord);
	}

	public final void resetNetwork() {
		network = null;
	}

	@Override
	public ElectriTiles getMachine() {
		return ElectriTiles.CABLE;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
		return network != null ? network.addEnergy(maxReceive, simulate) : 0;
	}

	@Override
	public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
		return network != null ? network.drainEnergy(maxExtract, simulate) : 0;
	}

	@Override
	public boolean canConnectEnergy(ForgeDirection from) {
		return true;
	}

	@Override
	public int getEnergyStored(ForgeDirection from) {
		return 0;
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection from) {
		return Integer.MAX_VALUE;
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT)
	{
		super.readSyncTag(NBT);

		if (NBT.hasKey("limit"))
			this.setRFLimit(NBT.getInteger("limit"));
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT)
	{
		super.writeSyncTag(NBT);

		NBT.setInteger("limit", RFlimit);
	}

	@Override
	protected boolean connectsToTile(TileEntity te, ForgeDirection dir) {
		return te instanceof IEnergyHandler && ((IEnergyHandler)te).canConnectEnergy(dir.getOpposite());
	}

	@Override
	protected void onNetworkUpdate(World world, int x, int y, int z, ForgeDirection dir) {
		if (network != null) {
			TileEntity te = this.getAdjacentTileEntity(dir);
			if (te instanceof IEnergyHandler) {
				IEnergyHandler ih = (IEnergyHandler)te;
				if (ih.canConnectEnergy(dir.getOpposite())) {
					network.addConnection(ih, dir.getOpposite());
				}
			}
		}
	}

}
