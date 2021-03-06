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

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;

import Reika.DragonAPI.Base.TileEntityBase;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.Rendering.ReikaColorAPI;
import Reika.DragonAPI.Libraries.Rendering.ReikaRenderHelper;
import Reika.ElectriCraft.Auxiliary.Interfaces.BatteryTile;
import Reika.ElectriCraft.Base.ElectriTERenderer;

public class RenderModBattery extends ElectriTERenderer {

	@Override
	public String getImageFileName(RenderFetcher te) {
		return "";
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		BatteryTile te = (BatteryTile)tile;
		//if (te.isInWorld()) {
		GL11.glPushMatrix();
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glTranslated(par2, par4, par6);
		Tessellator v5 = Tessellator.instance;
		if (((TileEntityBase)te).isInWorld())
			ReikaRenderHelper.prepareGeoDraw(true);
		else {
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_LIGHTING);
		}

		double o = 0.0025;
		double w = 0.1875;
		double h = 0.0625;

		//double[] l = ReikaMathLibrary.splitNumberByDigits(te.getStoredEnergy(), 10);
		//for (int k = 0; k < l.length; k++) {
		int v = 4;
		int n = 16*v;
		int c = (int)(64*ReikaMathLibrary.logbase(te.getStoredEnergy(), 10)/ReikaMathLibrary.logbase(te.getMaxEnergy(), 10));//(int)Math.min(n, 0.5+(double)te.getStoredEnergy()*n/te.CAPACITY);//(int)(n*l[k]);//;
		double d = (1-h*2)/n;
		//double r = 0.0625/3;
		int c1 = te.getEnergyColor();
		int c2 = ReikaColorAPI.getColorWithBrightnessMultiplier(c1, 0.05F);
		for (int i = 0; i < c; i++) {
			v5.startDrawingQuads();
			int color = ReikaColorAPI.mixColors(c1, c2, (float)i/n);
			v5.setColorOpaque_I(color);
			v5.addVertex(0.5-w, h+d*(i+1), 0-o);
			v5.addVertex(0.5+w, h+d*(i+1), 0-o);
			v5.addVertex(0.5+w, h+d*i, 0-o);
			v5.addVertex(0.5-w, h+d*i, 0-o);
			v5.draw();
		}

		for (int i = 0; i < c; i++) {
			v5.startDrawingQuads();
			int color = ReikaColorAPI.mixColors(c1, c2, (float)i/n);
			v5.setColorOpaque_I(color);
			v5.addVertex(0.5-w, h+d*i, 1+o);
			v5.addVertex(0.5+w, h+d*i, 1+o);
			v5.addVertex(0.5+w, h+d*(i+1), 1+o);
			v5.addVertex(0.5-w, h+d*(i+1), 1+o);
			v5.draw();
		}

		for (int i = 0; i < c; i++) {
			v5.startDrawingQuads();
			int color = ReikaColorAPI.mixColors(c1, c2, (float)i/n);
			v5.setColorOpaque_I(color);
			v5.addVertex(1+o, h+d*(i+1), 0.5-w);
			v5.addVertex(1+o, h+d*(i+1), 0.5+w);
			v5.addVertex(1+o, h+d*i, 0.5+w);
			v5.addVertex(1+o, h+d*i, 0.5-w);
			v5.draw();
		}

		for (int i = 0; i < c; i++) {
			v5.startDrawingQuads();
			int color = ReikaColorAPI.mixColors(c1, c2, (float)i/n);
			v5.setColorOpaque_I(color);
			v5.addVertex(0-o, h+d*i, 0.5-w);
			v5.addVertex(0-o, h+d*i, 0.5+w);
			v5.addVertex(0-o, h+d*(i+1), 0.5+w);
			v5.addVertex(0-o, h+d*(i+1), 0.5-w);
			v5.draw();
		}

		//	}

		GL11.glPopAttrib();

		/*
			ReikaRenderHelper.disableEntityLighting();
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glPushMatrix();
			double s = 0.125;
			double da = 0;
			double db = 5;
			double dc = 0;
			GL11.glTranslated(da, db, dc);
			GL11.glScaled(s, -s, s);
			this.getFontRenderer().drawString(String.format("%.5f", ReikaMathLibrary.logbase(te.getStoredEnergy(), 10)), 0, 0, 0xffffff);
			GL11.glPopMatrix();

			ReikaRenderHelper.enableEntityLighting();
			GL11.glEnable(GL11.GL_LIGHTING);*/

		GL11.glPopMatrix();
		//}
	}

}
