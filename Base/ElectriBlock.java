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

import net.minecraft.block.material.Material;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public abstract class ElectriBlock extends NetworkBlock {

	public ElectriBlock(int par1, Material par2Material) {
		super(par1, par2Material);
	}

	@Override
	public boolean hasTileEntity(int meta) {
		return true;
	}

	@Override
	public final boolean isOpaqueCube() {
		return false;
	}

	@Override
	public final boolean renderAsNormalBlock()
	{
		return false;
	}

	@Override
	public int getRenderType() {
		return -1;
	}

	@Override
	public final AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z)
	{
		return this.getCollisionBoundingBoxFromPool(world, x, y, z);
	}

}
