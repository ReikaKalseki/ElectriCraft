package Reika.RotationalInduction.Renders;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;

import org.lwjgl.opengl.GL11;

import Reika.DragonAPI.Interfaces.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.RotationalInduction.Base.InductionTERenderer;
import Reika.RotationalInduction.TileEntities.TileEntityWire;

public class RenderWire extends InductionTERenderer {

	@Override
	public String getImageFileName(RenderFetcher te) {
		return "";
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float f) {
		TileEntityWire te = (TileEntityWire)tile;
		if (tile.hasWorldObj()) {

		}
		else {
			ReikaTextureHelper.bindTerrainTexture();
			this.renderBlock(te, par2, par4-0.3, par6, te.getEndIcon());
			this.renderBlock(te, par2, par4+0.1, par6, te.getCenterIcon());
			this.renderBlock(te, par2, par4+0.5, par6, te.getEndIcon());
			if (te.insulated) {
				double s = 1.2;
				double d = 2.375;
				double dy = -0.25;
				double dx = -0.5;
				GL11.glRotated(90, 0, 1, 0);
				GL11.glTranslated(dx, dy, 0);
				GL11.glScaled(s, d, s);
				this.renderBlock(te, par2, par4+0.1, par6, te.getInsulatedCenterIcon());
				GL11.glScaled(1/s, 1/d, 1/s);
				GL11.glTranslated(-dx, -dy, 0);
				GL11.glRotated(-90, 0, 1, 0);
			}
		}
	}

	private void renderBlock(TileEntityWire te, double par2, double par4, double par6, Icon ico) {
		float u = ico.getMinU();
		float v = ico.getMinV();
		float du = ico.getMaxU();
		float dv = ico.getMaxV();
		GL11.glTranslated(par2, par4, par6);
		Tessellator v5 = Tessellator.instance;

		float f = 0.5F;
		double s = 0.4;
		GL11.glColor4f(f, f, f, 1);
		GL11.glScaled(s, s, s);
		v5.startDrawingQuads();
		v5.setNormal(0, 1, 0);
		v5.addVertexWithUV(0, 0, 1, u, v);
		v5.addVertexWithUV(1, 0, 1, du, v);
		v5.addVertexWithUV(1, 1, 1, du, dv);
		v5.addVertexWithUV(0, 1, 1, u, dv);
		v5.draw();

		v5.startDrawingQuads();
		v5.setNormal(0, 1, 0);
		v5.addVertexWithUV(0, 1, 0, u, dv);
		v5.addVertexWithUV(1, 1, 0, du, dv);
		v5.addVertexWithUV(1, 0, 0, du, v);
		v5.addVertexWithUV(0, 0, 0, u, v);
		v5.draw();

		f = 0.33F;
		GL11.glColor4f(f, f, f, 1);
		v5.startDrawingQuads();
		v5.setNormal(0, 1, 0);
		v5.addVertexWithUV(1, 1, 0, u, dv);
		v5.addVertexWithUV(1, 1, 1, du, dv);
		v5.addVertexWithUV(1, 0, 1, du, v);
		v5.addVertexWithUV(1, 0, 0, u, v);
		v5.draw();

		v5.startDrawingQuads();
		v5.setNormal(0, 1, 0);
		v5.addVertexWithUV(0, 0, 0, u, v);
		v5.addVertexWithUV(0, 0, 1, du, v);
		v5.addVertexWithUV(0, 1, 1, du, dv);
		v5.addVertexWithUV(0, 1, 0, u, dv);
		v5.draw();

		f = 1F;
		GL11.glColor4f(f, f, f, 1);
		v5.startDrawingQuads();
		v5.setNormal(0, 1, 0);
		v5.addVertexWithUV(0, 1, 1, u, dv);
		v5.addVertexWithUV(1, 1, 1, du, dv);
		v5.addVertexWithUV(1, 1, 0, du, v);
		v5.addVertexWithUV(0, 1, 0, u, v);
		v5.draw();

		v5.startDrawingQuads();
		v5.setNormal(0, 1, 0);
		v5.addVertexWithUV(0, 0, 0, u, v);
		v5.addVertexWithUV(1, 0, 0, du, v);
		v5.addVertexWithUV(1, 0, 1, du, dv);
		v5.addVertexWithUV(0, 0, 1, u, dv);
		v5.draw();
		GL11.glScaled(1/s, 1/s, 1/s);
		GL11.glTranslated(-par2, -par4, -par6);
		v5.setColorOpaque(255, 255, 255);
	}

}
