/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ElectriCraft;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import Reika.DragonAPI.Base.CoreContainer;
import Reika.ElectriCraft.GUIs.GuiElectriBook;
import Reika.ElectriCraft.GUIs.GuiRFCable;
import Reika.ElectriCraft.GUIs.GuiTransformer;
import Reika.ElectriCraft.TileEntities.TileEntityTransformer;
import Reika.ElectriCraft.TileEntities.ModInterface.TileEntityRFCable;

import cpw.mods.fml.common.network.IGuiHandler;

public class ElectriGuiHandler implements IGuiHandler {

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (ID == 10) {
			return null;
		}
		if (te instanceof TileEntityRFCable) {
			return new CoreContainer(player, te);
		}
		if (te instanceof TileEntityTransformer) {
			return new CoreContainer(player, te);
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if (ID == 10) {
			return new GuiElectriBook(player, world, 0, 0);
		}
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof TileEntityRFCable) {
			return new GuiRFCable(player, (TileEntityRFCable)te);
		}
		if (te instanceof TileEntityTransformer) {
			return new GuiTransformer(player, (TileEntityTransformer)te);
		}
		return null;
	}

}
