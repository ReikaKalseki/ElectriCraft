/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ElectriCraft.Blocks;

import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import Reika.ElectriCraft.Base.BatteryBlock;
import Reika.ElectriCraft.Base.ElectriTileEntity;
import Reika.ElectriCraft.Registry.ElectriTiles;
import Reika.ElectriCraft.TileEntities.ModInterface.TileEntityRFBattery;

public class BlockRFBattery extends BatteryBlock {

	public BlockRFBattery(Material par2Material) {
		super(par2Material);
	}

	@Override
	public ElectriTileEntity createTileEntity(World world, int meta) {
		return new TileEntityRFBattery();
	}

	@Override
	protected String getTextureSubstring() {
		return "rf";
	}

	@Override
	public ElectriTiles getMapping(int meta) {
		return ElectriTiles.RFBATTERY;
	}

	@Override
	public ElectriTiles getMapping(IBlockAccess world, int x, int y, int z) {
		return ElectriTiles.RFBATTERY;
	}

}
