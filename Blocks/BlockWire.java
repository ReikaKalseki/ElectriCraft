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

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.RotationalInduction.Induction;
import Reika.RotationalInduction.Base.InductionBlock;
import Reika.RotationalInduction.TileEntities.TileEntityWire;

public class BlockWire extends InductionBlock {

	public BlockWire(int par1, Material par2Material) {
		super(par1, par2Material);
		this.setStepSound(soundClothFootstep);
	}

	@Override
	public TileEntity createTileEntity(World world, int meta) {
		return new TileEntityWire();
	}

	@Override
	public final int getRenderType() {
		return Induction.proxy.wireRender;
	}

	@Override
	public int idDropped(int id, Random r, int fortune) {
		return 0;
	}

	@Override
	public boolean canRenderInPass(int pass)
	{
		return pass == 0;
	}

	@Override
	public int damageDropped(int par1)
	{
		return par1;
	}

	@Override
	public int quantityDropped(Random par1Random)
	{
		return 0;
	}

	@Override
	public boolean canHarvestBlock(EntityPlayer player, int meta)
	{
		return true;
	}

	private boolean canHarvest(World world, EntityPlayer ep, int x, int y, int z) {
		return true;
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int id) {
		TileEntityWire te = (TileEntityWire)world.getBlockTileEntity(x, y, z);
		te.recomputeConnections(world, x, y, z);
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		TileEntityWire te = (TileEntityWire)world.getBlockTileEntity(x, y, z);
		te.addToAdjacentConnections(world, x, y, z);
		te.recomputeConnections(world, x, y, z);
	}

	@Override
	public final AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		double d = 0.25;
		return ReikaAABBHelper.getBlockAABB(x, y, z).contract(d, d, d);
	}

}
