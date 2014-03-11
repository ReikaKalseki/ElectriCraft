/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ElectroCraft.Base;

import java.util.ArrayList;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Base.TileEntityBase;
import Reika.DragonAPI.Interfaces.RenderFetcher;
import Reika.DragonAPI.Interfaces.TextureFetcher;
import Reika.RotaryCraft.API.ShaftMachine;
import Reika.RotaryCraft.API.Transducerable;
import Reika.RotaryCraft.Registry.ConfigRegistry;
import Reika.ElectroCraft.Auxiliary.ElectroRenderList;
import Reika.ElectroCraft.Registry.ElectroTiles;

public abstract class ElectroTileEntity extends TileEntityBase implements RenderFetcher, Transducerable {

	protected ForgeDirection[] dirs = ForgeDirection.values();

	public float phi;

	public final TextureFetcher getRenderer() {
		if (ElectroTiles.TEList[this.getIndex()].hasRender())
			return ElectroRenderList.getRenderForMachine(ElectroTiles.TEList[this.getIndex()]);
		else
			return null;
	}

	@Override
	public int getTileEntityBlockID() {
		return ElectroTiles.TEList[this.getIndex()].getBlockID();
	}

	public ElectroTiles getMachine() {
		return ElectroTiles.TEList[this.getIndex()];
	}

	@Override
	protected String getTEName() {
		return ElectroTiles.TEList[this.getIndex()].getName();
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
		ElectroTiles r = ElectroTiles.TEList[this.getIndex()];
		return pass == 0 || ((r.renderInPass1() || this instanceof ShaftMachine) && pass == 1);
	}

	public ArrayList<String> getMessages(World world, int x, int y, int z, int side) {
		ArrayList<String> li = new ArrayList();

		return li;
	}

	@Override
	public final int getPacketDelay() {
		return DragonAPICore.isSinglePlayer() ? 1 : Math.min(20, ConfigRegistry.PACKETDELAY.getValue());
	}
}
