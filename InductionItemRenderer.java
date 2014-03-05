/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.RotationalInduction;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.RotationalInduction.Registry.InductionItems;
import Reika.RotationalInduction.Registry.InductionTiles;
import Reika.RotationalInduction.Registry.WireType;
import Reika.RotationalInduction.TileEntities.TileEntityWire;

public class InductionItemRenderer implements IItemRenderer {


	public InductionItemRenderer() {

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
		if (item.itemID == InductionItems.WIRE.getShiftedID()) {
			TileEntityWire wire = (TileEntityWire)InductionTiles.WIRE.createTEInstanceForRender();
			wire.insulated = item.getItemDamage() >= WireType.INS_OFFSET;
			wire.setBlockMetadata(item.getItemDamage()%WireType.INS_OFFSET);
			TileEntityRenderer.instance.renderTileEntityAt(wire, a, -0.1D, b, 0.0F);
			return;
		}
		InductionTiles machine = InductionTiles.TEList[item.getItemDamage()];
		if (machine.hasRender())
			TileEntityRenderer.instance.renderTileEntityAt(machine.createTEInstanceForRender(), a, -0.1D, b, 0.0F);
		else {
			ReikaTextureHelper.bindTerrainTexture();
			rb.renderBlockAsItem(machine.getBlockVariable(), 0, 1);
		}
	}
}
