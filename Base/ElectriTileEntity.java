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

import java.util.ArrayList;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Base.TileEntityRegistryBase;
import Reika.DragonAPI.Interfaces.TextureFetcher;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.ElectriCraft.Auxiliary.ElectriRenderList;
import Reika.ElectriCraft.Registry.ElectriTiles;
import Reika.ElectriCraft.TileEntities.TileEntityBattery;
import Reika.ElectriCraft.TileEntities.TileEntityWire;
import Reika.RotaryCraft.API.Interfaces.Transducerable;
import Reika.RotaryCraft.API.Power.ShaftMachine;

import li.cil.oc.api.network.Visibility;

public abstract class ElectriTileEntity extends TileEntityRegistryBase<ElectriTiles> implements RenderFetcher, Transducerable {

	protected ForgeDirection[] dirs = ForgeDirection.values();

	public float phi;

	public boolean isFlipped = false;

	public final TextureFetcher getRenderer() {
		if (ElectriTiles.TEList[this.getIndex()].hasRender())
			return ElectriRenderList.getRenderForMachine(ElectriTiles.TEList[this.getIndex()]);
		else
			return null;
	}

	@Override
	public final boolean allowTickAcceleration() {
		return false;
	}

	public abstract ElectriTiles getTile();

	public int getTextureState(ForgeDirection side) {
		return 0;
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);
		NBT.setBoolean("flip", isFlipped);
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);
		isFlipped = NBT.getBoolean("flip");
	}

	@Override
	public boolean shouldRenderInPass(int pass) {
		ElectriTiles r = ElectriTiles.TEList[this.getIndex()];
		return pass == 0 || ((r.renderInPass1() || this instanceof ShaftMachine) && pass == 1);
	}

	public final ArrayList<String> getMessages(World world, int x, int y, int z, int side) {
		ArrayList<String> li = new ArrayList();
		if (this instanceof TileEntityWire) {
			TileEntityWire wire = (TileEntityWire)this;
			li.add(String.format("Point Voltage: %dV", wire.getWireVoltage()));
			li.add(String.format("Point Current: %dA", wire.getWireCurrent()));
		}
		if (this instanceof TileEntityBattery) {
			TileEntityBattery b = (TileEntityBattery)this;
			double max = b.getMaxEnergy();
			li.add(String.format("Stored Energy: %s/%s", b.getDisplayEnergy(), b.getBatteryType().getFormattedCapacity()));
		}
		return li;
	}

	@Override
	public int getRedstoneOverride() {
		return 0;
	}

	@Override
	@ModDependent(ModList.OPENCOMPUTERS)
	public final Visibility getOCNetworkVisibility() {
		return this.getTile().isWiring() ? Visibility.None : Visibility.Network;
	}
}
