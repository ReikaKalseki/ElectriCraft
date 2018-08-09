/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ElectriCraft.TileEntities;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.API.Interfaces.WorldRift;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaArrayHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaParticleHelper;
import Reika.ElectriCraft.API.WrappableWireSource;
import Reika.ElectriCraft.Auxiliary.WrappedSource;
import Reika.ElectriCraft.Auxiliary.Interfaces.Overloadable;
import Reika.ElectriCraft.Auxiliary.Interfaces.WireEmitter;
import Reika.ElectriCraft.Auxiliary.Interfaces.WireReceiver;
import Reika.ElectriCraft.Base.WiringTile;
import Reika.ElectriCraft.Blocks.BlockWire;
import Reika.ElectriCraft.Registry.ElectriTiles;
import Reika.ElectriCraft.Registry.WireType;

public class TileEntityWire extends WiringTile implements Overloadable {

	private boolean[] connections = new boolean[6];

	public boolean insulated;

	private boolean shouldMelt;

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);

		if (shouldMelt)
			this.melt(world, x, y, z);
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public ElectriTiles getMachine() {
		return ElectriTiles.WIRE;
	}

	@Override
	protected void onJoinNetwork() {
		this.checkForWrappables();
	}

	@Override
	public void onNetworkChanged() {
		super.onNetworkChanged();
		this.checkForWrappables();
	}

	public boolean isConnectedOnSideAt(World world, int x, int y, int z, ForgeDirection dir) {
		dir = dir.offsetX == 0 ? dir.getOpposite() : dir;
		int dx = x+dir.offsetX;
		int dy = y+dir.offsetY;
		int dz = z+dir.offsetZ;
		Block b = world.getBlock(dx, dy, dz);
		int meta = world.getBlockMetadata(dx, dy, dz);
		TileEntity te = world.getTileEntity(dx, dy, dz);
		if (b == this.getTileEntityBlockID())
			return true;//connections[dir.ordinal()] && ((TileEntityWire)te).connections[dir.getOpposite().ordinal()];
		boolean flag = false;
		if (te instanceof WiringTile) {
			flag = flag || ((WiringTile) te).canNetworkOnSide(dir.getOpposite());
		}
		if (te instanceof WireEmitter) {
			flag = flag || ((WireEmitter)te).canEmitPowerToSide(dir.getOpposite());
		}
		if (te instanceof WireReceiver) {
			flag = flag || ((WireReceiver)te).canReceivePowerFromSide(dir.getOpposite());
		}
		if (te instanceof WorldRift) {
			flag = true;
		}
		if (te instanceof WrappableWireSource) {
			flag = flag || ((WrappableWireSource) te).canConnectToSide(dir.getOpposite());
		}
		return flag;
	}

	@Override
	public final boolean canNetworkOnSide(ForgeDirection dir) {
		return true;//connections[dir.ordinal()];
	}

	public WireType getWireType() {
		int meta = this.isInWorld() ? worldObj.getBlockMetadata(xCoord, yCoord, zCoord) : this.getBlockMetadata();
		return WireType.wireList[meta];
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		connections = ReikaArrayHelper.booleanFromByteBitflags(NBT.getByte("conn"), 6);

		insulated = NBT.getBoolean("insul");

		shouldMelt = NBT.getBoolean("melt");
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBT.setByte("conn", ReikaArrayHelper.booleanToByteBitflags(connections));

		NBT.setBoolean("insul", insulated);

		NBT.setBoolean("melt", shouldMelt);
	}

	@Override
	public final AxisAlignedBB getRenderBoundingBox() {
		return AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord+1, yCoord+1, zCoord+1);
	}

	public boolean isConnectionValidForSide(ForgeDirection dir) {
		if (dir.offsetX == 0 && MinecraftForgeClient.getRenderPass() != 1)
			dir = dir.getOpposite();
		return connections[dir.ordinal()];
	}

	public void recomputeConnections(World world, int x, int y, int z) {
		boolean clear = false;
		for (int i = 0; i < 6; i++) {
			boolean flag = this.isConnected(dirs[i]);
			if (flag != connections[i]) {
				clear = true;
			}
			connections[i] = flag;
			world.func_147479_m(x+dirs[i].offsetX, y+dirs[i].offsetY, z+dirs[i].offsetZ);
		}
		this.checkForWrappables();
		//world.markBlockForUpdate(x, y, z);
		world.func_147479_m(x, y, z);
		if (clear) {
			if (network != null)
				network.removeElement(this);
			//this.findAndJoinNetwork(world, x, y, z);
		}
	}

	public void checkRiftConnections() {
		if (network != null)
			network.checkRiftConnections();
	}

	private void checkForWrappables() {
		if (network == null)
			return;
		for (int i = 0; i < 6; i++) {
			TileEntity te = this.getAdjacentTileEntity(dirs[i]);
			if (te instanceof WrappableWireSource) {
				network.addElement(new WrappedSource((WrappableWireSource)te));
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
				TileEntityWire te = (TileEntityWire)world.getTileEntity(dx, dy, dz);
				te.connections[dir.getOpposite().ordinal()] = true;//te.isConnected(dir.getOpposite());
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
			return true;//connectionCount < 3;
		//certain TEs
		return false;
	}

	@Override
	public int getMaxCurrent() {
		return this.getWireType().maxCurrent;
	}

	public void melt(World world, int x, int y, int z) {
		ReikaSoundHelper.playSoundAtBlock(world, x, y, z, "random.fizz");
		ReikaParticleHelper.LAVA.spawnAroundBlock(world, x, y, z, 12);
		world.setBlock(x, y, z, Blocks.flowing_lava);
	}

	@Override
	public void overload(int current) {
		shouldMelt = true;
	}

	public IIcon getEndIcon() {
		return BlockWire.getEndTexture(this.getWireType());
	}

	public IIcon getCenterIcon() {
		return BlockWire.getTexture(this.getWireType());
	}

	public IIcon getInsulatedCenterIcon() {
		return BlockWire.getInsulatedTexture(this.getWireType());
	}

	public IIcon getInsulatedEndIcon() {
		return BlockWire.getInsulatedEndTexture(this.getWireType());
	}

	@Override
	public int getResistance() {
		return this.getWireType().resistance;
	}
}
