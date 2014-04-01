/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ElectriCraft.Base;

import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import Reika.DragonAPI.Base.TileEntityRenderBase;
import Reika.DragonAPI.Interfaces.TextureFetcher;
import Reika.ElectriCraft.ElectriCraft;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class ElectriTERenderer extends TileEntityRenderBase implements TextureFetcher {

	@Override
	public final String getTextureFolder() {
		return "/Reika/ElectriCraft/Textures/TileEntityTex/";
	}

	@Override
	protected Class getModClass() {
		return ElectriCraft.class;
	}

	protected void setupGL(ElectriTileEntity tile, double par2, double par4, double par6) {
		GL11.glPushMatrix();
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glTranslated(par2, par4, par6);
		GL11.glScalef(1.0F, -1.0F, -1.0F);
		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
		GL11.glTranslated(0, -2, -1);

		if (tile.isInWorld() && tile.isFlipped && MinecraftForgeClient.getRenderPass() == 0) {
			GL11.glRotated(180, 1, 0, 0);
			GL11.glTranslated(0, -2, 0);
		}
	}

	protected void closeGL(ElectriTileEntity tile) {
		if (tile.isInWorld())
			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}
}
