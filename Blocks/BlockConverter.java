/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ElectroCraft.Blocks;

import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import Reika.ElectroCraft.Base.ElectroBlock;
import Reika.ElectroCraft.Registry.ElectroTiles;

public class BlockConverter extends ElectroBlock {

	public BlockConverter(int par1, Material mat) {
		super(par1, mat);
	}

	@Override
	public TileEntity createTileEntity(World world, int meta) {
		return ElectroTiles.createTEFromIDAndMetadata(blockID, meta);
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z)
	{
		return ElectroTiles.getTE(world, x, y, z).getCraftedProduct();
	}

}
