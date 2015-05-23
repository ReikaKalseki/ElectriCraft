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
import Reika.ElectriCraft.TileEntities.TileEntityMotor;
import Reika.RotaryCraft.Auxiliary.IORenderer;
import Reika.RotaryCraft.ModInterface.ModelElecMotor;

public class RenderMotor extends ElectriTERenderer
{
	private ModelElecMotor ElecMotorModel = new ModelElecMotor();

	public void renderTileEntityMotorAt(TileEntityMotor tile, double par2, double par4, double par6, float par8)
	{
		int var9;

		if (!tile.isInWorld())
			var9 = 0;
		else
			var9 = tile.getBlockMetadata();

		ModelElecMotor var14;
		var14 = ElecMotorModel;

		this.setupGL(tile, par2, par4, par6);

		this.bindTextureByName("/Reika/ElectriCraft/Textures/elecmotortex.png");

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
		if (tile.isFlipped && tile.getFacing().offsetZ != 0) {
			GL11.glRotated(180, 0, 1, 0);
		}
		int num = tile.isInWorld() ? 5 : 5;
		var14.renderAll(tile, ReikaJavaLibrary.makeListFrom(num), tile.phi, 0);

		this.closeGL(tile);
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8)
	{
		if (this.doRenderModel((TileEntityMotor)tile))
			this.renderTileEntityMotorAt((TileEntityMotor)tile, par2, par4, par6, par8);
		if (((TileEntityMotor) tile).isInWorld() && MinecraftForgeClient.getRenderPass() == 1) {
			IORenderer.renderIO(tile, par2, par4, par6);
		}
	}

	@Override
	public String getImageFileName(RenderFetcher te) {
		return "elecmotortex.png";
	}
}
