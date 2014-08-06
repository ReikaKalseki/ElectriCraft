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

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.ElectriCraft.Auxiliary.ConversionTile;
import Reika.ElectriCraft.Base.ElectricalReceiver;
import Reika.ElectriCraft.Network.WireNetwork;
import Reika.ElectriCraft.Registry.ElectriTiles;
import Reika.RotaryCraft.API.Screwdriverable;
import Reika.RotaryCraft.API.ShaftPowerEmitter;
import Reika.RotaryCraft.API.ShaftPowerReceiver;
import Reika.RotaryCraft.Registry.EngineType;
import Reika.RotaryCraft.Registry.SoundRegistry;

public class TileEntityMotor extends ElectricalReceiver implements Screwdriverable, ShaftPowerEmitter, ConversionTile {

	private static final int soundtime = (int)(EngineType.DC.getSoundLength()*2.04F);
	private StepTimer soundTimer = new StepTimer(soundtime);

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

		if (!world.isRemote && network != null) {
			torque = network.getTerminalCurrent(this)*WireNetwork.TORQUE_PER_AMP;
			omega = network.getTerminalVoltage(this)/WireNetwork.TORQUE_PER_AMP;
		}
		power = (long)omega*(long)torque;
		if (power > 0) {
			soundTimer.update();
			if (soundTimer.checkCap())
				SoundRegistry.ELECTRIC.playSoundAtBlock(world, x, y, z, this.getSoundVolume(world, x, y, z), 0.333F);
		}
		else {
			torque = omega = 0;
		}
		TileEntity tg = this.getAdjacentTileEntity(this.getFacing().getOpposite());
		if (tg instanceof ShaftPowerReceiver) {
			ShaftPowerReceiver rec = (ShaftPowerReceiver)tg;
			rec.setOmega(omega);
			rec.setTorque(torque);
			rec.setPower(power);
		}
	}

	private float getSoundVolume(World world, int x, int y, int z) {
		if (world.getBlockId(x, y-1, z) == Block.cloth.blockID && world.getBlockId(x, y+1, z) == Block.cloth.blockID)
			return 0.1F;
		ForgeDirection dir = this.getFacing();
		ForgeDirection dir2 = dir.getOpposite();
		for (int i = 0; i < 6; i++) {
			ForgeDirection side = dirs[i];
			if (side != dir && side != dir2 && side != ForgeDirection.DOWN) {
				int dx = x+side.offsetX;
				int dy = y+side.offsetY;
				int dz = z+side.offsetZ;
				int id = world.getBlockId(dx, dy, dz);
				if (id != Block.cloth.blockID)
					return 0.36F;
			}
		}
		return 0.1F;
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
	public int getCurrentLimit() {
		return 0;
	}

	@Override
	public void overCurrent() {

	}

	@Override
	public void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		facing = dirs[NBT.getInteger("face")];

		omega = NBT.getInteger("omg");
		torque = NBT.getInteger("tq");
		power = NBT.getLong("pwr");
	}

	@Override
	public void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBT.setInteger("face", this.getFacing().ordinal());

		NBT.setInteger("omg", omega);
		NBT.setInteger("tq", torque);
		NBT.setLong("pwr", power);
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
	public ElectriTiles getMachine() {
		return ElectriTiles.MOTOR;
	}

	@Override
	public boolean canNetworkOnSide(ForgeDirection dir) {
		return dir == this.getFacing();
	}

	@Override
	public boolean canWriteTo(ForgeDirection from) {
		ForgeDirection dir = this.getFacing().getOpposite();
		return dir == from;
	}

	@Override
	public boolean isEmitting() {
		return true;
	}

	@Override
	public boolean canReceivePowerFromSide(ForgeDirection dir) {
		return this.canNetworkOnSide(dir);
	}

	@Override
	public boolean canReceivePower() {
		return true;
	}

	@Override
	public int getEmittingX() {
		return xCoord+this.getFacing().getOpposite().offsetX;
	}

	@Override
	public int getEmittingY() {
		return yCoord+this.getFacing().getOpposite().offsetY;
	}

	@Override
	public int getEmittingZ() {
		return zCoord+this.getFacing().getOpposite().offsetZ;
	}

	@Override
	public long getMaxPower() {
		return power;
	}

	@Override
	public long getCurrentPower() {
		return power;
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
}
