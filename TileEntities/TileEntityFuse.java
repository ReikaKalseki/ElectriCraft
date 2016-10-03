/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ElectriCraft.TileEntities;

import java.util.ArrayList;

import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import Reika.DragonAPI.APIPacketHandler.PacketIDs;
import Reika.DragonAPI.DragonAPIInit;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.ElectriCraft.Auxiliary.WireFuse;
import Reika.ElectriCraft.Base.TileEntityWireComponent;
import Reika.ElectriCraft.Registry.ElectriTiles;
import Reika.RotaryCraft.Auxiliary.Interfaces.NBTMachine;

public class TileEntityFuse extends TileEntityWireComponent implements WireFuse, NBTMachine {

	private boolean overloaded;

	private int currentLimit;

	public static final int[] TIERS = {
		32,
		128,
		1024,
		8192
	};

	public TileEntityFuse() {

	}

	public TileEntityFuse(int limit) {
		currentLimit = limit;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);
	}

	@Override
	public int getResistance() {
		return !this.isOverloaded() ? 0 : Integer.MAX_VALUE;
	}

	@Override
	public int getMaxCurrent() {
		return currentLimit;
	}

	@Override
	public ElectriTiles getMachine() {
		return ElectriTiles.FUSE;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	public boolean isOverloaded() {
		return overloaded;
	}

	@Override
	public float getHeight() {
		return 0.625F;
	}

	@Override
	public float getWidth() {
		return 0.75F;
	}

	@Override
	public boolean canConnect() {
		return !this.isOverloaded();
	}

	@Override
	public void overload(int current) {
		overloaded = true;
		ReikaSoundHelper.playBreakSound(worldObj, xCoord, yCoord, zCoord, Blocks.glass);
		ReikaSoundHelper.playSoundAtBlock(worldObj, xCoord, yCoord, zCoord, "random.fizz", 2, 1);
		ReikaPacketHelper.sendDataPacketWithRadius(DragonAPIInit.packetChannel, PacketIDs.COLOREDPARTICLE.ordinal(), this, 24, 1, 1, 1, 32, 0);
	}

	@Override
	public void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		overloaded = NBT.getBoolean("overload");
		currentLimit = NBT.getInteger("limit");
	}

	@Override
	public void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);


		NBT.setBoolean("overload", overloaded);
		NBT.setInteger("limit", currentLimit);
	}

	@Override
	public boolean shouldRenderInPass(int pass) {
		return pass <= 1;
	}

	@Override
	public NBTTagCompound getTagsToWriteToStack() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("currentlim", currentLimit);
		return tag;
	}

	@Override
	public void setDataFromItemStackTag(NBTTagCompound NBT) {
		if (NBT != null) {
			currentLimit = NBT.getInteger("currentlim");
		}
	}

	@Override
	public ArrayList<NBTTagCompound> getCreativeModeVariants() {
		ArrayList<NBTTagCompound> li = new ArrayList();
		for (int i = 0; i < TIERS.length; i++) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setInteger("currentlim", TIERS[i]);
			li.add(tag);
		}
		return li;
	}

	@Override
	public ArrayList<String> getDisplayTags(NBTTagCompound NBT) {
		ArrayList<String> li = new ArrayList();
		if (NBT != null) {
			li.add("Current Limit: "+NBT.getInteger("currentlim")+"A");
		}
		return li;
	}

}
