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

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.DragonAPI.APIPacketHandler.PacketIDs;
import Reika.DragonAPI.DragonAPIInit;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaParticleHelper;
import Reika.ElectriCraft.Auxiliary.ConversionTile;
import Reika.ElectriCraft.Base.ElectricalReceiver;
import Reika.ElectriCraft.Network.WireNetwork;
import Reika.ElectriCraft.Registry.ElectriTiles;
import Reika.RotaryCraft.API.Interfaces.Screwdriverable;
import Reika.RotaryCraft.API.Power.ShaftMerger;
import Reika.RotaryCraft.API.Power.ShaftPowerReceiver;
import Reika.RotaryCraft.Auxiliary.ItemStacks;
import Reika.RotaryCraft.Auxiliary.PowerSourceList;
import Reika.RotaryCraft.Auxiliary.ShaftPowerEmitter;
import Reika.RotaryCraft.Auxiliary.Interfaces.NBTMachine;
import Reika.RotaryCraft.Auxiliary.Interfaces.PowerSourceTracker;
import Reika.RotaryCraft.Registry.ConfigRegistry;
import Reika.RotaryCraft.Registry.EngineType;
import Reika.RotaryCraft.Registry.SoundRegistry;

public class TileEntityMotor extends ElectricalReceiver implements Screwdriverable, ShaftPowerEmitter, ConversionTile, NBTMachine, PowerSourceTracker {

	private static final int soundtime = (int)(EngineType.DC.getSoundLength()*2.04F);
	private StepTimer soundTimer = new StepTimer(soundtime);

	protected int omega;
	protected int torque;
	protected long power;

	protected int iotick;

	private ForgeDirection facing;

	private int maxAmp = 1;

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);

		if (iotick > 0)
			iotick -= 8;

		if (!world.isRemote && network != null) {
			torque = this.getEffectiveCurrent(world, x, y, z)*WireNetwork.TORQUE_PER_AMP;
			omega = network.getTerminalVoltage(this)/WireNetwork.TORQUE_PER_AMP;
		}
		power = (long)omega*(long)torque;
		if (power > 0) {
			soundTimer.update();
			if (soundTimer.checkCap())
				SoundRegistry.ELECTRIC.playSoundAtBlock(world, x, y, z, this.getTrueVolume(world, x, y, z), 0.333F);
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

	private int getEffectiveCurrent(World world, int x, int y, int z) {
		int in = network.getTerminalCurrent(this);
		int max = maxAmp*network.getAverageCurrent(this);
		if (network.getNumberSourcesPer(this) > maxAmp) { //was (in > max)
			if (rand.nextInt(10) == 0) {
				ReikaSoundHelper.playSoundAtBlock(world, x, y, z, "random.fizz");
				//ReikaParticleHelper.SMOKE.spawnAroundBlock(world, x, y, z, 2);
				ReikaPacketHelper.sendDataPacket(DragonAPIInit.packetChannel, PacketIDs.PARTICLE.ordinal(), this, ReikaParticleHelper.SMOKE.ordinal(), 1);
			}
		}
		return Math.min(in, max);
	}

	public boolean upgrade(ItemStack is) {
		boolean flag = false;
		if (ReikaItemHelper.matchStacks(is, ItemStacks.redgoldingot) && maxAmp < 2) {
			maxAmp = 2;
			flag = true;
		}
		else if (ReikaItemHelper.matchStacks(is, ItemStacks.tungsteningot) && maxAmp < 4) {
			maxAmp = 4;
			flag = true;
		}

		if (flag) {
			if (network != null) {
				network.updateWires();
			}
		}
		return flag;
	}

	private float getTrueVolume(World world, int x, int y, int z) {
		return ConfigRegistry.ENGINEVOLUME.getFloat()*this.getSoundVolume(world, x, y, z);
	}

	private float getSoundVolume(World world, int x, int y, int z) {
		if (world.getBlock(x, y-1, z) == Blocks.wool && world.getBlock(x, y+1, z) == Blocks.wool)
			return 0.1F;
		ForgeDirection dir = this.getFacing();
		ForgeDirection dir2 = dir.getOpposite();
		for (int i = 0; i < 6; i++) {
			ForgeDirection side = dirs[i];
			if (side != dir && side != dir2 && side != ForgeDirection.DOWN) {
				int dx = x+side.offsetX;
				int dy = y+side.offsetY;
				int dz = z+side.offsetZ;
				Block id = world.getBlock(dx, dy, dz);
				if (id != Blocks.wool)
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

		maxAmp = NBT.getInteger("amp");
	}

	@Override
	public void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBT.setInteger("face", this.getFacing().ordinal());

		NBT.setInteger("omg", omega);
		NBT.setInteger("tq", torque);
		NBT.setLong("pwr", power);

		NBT.setInteger("amp", maxAmp);
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

	@Override
	public NBTTagCompound getTagsToWriteToStack() {
		if (maxAmp > 1) {
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setInteger("amp", maxAmp);
			return nbt;
		}
		return null;
	}

	@Override
	public void setDataFromItemStackTag(NBTTagCompound NBT) {
		if (NBT != null && NBT.hasKey("amp"))
			maxAmp = NBT.getInteger("amp");
	}

	@Override
	public ArrayList<NBTTagCompound> getCreativeModeVariants() {
		ArrayList<NBTTagCompound> li = new ArrayList();
		NBTTagCompound NBT = new NBTTagCompound();
		NBT.setInteger("amp", 4);
		li.add(NBT);
		return li;
	}

	@Override
	public ArrayList<String> getDisplayTags(NBTTagCompound NBT) {
		ArrayList<String> li = new ArrayList();
		if (NBT != null && NBT.hasKey("amp")) {
			int amp = NBT.getInteger("amp");
			li.add(String.format("Contains a %dx amplifier", amp));
		}
		return li;
	}

	@Override
	public PowerSourceList getPowerSources(PowerSourceTracker io, ShaftMerger caller) {
		return network != null ? network.getInputSources(io, caller) : new PowerSourceList();
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
}
