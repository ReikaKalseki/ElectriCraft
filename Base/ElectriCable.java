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

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ElectriCraft.Registry.ElectriTiles;



public abstract class ElectriCable extends ElectriTileEntity {

	private boolean[] connections = new boolean[6];

	@Override
	protected void readSyncTag(NBTTagCompound NBT)
	{
		super.readSyncTag(NBT);

		for (int i = 0; i < 6; i++) {
			connections[i] = NBT.getBoolean("conn"+i);
		}
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT)
	{
		super.writeSyncTag(NBT);

		for (int i = 0; i < 6; i++) {
			NBT.setBoolean("conn"+i, connections[i]);
		}
	}

	public final boolean isConnectionValidForSide(ForgeDirection dir) {
		if (dir.offsetX == 0 && MinecraftForgeClient.getRenderPass() != 1)
			dir = dir.getOpposite();
		return connections[dir.ordinal()];
	}

	@Override
	public final AxisAlignedBB getRenderBoundingBox() {
		return AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord+1, yCoord+1, zCoord+1);
	}

	public final void recomputeConnections(World world, int x, int y, int z) {
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = dirs[i];
			int dx = x+dir.offsetX;
			int dy = y+dir.offsetY;
			int dz = z+dir.offsetZ;
			connections[i] = this.isConnected(dir);
			world.func_147479_m(dx, dy, dz);
			this.onNetworkUpdate(world, x, y, z, dir);
		}
		world.func_147479_m(x, y, z);
	}

	protected void onNetworkUpdate(World world, int x, int y, int z, ForgeDirection dir) {

	}

	public final void deleteFromAdjacentConnections(World world, int x, int y, int z) {
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = dirs[i];
			int dx = x+dir.offsetX;
			int dy = x+dir.offsetY;
			int dz = x+dir.offsetZ;
			ElectriTiles m = ElectriTiles.getTE(world, dx, dy, dz);
			if (m == this.getMachine()) {
				ElectriCable te = (ElectriCable)world.getTileEntity(dx, dy, dz);
				te.connections[dir.getOpposite().ordinal()] = false;
				world.func_147479_m(dx, dy, dz);
			}
		}
	}

	public final void addToAdjacentConnections(World world, int x, int y, int z) {
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = dirs[i];
			int dx = x+dir.offsetX;
			int dy = x+dir.offsetY;
			int dz = x+dir.offsetZ;
			ElectriTiles m = ElectriTiles.getTE(world, dx, dy, dz);
			if (m == this.getMachine()) {
				ElectriCable te = (ElectriCable)world.getTileEntity(dx, dy, dz);
				te.connections[dir.getOpposite().ordinal()] = true;
				world.func_147479_m(dx, dy, dz);
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

	public final boolean isConnectedOnSideAt(World world, int x, int y, int z, ForgeDirection dir) {
		dir = dir.offsetX == 0 ? dir.getOpposite() : dir;
		int dx = x+dir.offsetX;
		int dy = y+dir.offsetY;
		int dz = z+dir.offsetZ;
		Block b = world.getBlock(dx, dy, dz);
		int meta = world.getBlockMetadata(dx, dy, dz);
		if (b == this.getTileEntityBlockID())
			return true;
		TileEntity te = world.getTileEntity(dx, dy, dz);
		return te != null && this.connectsToTile(te, dir);
	}

	public final boolean isConnectedToHandlerOnSideAt(World world, int x, int y, int z, ForgeDirection dir) {
		dir = dir.offsetX == 0 ? dir.getOpposite() : dir;
		int dx = x+dir.offsetX;
		int dy = y+dir.offsetY;
		int dz = z+dir.offsetZ;
		Block b = world.getBlock(dx, dy, dz);
		int meta = world.getBlockMetadata(dx, dy, dz);
		if (b == this.getTileEntityBlockID())
			return false;
		TileEntity te = world.getTileEntity(dx, dy, dz);
		return te != null && this.connectsToTile(te, dir);
	}

	protected abstract boolean connectsToTile(TileEntity te, ForgeDirection dir);

	public final IIcon getCenterIcon() {
		return ((BlockElectriCable)this.getBlockType()).getCenterIcon();
	}

	public final IIcon getEndIcon() {
		return ((BlockElectriCable)this.getBlockType()).getEndIcon();
	}

}
