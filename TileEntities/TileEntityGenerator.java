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

import java.util.Collection;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.ElectriCraft.Auxiliary.Interfaces.ConversionTile;
import Reika.ElectriCraft.Base.ElectricalEmitter;
import Reika.ElectriCraft.Network.WireNetwork;
import Reika.ElectriCraft.Registry.ElectriTiles;
import Reika.RotaryCraft.API.Interfaces.Screwdriverable;
import Reika.RotaryCraft.API.Power.PowerTransferHelper;
import Reika.RotaryCraft.API.Power.ShaftMerger;
import Reika.RotaryCraft.API.Power.ShaftPowerReceiver;
import Reika.RotaryCraft.Auxiliary.PowerSourceList;
import Reika.RotaryCraft.Auxiliary.Interfaces.PowerSourceTracker;

public class TileEntityGenerator extends ElectricalEmitter implements Screwdriverable, ShaftPowerReceiver, ConversionTile, PowerSourceTracker {

	private int lastomega;
	private int lasttorque;

	protected int omega;
	protected int torque;
	protected long power;

	protected int iotick;

	private ForgeDirection facing;

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);

		if (iotick > 0)
			iotick -= 8;

		if (!PowerTransferHelper.checkPowerFrom(this, this.getFacing())) {
			this.noInputMachine();
		}

		if (power == 0 || omega == 0 || torque == 0) {
			power = 0;
			omega = torque = 0;
		}

		if (power > 400e6 && !world.isRemote) {
			this.delete();
			world.newExplosion(null, x+0.5, y+0.5, z+0.5, 4, true, true);
		}

		//ReikaJavaLibrary.pConsole(network, Side.SERVER);
		if (!world.isRemote && network != null) {
			//ReikaJavaLibrary.pConsole(omega);
			if (omega != lastomega || torque != lasttorque) {
				network.updateWires();
			}
		}

		lastomega = omega;
		lasttorque = torque;
	}

	public final ForgeDirection getFacing() {
		return facing != null ? facing : ForgeDirection.EAST;
	}

	public void setFacing(ForgeDirection dir) {
		facing = dir;
	}

	@Override
	public final int getOmega() {
		return omega;
	}

	@Override
	public final int getTorque() {
		return torque;
	}

	@Override
	public final long getPower() {
		return power;
	}

	@Override
	public final int getIORenderAlpha() {
		return iotick;
	}

	@Override
	public final void setIORenderAlpha(int io) {
		iotick = io;
	}

	@Override
	public void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		facing = dirs[NBT.getInteger("face")];

		omega = NBT.getInteger("omg");
		torque = NBT.getInteger("tq");
		power = NBT.getLong("pwr");

		iotick = NBT.getInteger("io");
	}

	@Override
	public void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBT.setInteger("face", this.getFacing().ordinal());

		NBT.setInteger("omg", omega);
		NBT.setInteger("tq", torque);
		NBT.setLong("pwr", power);

		NBT.setInteger("io", iotick);
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {
		if (!this.isInWorld()) {
			phi = 0;
			return;
		}
		phi += ReikaMathLibrary.doubpow(ReikaMathLibrary.logbase(omega+1, 2), 1.05);
	}

	@Override
	public ElectriTiles getTile() {
		return ElectriTiles.GENERATOR;
	}

	@Override
	public int getGenVoltage() {
		return omega*WireNetwork.TORQUE_PER_AMP;
	}

	@Override
	public int getGenCurrent() {
		return torque/WireNetwork.TORQUE_PER_AMP;
	}

	@Override
	public boolean canNetworkOnSide(ForgeDirection dir) {
		return dir == this.getFacing().getOpposite();
	}

	@Override
	public void setOmega(int omega) {
		this.omega = omega;
	}

	@Override
	public void setTorque(int torque) {
		this.torque = torque;
	}

	@Override
	public void setPower(long power) {
		this.power = power;
	}

	@Override
	public boolean canReadFrom(ForgeDirection from) {
		ForgeDirection dir = this.getFacing();
		return from == dir;
	}

	@Override
	public boolean isReceiving() {
		return true;
	}

	@Override
	public void noInputMachine() {
		omega = torque = 0;
		power = 0;
	}

	@Override
	public boolean canEmitPowerToSide(ForgeDirection dir) {
		return this.canNetworkOnSide(dir);
	}

	@Override
	public boolean onShiftRightClick(World world, int x, int y, int z, ForgeDirection side) {
		return false;
	}

	@Override
	public boolean onRightClick(World world, int x, int y, int z, ForgeDirection side) {
		this.incrementFacing();
		return true;
	}

	protected void incrementFacing() {
		int o = this.getFacing().ordinal();
		if (o == 5)
			this.setFacing(dirs[2]);
		else
			this.setFacing(dirs[o+1]);
		this.rebuildNetwork();
	}

	@Override
	public boolean canEmitPower() {
		return true;
	}

	@Override
	public int getMinTorque(int available) {
		return torque;
	}

	@Override
	public PowerSourceList getPowerSources(PowerSourceTracker io, ShaftMerger caller) {
		ForgeDirection dir = this.getFacing();
		return PowerSourceList.getAllFrom(worldObj, dir, xCoord+dir.offsetX, yCoord+dir.offsetY, zCoord+dir.offsetZ, this, caller);
	}

	@Override
	public int getIoOffsetX() {
		return 0;
	}

	@Override
	public int getIoOffsetY() {
		return 0;
	}

	@Override
	public int getIoOffsetZ() {
		return 0;
	}

	@Override
	public void getAllOutputs(Collection<TileEntity> c, ForgeDirection dir) {

	}
}
