/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ElectriCraft.Base;

import java.util.ArrayList;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Base.TileEntityBase;
import Reika.DragonAPI.Interfaces.RenderFetcher;
import Reika.DragonAPI.Interfaces.TextureFetcher;
import Reika.ElectriCraft.Auxiliary.ElectriRenderList;
import Reika.ElectriCraft.Registry.ElectriTiles;
import Reika.ElectriCraft.TileEntities.TileEntityWire;
import Reika.RotaryCraft.API.ShaftMachine;
import Reika.RotaryCraft.API.Transducerable;
import Reika.RotaryCraft.Registry.ConfigRegistry;

public abstract class ElectriTileEntity extends TileEntityBase implements RenderFetcher, Transducerable {

	protected ForgeDirection[] dirs = ForgeDirection.values();

	public float phi;

	public final TextureFetcher getRenderer() {
		if (ElectriTiles.TEList[this.getIndex()].hasRender())
			return ElectriRenderList.getRenderForMachine(ElectriTiles.TEList[this.getIndex()]);
		else
			return null;
	}

	@Override
	public int getTileEntityBlockID() {
		return ElectriTiles.TEList[this.getIndex()].getBlockID();
	}

	public ElectriTiles getMachine() {
		return ElectriTiles.TEList[this.getIndex()];
	}

	@Override
	protected String getTEName() {
		return ElectriTiles.TEList[this.getIndex()].getName();
	}

	public abstract int getIndex();

	public int getTextureState(ForgeDirection side) {
		return 0;
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT)
	{
		super.writeSyncTag(NBT);

		NBT.setFloat("ang", phi);
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT)
	{
		super.readSyncTag(NBT);

		phi = NBT.getFloat("ang");
	}

	public boolean isThisTE(int id, int meta) {
		return id == this.getTileEntityBlockID() && meta == this.getIndex();
	}

	public final String getName() {
		return this.getTEName();
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
		return li;
	}

	@Override
	public final int getPacketDelay() {
		return DragonAPICore.isSinglePlayer() ? 1 : Math.min(20, ConfigRegistry.PACKETDELAY.getValue());
	}
}
