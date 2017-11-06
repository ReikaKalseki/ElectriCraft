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

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.ElectriCraft.Base.ElectriTERenderer;
import Reika.ElectriCraft.TileEntities.TileEntityMeter;
import Reika.RotaryCraft.Auxiliary.IORenderer;

public class RenderElectricMeter extends ElectriTERenderer
{
	private ModelMeter MeterModel = new ModelMeter();

	public void renderTileEntityMeterAt(TileEntityMeter tile, double par2, double par4, double par6, float par8)
	{
		int var9;

		ModelMeter var14;
		var14 = MeterModel;

		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.setupGL(tile, par2, par4, par6);

		this.bindTextureByName("/Reika/ElectriCraft/Textures/metertex.png");

		var14.renderAll(tile, null, tile.phi);
		if (tile.isInWorld())
			this.renderText(tile, par2, par4, par6);

		this.closeGL(tile);
	}

	private void renderText(TileEntityMeter tile, double par2, double par4, double par6) {
		FontRenderer f = this.getFontRenderer();
		String s1 = String.format("Voltage:");
		String s2 = String.format("Current:");
		String s1b = String.format("%dV", tile.getWireVoltage());
		String s2b = String.format("%dA", tile.getWireCurrent());

		String s3 = String.format("Resistance: %d Ohms", tile.getResistance());

		GL11.glPushMatrix();
		double s = 0.01;
		//ReikaRenderHelper.disableLighting();
		GL11.glDisable(GL11.GL_LIGHTING);
		ReikaRenderHelper.disableEntityLighting();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glDepthMask(false);
		float ang = RenderManager.instance.playerViewY%360;
		if (ang < 0)
			ang += 360;
		int ra = 90*(int)((ang+180+45)/90F);
		GL11.glRotated(ra, 0, 1, 0);
		GL11.glTranslated(0, 0.515, 0);
		GL11.glScaled(s, s, s);
		GL11.glRotated(-90, 1, 0, 0);

		int dx = -30;
		int dy = -30;

		f.drawString(s1, dx, dy, 0xffffff);
		f.drawString(s1b, dx, dy+10, 0xffffff);

		f.drawString(s2, dx, dy+30, 0xffffff);
		f.drawString(s2b, dx, dy+40, 0xffffff);

		GL11.glPopMatrix();
		GL11.glDepthMask(true);
		ReikaRenderHelper.enableEntityLighting();
		GL11.glEnable(GL11.GL_LIGHTING);
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8)
	{
		if (this.doRenderModel((TileEntityMeter)tile))
			this.renderTileEntityMeterAt((TileEntityMeter)tile, par2, par4, par6, par8);
		if (((TileEntityMeter) tile).isInWorld() && MinecraftForgeClient.getRenderPass() == 1) {
			IORenderer.renderIO(tile, par2, par4, par6);
		}
	}

	@Override
	public String getImageFileName(RenderFetcher te) {
		return "metertex.png";
	}
}
