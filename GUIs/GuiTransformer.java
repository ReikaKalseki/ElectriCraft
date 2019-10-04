/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ElectriCraft.GUIs;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;

import Reika.DragonAPI.Base.CoreContainer;
import Reika.DragonAPI.Libraries.IO.ReikaGuiAPI;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.ElectriCraft.ElectriCraft;
import Reika.ElectriCraft.Registry.ElectriPackets;
import Reika.ElectriCraft.TileEntities.TileEntityTransformer;
import Reika.RotaryCraft.RotaryCraft;

public class GuiTransformer extends GuiContainer {

	private final TileEntityTransformer trans;
	private GuiTextField input;
	private GuiTextField input2;

	private int n1;
	private int n2;

	public GuiTransformer(EntityPlayer ep, TileEntityTransformer te) {
		super(new CoreContainer(ep, te));
		trans = te;
		xSize = 197;
		ySize = 103;

		n1 = te.getN1();
		n2 = te.getN2();
	}

	@Override
	public void initGui() {
		super.initGui();
		int j = (width - xSize) / 2+8;
		int k = (height - ySize) / 2 - 12;
		input = new GuiTextField(fontRendererObj, j+xSize/2-65, k+40, 40, 16);
		input.setFocused(false);
		input.setMaxStringLength(4);
		input2 = new GuiTextField(fontRendererObj, j+xSize/2-10, k+40, 40, 16);
		input2.setFocused(false);
		input2.setMaxStringLength(4);
	}

	@Override
	protected void keyTyped(char c, int i){
		super.keyTyped(c, i);
		input.textboxKeyTyped(c, i);
		input2.textboxKeyTyped(c, i);
	}

	@Override
	protected void mouseClicked(int i, int j, int k){
		super.mouseClicked(i, j, k);
		input.mouseClicked(i, j, k);
		input2.mouseClicked(i, j, k);

		if (!input.isFocused() && !input2.isFocused())
			this.initGui();
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		boolean valid1 = true;
		boolean valid2 = true;
		if (input.getText().isEmpty() && input2.getText().isEmpty()) {
			return;
		}
		if (input.getText().isEmpty())
			valid1 = false;
		if (input2.getText().isEmpty())
			valid2 = false;
		if (!input.getText().isEmpty() && !(input.getText().matches("^[0-9 ]+$"))) {
			n1 = 0;
			input.deleteFromCursor(-1);
			valid1 = false;
		}
		if (!input2.getText().isEmpty() && !(input2.getText().matches("^[0-9 ]+$"))) {
			n2 = 0;
			input2.deleteFromCursor(-1);
			valid2 = false;
		}
		if (!valid1 && !valid2)
			return;
		//ModLoader.getMinecraftInstance().thePlayer.addChatMessage("435");
		//System.out.println(input.getText());
		if (valid1) {
			n1 = Math.max(0, ReikaJavaLibrary.safeIntParse(input.getText()));
		}
		if (valid2) {
			n2 = Math.max(0, ReikaJavaLibrary.safeIntParse(input2.getText()));
		}
		this.sendData();
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
	}

	private void sendData() {
		ReikaPacketHelper.sendPacketToServer(ElectriCraft.packetChannel, ElectriPackets.TRANSFORMER.ordinal(), trans, n1, n2);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		super.drawGuiContainerForegroundLayer(par1, par2);
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		int dx = xSize/2;
		int dy = ySize/2-4;

		String[] s = trans.getRatioForDisplay().split(":");
		if (!input.isFocused())
			fontRendererObj.drawString(s[0], dx-53, dy-15, 0xffffff);
		if (!input2.isFocused())
			fontRendererObj.drawString(s[1], dx+2, dy-15, 0xffffff);

		ReikaGuiAPI.instance.drawCenteredStringNoShadow(fontRendererObj, ":", dx-9, dy-15, 4210752);
		ReikaGuiAPI.instance.drawCenteredStringNoShadow(fontRendererObj, String.format("Efficiency: %.2f%%", 100*trans.getEfficiency()), dx-10, dy+6, 4210752);
		ReikaGuiAPI.instance.drawCenteredStringNoShadow(fontRendererObj, "Transformer", dx-8, 6, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		String i = "/Reika/ElectriCraft/Textures/GUI/transformergui.png";
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		ReikaTextureHelper.bindTexture(RotaryCraft.class, i);
		this.drawTexturedModalRect(j, k, 0, 0, xSize, ySize);

		input.drawTextBox();
		input2.drawTextBox();
	}

}
