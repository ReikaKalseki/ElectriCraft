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

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import Reika.ElectriCraft.Base.ElectriBlock;
import Reika.ElectriCraft.Registry.ElectriTiles;
import Reika.ElectriCraft.TileEntities.TileEntityResistor;
import Reika.ElectriCraft.TileEntities.TileEntityResistor.ColorBand;

public class BlockElectricMachine extends ElectriBlock {

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
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer ep, int side, float a, float b, float c) {
		ElectriTiles e = ElectriTiles.getTE(world, x, y, z);
		if (e == ElectriTiles.RESISTOR) {
			TileEntityResistor te = (TileEntityResistor)world.getBlockTileEntity(x, y, z);
			te.setColors(ColorBand.BLACK, ColorBand.BROWN, ColorBand.BROWN);
			return true;
		}
		return false;
	}

}
