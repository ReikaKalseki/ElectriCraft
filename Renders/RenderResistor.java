/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ElectriCraft.Renders;

import Reika.DragonAPI.Interfaces.RenderFetcher;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;
import Reika.ElectriCraft.Base.ElectriTERenderer;
import Reika.ElectriCraft.TileEntities.TileEntityResistor;
import Reika.RotaryCraft.Auxiliary.IORenderer;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.opengl.GL11;

public class RenderResistor extends ElectriTERenderer
{
	private ModelResistor ResistorModel = new ModelResistor();

	public void renderTileEntityResistorAt(TileEntityResistor tile, double par2, double par4, double par6, float par8)
	{
		int var9;

		if (!tile.isInWorld())
			var9 = 0;
		else
			var9 = tile.getBlockMetadata();

		ModelResistor var14;
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
		ReikaDyeHelper[] colors = tile.getBandRenderColors();
		var14.renderAll(tile, ReikaJavaLibrary.makeListFrom(colors), tile.phi, 0);

		this.closeGL(tile);
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8)
	{
		if (this.isValidMachineRenderpass((TileEntityResistor)tile))
			this.renderTileEntityResistorAt((TileEntityResistor)tile, par2, par4, par6, par8);
		if (((TileEntityResistor) tile).isInWorld() && MinecraftForgeClient.getRenderPass() == 1) {
			IORenderer.renderIO(tile, par2, par4, par6);
		}
	}

	@Override
	public String getImageFileName(RenderFetcher te) {
		return "resistor.png";
	}
}
