/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.RotationalInduction.Blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import Reika.RotationalInduction.Induction;

public class BlockInductionOre extends Block {

	public BlockInductionOre(int par1, Material par2Material) {
		super(par1, par2Material);
		this.setCreativeTab(Induction.tabInduction);
		this.setResistance(3F);
		this.setHardness(2.5F);
	}

}
