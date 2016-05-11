/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ElectriCraft.Base;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.API.Interfaces.WorldRift;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.ElectriCraft.ElectriCraft;
import Reika.ElectriCraft.Auxiliary.NetworkTile;
import Reika.ElectriCraft.Network.WireNetwork;

public abstract class NetworkTileEntity extends ElectriTileEntity implements NetworkTile {

	protected WireNetwork network;
	private boolean isConnectable = true;

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if ((this.getTicksExisted() == 0 || network == null) && !world.isRemote) {
			this.findAndJoinNetwork(world, x, y, z);
			//ReikaJavaLibrary.pConsole(network, Side.SERVER);
		}
	}

	@Override
	public final void onInvalidateOrUnload(World world, int x, int y, int z, boolean invalid) {
		isConnectable = false;
		if (network != null)
			network.removeElement(this);
	}

	public final boolean isConnectable() {
		return isConnectable && !worldObj.isRemote && worldObj.checkChunksExist(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);
	}

	public void onNetworkChanged() {

	}

	public final void findAndJoinNetwork(World world, int x, int y, int z) {
		network = null;
		if (!isConnectable)
			return;
		network = new WireNetwork();
		network.addElement(this);
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = dirs[i];
			if (this.canNetworkOnSide(dir)) {
				TileEntity te = this.getAdjacentTileEntity(dir);
				this.linkTile(te, dir);
			}
		}
		this.onJoinNetwork();
	}

	private void linkTile(TileEntity te, ForgeDirection dir) {
		if (te instanceof NetworkTileEntity) {
			//ReikaJavaLibrary.pConsole(te, Side.SERVER);
			NetworkTileEntity n = (NetworkTileEntity)te;
			if (n.canNetworkOnSide(dir.getOpposite())) {
				WireNetwork w = n.network;
				if (w != null) {
					//ReikaJavaLibrary.pConsole(dir+":"+te, Side.SERVER);
					w.merge(network);
				}
			}
		}
		else if (te instanceof WorldRift) {
			WorldRift sr = (WorldRift)te;
			WorldLocation loc = sr.getLinkTarget();
			if (loc != null) {
				this.linkTile(sr.getTileEntityFrom(dir), dir);
			}
		}
	}

	protected void onJoinNetwork() {}

	public abstract boolean canNetworkOnSide(ForgeDirection dir);

	public final WireNetwork getNetwork() {
		return network;
	}

	public final void setNetwork(WireNetwork n) {
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

	public abstract int getCurrentLimit();

	public abstract void overCurrent();

	@Override
	public final World getWorld() {
		return worldObj;
	}

	@Override
	public final int getX() {
		return xCoord;
	}

	@Override
	public final int getY() {
		return yCoord;
	}

	@Override
	public final int getZ() {
		return zCoord;
	}

}
