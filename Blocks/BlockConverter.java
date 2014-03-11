/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ElectriCraft.Blocks;

import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import Reika.ElectriCraft.Base.ElectriBlock;
import Reika.ElectriCraft.Registry.ElectriTiles;

public class BlockConverter extends ElectriBlock {

	public BlockConverter(int par1, Material mat) {
		super(par1, mat);
	}

	@Override
	public TileEntity createTileEntity(World world, int meta) {
		return ElectriTiles.createTEFromIDAndMetadata(blockID, meta);
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z)
	{
		return ElectriTiles.getTE(world, x, y, z).getCraftedProduct();
	}

}
