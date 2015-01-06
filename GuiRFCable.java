/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ElectriCraft;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import Reika.DragonAPI.Base.CoreContainer;
import Reika.DragonAPI.Instantiable.GUI.ImagedGuiButton;
import Reika.DragonAPI.Libraries.IO.ReikaGuiAPI;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaEngLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.ElectriCraft.Registry.ElectriPackets;
import Reika.ElectriCraft.TileEntities.TileEntityRFCable;
import Reika.RotaryCraft.RotaryCraft;

public class GuiRFCable extends GuiContainer {

	private final TileEntityRFCable cable;
	private int limit;

	public GuiRFCable(EntityPlayer ep, TileEntityRFCable te) {
		super(new CoreContainer(ep, te));
		cable = te;
		limit = te.getRFLimit();
		xSize = 197;
		ySize = 103;
	}

	@Override
	public void initGui() {
		super.initGui();
		buttonList.clear();

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

		String tex = "/Reika/RotaryCraft/Textures/GUI/buttons.png";
		for (int i = 0; i < 6; i++) {
			int dx = xSize/2-12;
			int dy = 20+i*12;
			int w = 50;
			int nve = i != 0 ? -i : -40;
			buttonList.add(new ImagedGuiButton(i, j+dx-w, k+dy, 24, 12, 18, 54, tex, RotaryCraft.class));
			buttonList.add(new ImagedGuiButton(nve, j+dx+w, k+dy, 24, 12, 42, 54, tex, RotaryCraft.class));
		}
		buttonList.add(new GuiButton(100, j+xSize/2-20, k+72, 40, 20, "Reset"));
	}

	@Override
	protected void actionPerformed(GuiButton b) {
		super.actionPerformed(b);

		if (b.id == 100) {
			limit = 0;
		}
		else {
			int amt = b.id == -40 ? 1 : ReikaMathLibrary.intpow2(10, this.getPower(b.id));
			if (b.id < 0)
				amt = -amt;
			limit += amt;
			limit = Math.max(limit, 0);
		}

		cable.setRFLimit(limit);
		ReikaPacketHelper.sendDataPacket(ElectriCraft.packetChannel, ElectriPackets.RFCABLE.ordinal(), cable, limit);
		this.initGui();
	}

	private int getPower(int base) {
		return Math.abs(base)+(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) ? 3 : 0);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		super.drawGuiContainerForegroundLayer(par1, par2);
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		String s = String.format("Network Limit:");
		String s2 = String.format("%.3f %sRF/t", ReikaMathLibrary.getThousandBase(limit), ReikaEngLibrary.getSIPrefix(limit));
		int dx = xSize/2;
		int dy = ySize/2-4;
		ReikaGuiAPI.instance.drawCenteredStringNoShadow(fontRendererObj, s, dx, dy-6, 4210752);
		ReikaGuiAPI.instance.drawCenteredStringNoShadow(fontRendererObj, s2, dx, dy+6, 4210752);

		for (int i = 0; i < 6; i++) {
			int dx2 = xSize/2-12;
			int dy2 = 20+i*12+3;
			int w = 53;
			int n = ReikaMathLibrary.intpow2(10, this.getPower(i));
			String sl = String.format("+%d%s", (int)ReikaMathLibrary.getThousandBase(n), ReikaEngLibrary.getSIPrefix(n));
			String sr = String.format("-%d%s", (int)ReikaMathLibrary.getThousandBase(n), ReikaEngLibrary.getSIPrefix(n));
			fontRendererObj.drawString(sl, dx2-w-fontRendererObj.getStringWidth(sl), dy2, 0);
			fontRendererObj.drawString(sr, dx2+w+24, dy2, 0);
		}


		ReikaGuiAPI.instance.drawCenteredStringNoShadow(fontRendererObj, "RF Cable Network", dx, 6, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		String i = "/Reika/ElectriCraft/Textures/GUI/rfcablegui.png";
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		ReikaTextureHelper.bindTexture(RotaryCraft.class, i);
		this.drawTexturedModalRect(j, k, 0, 0, xSize, ySize);
	}

}
