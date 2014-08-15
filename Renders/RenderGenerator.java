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
import Reika.ElectriCraft.Base.ElectriTERenderer;
import Reika.ElectriCraft.TileEntities.TileEntityGenerator;
import Reika.RotaryCraft.Auxiliary.IORenderer;
import Reika.RotaryCraft.ModInterface.ModelGenerator;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.opengl.GL11;

public class RenderGenerator extends ElectriTERenderer
{

	private ModelGenerator GeneratorModel = new ModelGenerator();

	public void renderTileEntityGeneratorAt(TileEntityGenerator tile, double par2, double par4, double par6, float par8)
	{
		int var9;

		if (!tile.isInWorld())
			var9 = 0;
		else
			var9 = tile.getBlockMetadata();

		ModelGenerator var14;
		var14 = GeneratorModel;

		this.bindTextureByName("/Reika/ElectriCraft/Textures/generatortex.png");

		this.setupGL(tile, par2, par4, par6);

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

		GL11.glRotatef(var11+180, 0.0F, 1.0F, 0.0F);
		var14.renderAll(tile, null, 0, 0);

		this.closeGL(tile);
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8)
	{
		if (this.isValidMachineRenderpass((TileEntityGenerator)tile))
			this.renderTileEntityGeneratorAt((TileEntityGenerator)tile, par2, par4, par6, par8);
		if (((TileEntityGenerator) tile).isInWorld() && MinecraftForgeClient.getRenderPass() == 1) {
			IORenderer.renderIO(tile, par2, par4, par6);
		}
	}

	@Override
	public String getImageFileName(RenderFetcher te) {
		return "generatortex.png";
	}
}