/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ElectriCraft.Renders;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.opengl.GL11;

import Reika.DragonAPI.Interfaces.RenderFetcher;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.ElectriCraft.Base.ElectriTERenderer;
import Reika.ElectriCraft.TileEntities.TileEntityTransformer;
import Reika.RotaryCraft.Auxiliary.IORenderer;

public class RenderTransformer extends ElectriTERenderer
{
	private ModelTransformer TransformerModel = new ModelTransformer();

	public void renderTileEntityTransformerAt(TileEntityTransformer tile, double par2, double par4, double par6, float par8)
	{
		int var9;

		if (!tile.isInWorld())
			var9 = 0;
		else
			var9 = tile.getBlockMetadata();

		ModelTransformer var14;
		var14 = TransformerModel;

		this.setupGL(tile, par2, par4, par6);

		this.bindTextureByName("/Reika/ElectriCraft/Textures/transformertex.png");

		int var11 = 0;
		float var13;
		switch(tile.getFacing()) {
		case EAST:
			var11 = 180;
			break;
		case WEST:
			var11 = 0;
			break;
		case NORTH:
			var11 = 90;
			break;
		case SOUTH:
			var11 = 270;
			break;
		default:
			break;
		}

		GL11.glRotatef(var11, 0.0F, 1.0F, 0.0F);
		var14.renderAll(tile, ReikaJavaLibrary.makeListFrom(tile.getN1(), tile.getN2()), tile.phi, 0);

		this.closeGL(tile);
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8)
	{
		if (this.doRenderModel((TileEntityTransformer)tile))
			this.renderTileEntityTransformerAt((TileEntityTransformer)tile, par2, par4, par6, par8);
		if (((TileEntityTransformer) tile).isInWorld() && MinecraftForgeClient.getRenderPass() == 1) {
			IORenderer.renderIO(tile, par2, par4, par6);
		}
	}

	@Override
	public String getImageFileName(RenderFetcher te) {
		return "transformertex.png";
	}
}
