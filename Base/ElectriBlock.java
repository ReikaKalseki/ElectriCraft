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
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.ElectriCraft.ElectriCraft;
import Reika.ElectriCraft.Registry.ElectriBlocks;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

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

	@Override
	@SideOnly(Side.CLIENT)
	public final boolean addBlockDestroyEffects(World world, int x, int y, int z, int meta, EffectRenderer eff)
	{
		if (blockID == ElectriBlocks.WIRE.getBlockID() || blockID == ElectriBlocks.CABLE.getBlockID())
			return super.addBlockDestroyEffects(world, x, y, z, meta, eff);
		return ReikaRenderHelper.addModelledBlockParticles("/Reika/ElectriCraft/Textures/", world, x, y, z, this, eff, ReikaJavaLibrary.makeListFrom(new double[]{0,0,1,1}), ElectriCraft.class);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public final boolean addBlockHitEffects(World world, MovingObjectPosition tg, EffectRenderer eff)
	{
		if (blockID == ElectriBlocks.WIRE.getBlockID() || blockID == ElectriBlocks.CABLE.getBlockID())
			return super.addBlockHitEffects(world, tg, eff);
		return ReikaRenderHelper.addModelledBlockParticles("/Reika/ElectriCraft/Textures/", world, tg, this, eff, ReikaJavaLibrary.makeListFrom(new double[]{0,0,1,1}), ElectriCraft.class);
	}

}
