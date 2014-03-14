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

import java.util.List;

import mcp.mobius.waila.api.IWailaBlock;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.ElectriCraft.Base.ElectriBlock;
import Reika.ElectriCraft.Base.TileEntityWireComponent;
import Reika.ElectriCraft.Registry.ElectriTiles;
import Reika.ElectriCraft.TileEntities.TileEntityRelay;
import Reika.ElectriCraft.TileEntities.TileEntityResistor;

public class BlockElectricMachine extends ElectriBlock implements IWailaBlock {

	public BlockElectricMachine(int par1, Material mat) {
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

	@Override
	public final AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
	{
		ElectriTiles t = ElectriTiles.getTE(world, x, y, z);
		if (t != null) {
			if (t.isSpecialWiringPiece()) {
				AxisAlignedBB box = ((TileEntityWireComponent)world.getBlockTileEntity(x, y, z)).getAABB();
				return box;
			}
		}
		return super.getCollisionBoundingBoxFromPool(world, x, y, z);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer ep, int side, float a, float b, float c) {
		ElectriTiles e = ElectriTiles.getTE(world, x, y, z);
		ItemStack is = ep.getCurrentEquippedItem();
		if (e == ElectriTiles.RESISTOR && is != null && is.itemID == Item.dyePowder.itemID) {
			TileEntityResistor te = (TileEntityResistor)world.getBlockTileEntity(x, y, z);
			ForgeDirection dir = te.getFacing();
			float inc = dir.offsetX != 0 ? a : c;
			if (dir.offsetX > 0 || dir.offsetZ > 0)
				inc = 1F-inc;
			int band = 0;
			if (ReikaMathLibrary.isValueInsideBoundsIncl(0.125, 0.25, inc))
				band = 1;
			else if (ReikaMathLibrary.isValueInsideBoundsIncl(0.3125, 0.4375, inc))
				band = 2;
			else if (ReikaMathLibrary.isValueInsideBoundsIncl(0.5, 0.625, inc))
				band = 3;
			if (band > 0) {
				te.setColor(is, band);
				if (!ep.capabilities.isCreativeMode)
					is.stackSize--;
				return true;
			}
		}
		return false;
	}

	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
		return null;
	}

	@Override
	public List<String> getWailaHead(ItemStack is, List<String> tip, IWailaDataAccessor acc, IWailaConfigHandler config) {
		return tip;
	}

	@Override
	public List<String> getWailaBody(ItemStack is, List<String> tip, IWailaDataAccessor acc, IWailaConfigHandler config) {
		TileEntity te = acc.getTileEntity();
		if (te instanceof TileEntityResistor) {
			int limit = ((TileEntityResistor) te).getCurrentLimit();
			tip.add(String.format("Current Limit: %dA", limit));
		}
		if (te instanceof TileEntityRelay) {
			tip.add(((TileEntityRelay) te).isEnabled() ? "Connected" : "Disconnected");
		}
		return tip;
	}

	@Override
	public List<String> getWailaTail(ItemStack is, List<String> tip, IWailaDataAccessor acc, IWailaConfigHandler config) {
		return tip;
	}

}
