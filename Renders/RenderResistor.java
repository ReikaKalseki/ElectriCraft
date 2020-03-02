/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ElectriCraft.Renders;

import org.lwjgl.opengl.GL11;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.MinecraftForgeClient;

import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.ElectriCraft.Base.ElectriTERenderer;
import Reika.ElectriCraft.Base.TileEntityResistorBase;
import Reika.ElectriCraft.Base.TileEntityResistorBase.ColorBand;
import Reika.RotaryCraft.Auxiliary.IORenderer;

public class RenderResistor extends ElectriTERenderer
{
	protected ModelResistorBase ResistorModel = new ModelResistor();

	public final void renderTileEntityResistorAt(TileEntityResistorBase tile, double par2, double par4, double par6, float par8) {
		int var9;

		if (!tile.isInWorld())
			var9 = 0;
		else
			var9 = tile.getBlockMetadata();

		ModelResistorBase var14;
		var14 = ResistorModel;

		this.setupGL(tile, par2, par4, par6);

		this.bindTextureByName("/Reika/ElectriCraft/Textures/resistor.png");

		int var11 = 0;
		float var13;
		switch(tile.getFacing()) {
			case EAST:
				var11 = 270;
				break;
			case WEST:
				var11 = 90;
				break;
			case NORTH:
				var11 = 180;
				break;
			case SOUTH:
				var11 = 0;
				break;
			default:
				break;
		}

		GL11.glRotatef(var11, 0.0F, 1.0F, 0.0F);
		ColorBand[] colors = tile.getColorBands();
		var14.renderAll(tile, ReikaJavaLibrary.makeListFrom(colors), tile.phi, 0);

		this.closeGL(tile);
	}

	@Override
	public final void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		if (this.doRenderModel((TileEntityResistorBase)tile))
			this.renderTileEntityResistorAt((TileEntityResistorBase)tile, par2, par4, par6, par8);
		if (((TileEntityResistorBase) tile).isInWorld() && MinecraftForgeClient.getRenderPass() == 1) {
			IORenderer.renderIO(tile, par2, par4, par6);
		}
	}

	@Override
	public final String getImageFileName(RenderFetcher te) {
		return "resistor.png";
	}
}
