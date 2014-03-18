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
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.ElectriCraft.Auxiliary.ConversionTile;
import Reika.ElectriCraft.Base.ElectricalReceiver;
import Reika.ElectriCraft.Network.WireNetwork;
import Reika.ElectriCraft.Registry.ElectriTiles;
import Reika.RotaryCraft.API.ShaftPowerEmitter;
import Reika.RotaryCraft.Registry.EngineType;
import Reika.RotaryCraft.Registry.SoundRegistry;

public class TileEntityMotor extends ElectricalReceiver implements ShaftPowerEmitter, ConversionTile {

	private StepTimer soundTimer = new StepTimer(EngineType.DC.getSoundLength(0, 2.04F));

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

		if (!world.isRemote) {
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
	}

	private float getSoundVolume(World world, int x, int y, int z) {
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
		return 0.125F;
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
	public void animateWithTick(World world, int x, int y, int z) {
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
	public boolean canWriteToBlock(int x, int y, int z) {
		ForgeDirection dir = this.getFacing().getOpposite();
		return x == xCoord+dir.offsetX && y == yCoord && z == zCoord+dir.offsetZ;
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
}
