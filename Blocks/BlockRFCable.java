/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ElectriCraft.Blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.ElectriCraft.ElectriCraft;
import Reika.ElectriCraft.Base.BlockElectriCable;
import Reika.ElectriCraft.Registry.ElectriTiles;
import Reika.ElectriCraft.TileEntities.ModInterface.TileEntityRFCable;

public class BlockRFCable extends BlockElectriCable {

	public BlockRFCable(Material par2Material) {
		super(par2Material);
	}

	@Override
	public TileEntity createTileEntity(World world, int meta) {
		return new TileEntityRFCable();
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer ep, int s, float a, float b, float c) {
		ItemStack is = ep.getCurrentEquippedItem();
		if (!ReikaItemHelper.matchStacks(is, ElectriTiles.CABLE.getCraftedProduct())) {
			ep.openGui(ElectriCraft.instance, 0, world, x, y, z);
			return true;
		}
		return false;
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block oldid, int oldmeta) {
		TileEntityRFCable te = (TileEntityRFCable)world.getTileEntity(x, y, z);
		if (!world.isRemote)
			te.removeFromNetwork();
		super.breakBlock(world, x, y, z, oldid, oldmeta);
	}

	@Override
	protected String getTextureIcoName() {
		return "rf";
	}

	@Override
	public ElectriTiles getTile() {
		return ElectriTiles.CABLE;
	}

}
