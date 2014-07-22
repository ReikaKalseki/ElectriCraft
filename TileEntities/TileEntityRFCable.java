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

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.ForgeDirection;
import Reika.ElectriCraft.ElectriCraft;
import Reika.ElectriCraft.Base.ElectriTileEntity;
import Reika.ElectriCraft.Blocks.BlockRFCable;
import Reika.ElectriCraft.Network.RF.RFNetwork;
import Reika.ElectriCraft.Registry.ElectriTiles;
import cofh.api.energy.IEnergyHandler;

public class TileEntityRFCable extends ElectriTileEntity implements IEnergyHandler {

	private boolean[] connections = new boolean[6];

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
				if (n.canInterface(dir.getOpposite()))
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
	public boolean canInterface(ForgeDirection from) {
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

		for (int i = 0; i < 6; i++) {
			connections[i] = NBT.getBoolean("conn"+i);
		}

		if (NBT.hasKey("limit"))
			this.setRFLimit(NBT.getInteger("limit"));
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT)
	{
		super.writeSyncTag(NBT);

		for (int i = 0; i < 6; i++) {
			NBT.setBoolean("conn"+i, connections[i]);
		}

		NBT.setInteger("limit", RFlimit);
	}

	public boolean isConnectionValidForSide(ForgeDirection dir) {
		if (dir.offsetX == 0 && MinecraftForgeClient.getRenderPass() != 1)
			dir = dir.getOpposite();
		return connections[dir.ordinal()];
	}

	@Override
	public final AxisAlignedBB getRenderBoundingBox() {
		return AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord+1, yCoord+1, zCoord+1);
	}

	public void recomputeConnections(World world, int x, int y, int z) {
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = dirs[i];
			int dx = x+dir.offsetX;
			int dy = y+dir.offsetY;
			int dz = z+dir.offsetZ;
			connections[i] = this.isConnected(dir);
			world.markBlockForRenderUpdate(dx, dy, dz);
			if (network != null) {
				TileEntity te = this.getAdjacentTileEntity(dir);
				if (te instanceof IEnergyHandler) {
					IEnergyHandler ih = (IEnergyHandler)te;
					if (ih.canInterface(dir.getOpposite())) {
						network.addConnection(ih, dir.getOpposite());
					}
				}
			}
		}
		world.markBlockForRenderUpdate(x, y, z);
	}

	public void deleteFromAdjacentConnections(World world, int x, int y, int z) {
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = dirs[i];
			int dx = x+dir.offsetX;
			int dy = x+dir.offsetY;
			int dz = x+dir.offsetZ;
			ElectriTiles m = ElectriTiles.getTE(world, dx, dy, dz);
			if (m == this.getMachine()) {
				TileEntityRFCable te = (TileEntityRFCable)world.getBlockTileEntity(dx, dy, dz);
				te.connections[dir.getOpposite().ordinal()] = false;
				world.markBlockForRenderUpdate(dx, dy, dz);
			}
		}
	}

	public void addToAdjacentConnections(World world, int x, int y, int z) {
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = dirs[i];
			int dx = x+dir.offsetX;
			int dy = x+dir.offsetY;
			int dz = x+dir.offsetZ;
			ElectriTiles m = ElectriTiles.getTE(world, dx, dy, dz);
			if (m == this.getMachine()) {
				TileEntityRFCable te = (TileEntityRFCable)world.getBlockTileEntity(dx, dy, dz);
				te.connections[dir.getOpposite().ordinal()] = true;
				world.markBlockForRenderUpdate(dx, dy, dz);
			}
		}
	}

	private boolean isConnected(ForgeDirection dir) {
		int x = xCoord+dir.offsetX;
		int y = yCoord+dir.offsetY;
		int z = zCoord+dir.offsetZ;
		ElectriTiles m = this.getMachine();
		ElectriTiles m2 = ElectriTiles.getTE(worldObj, x, y, z);
		if (m == m2)
			return true;
		//certain TEs
		return false;
	}

	public boolean isConnectedOnSideAt(World world, int x, int y, int z, ForgeDirection dir) {
		dir = dir.offsetX == 0 ? dir.getOpposite() : dir;
		int dx = x+dir.offsetX;
		int dy = y+dir.offsetY;
		int dz = z+dir.offsetZ;
		int id = world.getBlockId(dx, dy, dz);
		int meta = world.getBlockMetadata(dx, dy, dz);
		if (id == this.getTileEntityBlockID())
			return true;
		TileEntity te = world.getBlockTileEntity(dx, dy, dz);
		boolean flag = false;
		if (te instanceof IEnergyHandler) {
			flag = flag || ((IEnergyHandler)te).canInterface(dir.getOpposite());
		}
		return flag;
	}

	public Icon getCenterIcon() {
		return BlockRFCable.getCenterIcon();
	}

	public Icon getEndIcon() {
		return BlockRFCable.getEndIcon();
	}

	public boolean isConnectedToHandlerOnSideAt(World world, int x, int y, int z, ForgeDirection dir) {
		dir = dir.offsetX == 0 ? dir.getOpposite() : dir;
		int dx = x+dir.offsetX;
		int dy = y+dir.offsetY;
		int dz = z+dir.offsetZ;
		int id = world.getBlockId(dx, dy, dz);
		int meta = world.getBlockMetadata(dx, dy, dz);
		if (id == this.getTileEntityBlockID())
			return false;
		TileEntity te = world.getBlockTileEntity(dx, dy, dz);
		boolean flag = false;
		if (te instanceof IEnergyHandler) {
			flag = flag || ((IEnergyHandler)te).canInterface(dir.getOpposite());
		}
		return flag;
	}

}
