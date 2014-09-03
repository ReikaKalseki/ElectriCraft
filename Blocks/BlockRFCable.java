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

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
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
import Reika.ElectriCraft.TileEntities.TileEntityRFCable;

public class BlockRFCable extends Block {

	private static IIcon center;
	private static IIcon end;

	public BlockRFCable(Material par2Material) {
		super(par2Material);
		this.setResistance(15);
		this.setHardness(0.5F);
	}

	@Override
	public TileEntity createTileEntity(World world, int meta) {
		return new TileEntityRFCable();
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block id) {
		TileEntityRFCable te = (TileEntityRFCable)world.getTileEntity(x, y, z);
		te.recomputeConnections(world, x, y, z);
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		TileEntityRFCable te = (TileEntityRFCable)world.getTileEntity(x, y, z);
		te.addToAdjacentConnections(world, x, y, z);
		te.recomputeConnections(world, x, y, z);
	}

	@Override
	public Item getItemDropped(int id, Random r, int fortune) {
		return ElectriTiles.CABLE.getCraftedProduct().getItem();
	}

	@Override
	public int damageDropped(int meta) {
		return ElectriTiles.CABLE.getCraftedProduct().getItemDamage();
	}

	@Override
	public final AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		double d = 0.25;
		TileEntityRFCable te = (TileEntityRFCable)world.getTileEntity(x, y, z);
		if (te == null)
			return null;
		float minx = te.isConnectedOnSideAt(world, x, y, z, ForgeDirection.WEST) ? 0 : 0.3F;
		float maxx = te.isConnectedOnSideAt(world, x, y, z, ForgeDirection.EAST) ? 1 : 0.7F;
		float minz = te.isConnectedOnSideAt(world, x, y, z, ForgeDirection.SOUTH) ? 0 : 0.3F;
		float maxz = te.isConnectedOnSideAt(world, x, y, z, ForgeDirection.NORTH) ? 1 : 0.7F;
		float miny = te.isConnectedOnSideAt(world, x, y, z, ForgeDirection.UP) ? 0 : 0.3F;
		float maxy = te.isConnectedOnSideAt(world, x, y, z, ForgeDirection.DOWN) ? 1 : 0.7F;
		return AxisAlignedBB.getBoundingBox(x+minx, y+miny, z+minz, x+maxx, y+maxy, z+maxz);
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block oldid, int oldmeta) {
		TileEntityRFCable te = (TileEntityRFCable)world.getTileEntity(x, y, z);
		if (!world.isRemote)
			te.removeFromNetwork();
		super.breakBlock(world, x, y, z, oldid, oldmeta);
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
		return ElectriCraft.proxy.cableRender;
	}

	@Override
	public final AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z)
	{
		return this.getCollisionBoundingBoxFromPool(world, x, y, z);
	}

	public static IIcon getCenterIcon() {
		return center;
	}

	public static IIcon getEndIcon() {
		return end;
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		return center;
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		center = ico.registerIcon("electricraft:rf");
		end = ico.registerIcon("electricraft:rf_end");
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z)
	{
		TileEntityRFCable te = (TileEntityRFCable)world.getTileEntity(x, y, z);
		ItemStack is = ElectriTiles.CABLE.getCraftedProduct();
		return is;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer ep, int s, float a, float b, float c) {
		ItemStack is = ep.getCurrentEquippedItem();
		//if (!ReikaItemHelper.matchStacks(is, ElectriTiles.CABLE.getCraftedProduct())) {
		ep.openGui(ElectriCraft.instance, 0, world, x, y, z);
		return true;
		//}
		//return false;
	}

}
