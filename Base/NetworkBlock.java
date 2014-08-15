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

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;

public abstract class NetworkBlock extends Block {

	public NetworkBlock(Material par2Material) {
		super(par2Material);
		this.setHardness(2F);
		this.setResistance(10F);
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block oldid, int oldmeta) {
		NetworkTileEntity te = (NetworkTileEntity)world.getTileEntity(x, y, z);
		if (!world.isRemote && te != null)
			te.removeFromNetwork();
		super.breakBlock(world, x, y, z, oldid, oldmeta);
	}

}