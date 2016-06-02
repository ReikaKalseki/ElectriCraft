/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ElectriCraft.Blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import Reika.ElectriCraft.Base.BlockElectriCable;
import Reika.ElectriCraft.Registry.ElectriTiles;
import Reika.ElectriCraft.TileEntities.ModInterface.TileEntityEUCable;

public class BlockEUCable extends BlockElectriCable {

	public BlockEUCable(Material par2Material) {
		super(par2Material);
	}

	@Override
	public TileEntity createTileEntity(World world, int meta) {
		return new TileEntityEUCable();
	}
	/*
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer ep, int s, float a, float b, float c) {
		ItemStack is = ep.getCurrentEquippedItem();
		if (!ReikaItemHelper.matchStacks(is, ElectriTiles.CABLE.getCraftedProduct())) {
			ep.openGui(ElectriCraft.instance, 0, world, x, y, z);
			return true;
		}
		return false;
	}
	 */
	@Override
	public void breakBlock(World world, int x, int y, int z, Block oldid, int oldmeta) {
		TileEntityEUCable te = (TileEntityEUCable)world.getTileEntity(x, y, z);
		super.breakBlock(world, x, y, z, oldid, oldmeta);
	}

	@Override
	protected String getTextureIcoName() {
		return "eu";
	}

	@Override
	public ElectriTiles getTile() {
		return ElectriTiles.EUCABLE;
	}

}
