/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ElectriCraft.Base;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ElectriCraft.ElectriCraft;
import Reika.ElectriCraft.Registry.ElectriTiles;


public abstract class BlockElectriCable extends Block {

	private IIcon center;
	private IIcon end;

	public BlockElectriCable(Material par2Material) {
		super(par2Material);
		this.setResistance(15);
		this.setHardness(0.5F);
	}

	@Override
	public abstract TileEntity createTileEntity(World world, int meta);

	public final IIcon getCenterIcon() {
		return center;
	}

	public final IIcon getEndIcon() {
		return end;
	}

	@Override
	public final IIcon getIcon(int s, int meta) {
		return center;
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
	public final int getRenderType() {
		return ElectriCraft.proxy.cableRender;
	}

	@Override
	public final AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z)
	{
		return this.getCollisionBoundingBoxFromPool(world, x, y, z);
	}

	@Override
	public final AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		double d = 0.25;
		ElectriCable te = (ElectriCable)world.getTileEntity(x, y, z);
		if (te == null)
			return null;
		float minx = te.isConnectedOnSideAt(world, x, y, z, ForgeDirection.WEST) ? 0 : 0.3F;
		float maxx = te.isConnectedOnSideAt(world, x, y, z, ForgeDirection.EAST) ? 1 : 0.7F;
		float minz = te.isConnectedOnSideAt(world, x, y, z, ForgeDirection.SOUTH) ? 0 : 0.3F;
		float maxz = te.isConnectedOnSideAt(world, x, y, z, ForgeDirection.NORTH) ? 1 : 0.7F;
		float miny = te.isConnectedOnSideAt(world, x, y, z, ForgeDirection.UP) ? 0 : 0.3F;
		float maxy = te.isConnectedOnSideAt(world, x, y, z, ForgeDirection.DOWN) ? 1 : 0.7F;
		AxisAlignedBB box = AxisAlignedBB.getBoundingBox(x+minx, y+miny, z+minz, x+maxx, y+maxy, z+maxz);
		this.setBounds(box, x, y, z);
		return box;
	}

	protected final void setBounds(AxisAlignedBB box, int x, int y, int z) {
		this.setBlockBounds((float)box.minX-x, (float)box.minY-y, (float)box.minZ-z, (float)box.maxX-x, (float)box.maxY-y, (float)box.maxZ-z);
	}

	@Override
	public final void onNeighborBlockChange(World world, int x, int y, int z, Block id) {
		ElectriCable te = (ElectriCable)world.getTileEntity(x, y, z);
		te.recomputeConnections(world, x, y, z);
	}

	@Override
	public final void onBlockAdded(World world, int x, int y, int z) {
		ElectriCable te = (ElectriCable)world.getTileEntity(x, y, z);
		te.addToAdjacentConnections(world, x, y, z);
		te.recomputeConnections(world, x, y, z);
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block oldid, int oldmeta) {
		ElectriCable te = (ElectriCable)world.getTileEntity(x, y, z);
		super.breakBlock(world, x, y, z, oldid, oldmeta);
	}

	@Override
	public final void registerBlockIcons(IIconRegister ico) {
		String s = this.getTextureIcoName();
		center = ico.registerIcon("electricraft:"+s);
		end = ico.registerIcon("electricraft:"+s+"_end");
	}

	protected abstract String getTextureIcoName();

	public abstract ElectriTiles getTile();

	@Override
	public final Item getItemDropped(int id, Random r, int fortune) {
		return this.getTile().getCraftedProduct().getItem();
	}

	@Override
	public final int damageDropped(int meta) {
		return this.getTile().getCraftedProduct().getItemDamage();
	}

	@Override
	public final ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z)
	{
		ItemStack is = this.getTile().getCraftedProduct();
		return is;
	}
}
