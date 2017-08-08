/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ElectriCraft.Base;

import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import Reika.DragonAPI.Interfaces.Block.MachineRegistryBlock;
import Reika.DragonAPI.Interfaces.Registry.TileEnum;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.ElectriCraft.ElectriCraft;
import Reika.ElectriCraft.Registry.ElectriBlocks;
import Reika.ElectriCraft.Registry.ElectriTiles;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class ElectriBlock extends NetworkBlock implements MachineRegistryBlock {

	public ElectriBlock(Material par2Material) {
		super(par2Material);
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
	public void registerBlockIcons(IIconRegister ico) {
		blockIcon = ico.registerIcon("rotarycraft:steel");
	}

	@Override
	public final AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z)
	{
		return this.getCollisionBoundingBoxFromPool(world, x, y, z);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public final boolean addDestroyEffects(World world, int x, int y, int z, int meta, EffectRenderer eff)
	{
		if (this == ElectriBlocks.WIRE.getBlockInstance() || this == ElectriBlocks.CABLE.getBlockInstance())
			return super.addDestroyEffects(world, x, y, z, meta, eff);
		return ReikaRenderHelper.addModelledBlockParticles("/Reika/ElectriCraft/Textures/", world, x, y, z, this, eff, ReikaJavaLibrary.makeListFrom(new double[]{0,0,1,1}), ElectriCraft.class);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public final boolean addHitEffects(World world, MovingObjectPosition tg, EffectRenderer eff)
	{
		if (this == ElectriBlocks.WIRE.getBlockInstance() || this == ElectriBlocks.CABLE.getBlockInstance())
			return super.addHitEffects(world, tg, eff);
		return ReikaRenderHelper.addModelledBlockParticles("/Reika/ElectriCraft/Textures/", world, tg, this, eff, ReikaJavaLibrary.makeListFrom(new double[]{0,0,1,1}), ElectriCraft.class);
	}

	@Override
	public final TileEnum getMachine(IBlockAccess world, int x, int y, int z) {
		return ElectriTiles.getTE(world, x, y, z);
	}

}
