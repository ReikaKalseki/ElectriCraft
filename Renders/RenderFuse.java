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

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.opengl.GL11;

import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.ElectriCraft.Base.ElectriTERenderer;
import Reika.ElectriCraft.TileEntities.TileEntityFuse;
import Reika.RotaryCraft.Auxiliary.IORenderer;

public class RenderFuse extends ElectriTERenderer
{
	private ModelFuse FuseModel = new ModelFuse();

	public void renderTileEntityFuseAt(TileEntityFuse tile, double par2, double par4, double par6, float par8)
	{
		int var9;

		if (!tile.isInWorld())
			var9 = 0;
		else
			var9 = tile.getBlockMetadata();

		ModelFuse var14;
		var14 = FuseModel;

		this.setupGL(tile, par2, par4, par6);
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glEnable(GL11.GL_BLEND);
		BlendMode.DEFAULT.apply();

		String s = "/Reika/ElectriCraft/Textures/fusetex.png";
		if (tile.isOverloaded()) {
			s = "/Reika/ElectriCraft/Textures/fusetex-burn.png";
		}
		else {
			float f = tile.getWireCurrent()/(float)tile.getMaxCurrent();
			if (f >= 0.75) {
				s = "/Reika/ElectriCraft/Textures/fusetex-hot3.png";
			}
			else if (f >= 0.5) {
				s = "/Reika/ElectriCraft/Textures/fusetex-hot2.png";
			}
			else if (f >= 0.25) {
				s = "/Reika/ElectriCraft/Textures/fusetex-hot.png";
			}
		}
		this.bindTextureByName(s);

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
		//GL11.glTranslated(0, -0.1875, 0);
		var14.renderAll(tile, null, tile.phi, 0);

		GL11.glPopAttrib();
		this.closeGL(tile);
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8)
	{
		//if (this.doRenderModel((TileEntityFuse)tile))
		this.renderTileEntityFuseAt((TileEntityFuse)tile, par2, par4, par6, par8);
		if (((TileEntityFuse) tile).isInWorld() && MinecraftForgeClient.getRenderPass() == 1) {
			IORenderer.renderIO(tile, par2, par4, par6);
		}
	}

	@Override
	public String getImageFileName(RenderFetcher te) {
		return "fusetex.png";
	}
}
