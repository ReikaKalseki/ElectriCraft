/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/

package Reika.RotationalInduction.Renders;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.opengl.GL11;

import Reika.DragonAPI.Interfaces.RenderFetcher;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.RotaryCraft.Auxiliary.IORenderer;
import Reika.RotaryCraft.ModInterface.ModelElecMotor;
import Reika.RotationalInduction.Base.InductionTERenderer;
import Reika.RotationalInduction.TileEntities.TileEntityMotor;

public class RenderMotor extends InductionTERenderer
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

		this.bindTextureByName("/Reika/RotationalInduction/Textures/elecmotortex.png");

		int var11 = 0;
		float var13;
		switch(var9) {
		case 2:
			var11 = 0;
			break;
		case 0:
			var11 = 270;
			break;
		case 1:
			var11 = 90;
			break;
		case 3:
			var11 = 180;
			break;
		}

		GL11.glRotatef(var11, 0.0F, 1.0F, 0.0F);
		int num = tile.isInWorld() ? 5 : 5;
		var14.renderAll(ReikaJavaLibrary.makeListFrom(num), tile.phi, 0);

		this.closeGL(tile);
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8)
	{
		if (this.isValidMachineRenderpass((TileEntityMotor)tile))
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
