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

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.opengl.GL11;

import Reika.DragonAPI.Interfaces.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
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
		TileEntityTransformer te = (TileEntityTransformer)tile;
		if (this.doRenderModel(te))
			this.renderTileEntityTransformerAt(te, par2, par4, par6, par8);
		if (te.isInWorld() && MinecraftForgeClient.getRenderPass() == 1) {
			IORenderer.renderIO(tile, par2, par4, par6);

			this.renderArrow(te, par2, par4, par6);
		}
	}

	private void renderArrow(TileEntityTransformer te, double par2, double par4, double par6) {
		int a = Math.max(0, 512-te.getTicksExisted()*8);
		if (a > 0) {
			GL11.glPushMatrix();
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

			GL11.glTranslated(par2, par4, par6);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_BLEND);
			BlendMode.DEFAULT.apply();
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			ReikaRenderHelper.disableEntityLighting();
			float lw = GL11.glGetFloat(GL11.GL_LINE_WIDTH);
			GL11.glLineWidth(5);

			Tessellator v5 = Tessellator.instance;
			v5.setBrightness(240);
			v5.startDrawing(GL11.GL_LINES);
			v5.setColorRGBA_I(0xffffff, a);

			double h = 1.1;
			v5.addVertex(0.5, h, 0.5);
			double dr = 0.125;
			double r = 0.375;
			double w = 0.08;
			double dx = 0.5+te.getFacing().offsetX*r;
			double dz = 0.5+te.getFacing().offsetZ*r;
			v5.addVertex(dx, h, dz);

			v5.addVertex(dx, h, dz);
			v5.addVertex(dx-te.getFacing().offsetX*dr+te.getFacing().offsetZ*w, h, dz-te.getFacing().offsetZ*dr+te.getFacing().offsetX*w);

			v5.addVertex(dx, h, dz);
			v5.addVertex(dx-te.getFacing().offsetX*dr-te.getFacing().offsetZ*w, h, dz-te.getFacing().offsetZ*dr-te.getFacing().offsetX*w);

			v5.draw();

			GL11.glLineWidth(lw);

			GL11.glPopMatrix();
			GL11.glPopAttrib();
		}
	}

	@Override
	public String getImageFileName(RenderFetcher te) {
		return "transformertex.png";
	}
}
