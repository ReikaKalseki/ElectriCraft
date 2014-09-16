/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ElectriCraft;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.ElectriCraft.Registry.ElectriTiles;
import Reika.ElectriCraft.Registry.WireType;
import Reika.ElectriCraft.TileEntities.TileEntityRFBattery;
import Reika.ElectriCraft.TileEntities.TileEntityWire;

public class ElectriItemRenderer implements IItemRenderer {


	public ElectriItemRenderer() {

	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return true;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		float a = 0; float b = 0;

		RenderBlocks rb = (RenderBlocks)data[0];
		if (type == type.ENTITY) {
			a = -0.5F;
			b = -0.5F;
			GL11.glScalef(0.5F, 0.5F, 0.5F);
		}
		ElectriTiles machine = ElectriTiles.getMachine(item);
		if (machine == ElectriTiles.WIRE) {
			TileEntityWire wire = (TileEntityWire)ElectriTiles.WIRE.createTEInstanceForRender();
			wire.insulated = item.getItemDamage() >= WireType.INS_OFFSET;
			wire.setBlockMetadata(item.getItemDamage()%WireType.INS_OFFSET);
			TileEntityRendererDispatcher.instance.renderTileEntityAt(wire, a, -0.1D, b, 0.0F);
		}
		else if (machine == ElectriTiles.RFBATTERY) {
			TileEntityRFBattery te = (TileEntityRFBattery)machine.createTEInstanceForRender();
			ReikaTextureHelper.bindTerrainTexture();
			rb.renderBlockAsItem(machine.getBlockInstance(), item.getItemDamage(), 1);
			te.setEnergyFromNBT(item);
			TileEntityRendererDispatcher.instance.renderTileEntityAt(te, -0.5, -0.5, -0.5, 0.0F);
		}
		else if (machine.hasRender())
			TileEntityRendererDispatcher.instance.renderTileEntityAt(machine.createTEInstanceForRender(), a, -0.1D, b, 0.0F);
		else {
			ReikaTextureHelper.bindTerrainTexture();
			rb.renderBlockAsItem(machine.getBlockInstance(), item.getItemDamage(), 1);
		}
	}
}
