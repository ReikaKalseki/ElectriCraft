/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ElectriCraft.GUIs;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import Reika.DragonAPI.Libraries.IO.ReikaGuiAPI;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.ElectriCraft.Auxiliary.ElectriDescriptions;
import Reika.ElectriCraft.Registry.BatteryType;
import Reika.ElectriCraft.Registry.ElectriBook;
import Reika.ElectriCraft.Registry.ElectriTiles;
import Reika.ElectriCraft.Registry.WireType;
import Reika.ElectriCraft.TileEntities.TileEntityResistor;
import Reika.ElectriCraft.TileEntities.TileEntityResistor.ColorBand;
import Reika.ElectriCraft.TileEntities.TileEntityWire;
import Reika.RotaryCraft.Auxiliary.HandbookAuxData;
import Reika.RotaryCraft.Auxiliary.Interfaces.HandbookEntry;
import Reika.RotaryCraft.GUIs.GuiHandbook;

public class GuiElectriBook extends GuiHandbook {

	private static final Random rand = new Random();

	private ColorBand resistorB1 = ColorBand.BLACK;
	private ColorBand resistorB2 = ColorBand.BLACK;
	private ColorBand resistorB3 = ColorBand.BLACK;

	private int guiTick;

	public GuiElectriBook(EntityPlayer p5ep, World world, int s, int p) {
		super(p5ep, world, s, p);
	}

	@Override
	protected void reloadXMLData() {
		ElectriDescriptions.reload();
	}

	@Override
	protected void addTabButtons(int j, int k) {
		ElectriBook.addRelevantButtons(j, k, screen, buttonList);
	}

	@Override
	public int getMaxScreen() {
		return ElectriBook.MODDESC.getScreen()+ElectriBook.MODDESC.getNumberChildren()/GuiHandbook.PAGES_PER_SCREEN;
	}

	@Override
	public int getMaxSubpage() {
		ElectriBook h = ElectriBook.getFromScreenAndPage(screen, page);
		return h.isMachine() ? 1 : 0;
	}

	@Override
	protected int getNewScreenByTOCButton(int id) {
		switch(id) {
			case 1:
				return ElectriBook.INTRO.getScreen();
			case 2:
				return ElectriBook.CONVDESC.getScreen();
			case 3:
				return ElectriBook.TRANSDESC.getScreen();
			case 4:
				return ElectriBook.STORAGEDESC.getScreen();
			case 5:
				return ElectriBook.UTILDESC.getScreen();
			case 6:
				return ElectriBook.MODDESC.getScreen();
		}
		return 0;
	}

	@Override
	protected boolean isOnTOC() {
		return this.getEntry() == ElectriBook.TOC;
	}

	@Override
	protected void drawAuxData(int posX, int posY) {
		guiTick++;

		ElectriBook h = (ElectriBook)this.getEntry();
		if (h.isMachine()) {
			List<ItemStack> out = h.getItems(subpage);
			if (out == null || out.size() <= 0)
				return;
			if (h == ElectriBook.WIRES) {
				out.clear();
				WireType type = WireType.wireList[(int)(System.currentTimeMillis()/4000)%WireType.wireList.length];
				ItemStack is = System.currentTimeMillis()%4000 >= 2000 ? type.getCraftedInsulatedProduct() : type.getCraftedProduct();
				out.add(is);
			}
			ReikaGuiAPI.instance.drawCustomRecipes(ri, fontRendererObj, out, HandbookAuxData.getWorktable(), posX+72-18, posY+18, posX-1620, posY+32);
		}
		if (this.getGuiLayout() == PageType.CRAFTING) {
			List<ItemStack> out = ReikaJavaLibrary.makeListFrom(h.getItem().getStackOf());
			if (out == null || out.size() <= 0)
				return;
			ReikaGuiAPI.instance.drawCustomRecipes(ri, fontRendererObj, out, CraftingManager.getInstance().getRecipeList(), posX+72, posY+18, posX+162, posY+32);
		}
		/*
		if (h == ElectriBook.MAGNET) {
			ItemStack in = ElectriStacks.lodestone;
			ItemStack out = ElectriItems.MAGNET.getStackOf();
			int k = (int)((System.nanoTime()/2000000000)%ElectriItems.MAGNET.getNumberMetadatas());
			if (k != 0) {
				in = ElectriItems.MAGNET.getStackOfMetadata(k-1);
				out = ElectriItems.MAGNET.getStackOfMetadata(k);
			}
			MachineRecipeRenderer.instance.drawCompressor(posX+66, posY+14, in, posX+120, posY+41, out);
		}
		if (h == ElectriBook.PELLET) {
			ItemStack in = CraftingItems.GRAPHITE.getItem();
			ItemStack in2 = CraftingItems.UDUST.getItem();
			ItemStack out = ElectriItems.PELLET.getStackOf();
			BlastCrafting r = RecipesBlastFurnace.getRecipes().getAllCraftingMaking(out).get(0);
			MachineRecipeRenderer.instance.drawBlastFurnaceCrafting(posX+99, posY+18, posX+180, posY+32, r);
			ReikaGuiAPI.instance.drawCenteredStringNoShadow(fontRendererObj, r.temperature+"C", posX+56, posY+66, 0);
		}
		 */
	}

	@Override
	protected void doRenderMachine(double x, double y, HandbookEntry he) {
		ElectriBook h = (ElectriBook)he;
		ElectriTiles et = h.getMachine();
		if (et != null) {
			TileEntity te = et.createTEInstanceForRender();
			double sc = 48;
			int r = (int)(System.nanoTime()/20000000)%360;
			double a = 0;
			double b = 0;
			double c = 0;
			if (et.isWiring()) {
				double dx = -x;
				double dy = -y-21;
				double dz = 0;
				GL11.glTranslated(-dx, -dy, -dz);
				GL11.glScaled(sc, -sc, sc);
				GL11.glRotatef(renderq, 1, 0, 0);
				GL11.glRotatef(r, 0, 1, 0);
				GL11.glTranslated(a, b, c);
				if (te instanceof TileEntityWire) {
					TileEntityWire tw = (TileEntityWire)te;
					tw.setBlockMetadata((int)(System.currentTimeMillis()/4000)%WireType.wireList.length);
					if (System.currentTimeMillis()%4000 >= 2000)
						tw.insulated = true;
					else
						tw.insulated = false;
				}
				TileEntityRendererDispatcher.instance.renderTileEntityAt(te, -0.5, 0, -0.5, 0);
				GL11.glTranslated(-a, -b, -c);
				GL11.glRotatef(-r, 0, 1, 0);
				GL11.glRotatef(-renderq, 1, 0, 0);
				GL11.glTranslated(-dx, -dy, -dz);
				GL11.glScaled(1D/sc, -1D/sc, 1D/sc);
			}
			else if (et.hasRender() && et.getBlock().getRenderType() != 0) {
				double dx = -x;
				double dy = -y-21;
				double dz = 0;
				GL11.glTranslated(-dx, -dy, -dz);
				GL11.glScaled(sc, -sc, sc);
				GL11.glRotatef(renderq, 1, 0, 0);
				GL11.glRotatef(r, 0, 1, 0);
				GL11.glTranslated(a, b, c);
				if (te instanceof TileEntityResistor) {
					TileEntityResistor tr = (TileEntityResistor)te;
					if (guiTick%100 == 0) {
						this.recalcResistorColors();
					}
					tr.setColor(resistorB1, 1);
					tr.setColor(resistorB2, 2);
					tr.setColor(resistorB3, 3);
				}
				TileEntityRendererDispatcher.instance.renderTileEntityAt(te, -0.5, 0, -0.5, 0);
				if (te instanceof TileEntityResistor) {
					TileEntityResistor tr = (TileEntityResistor)te;
					tr.setColor(ColorBand.BLACK, 1);
					tr.setColor(ColorBand.BLACK, 2);
					tr.setColor(ColorBand.BLACK, 3);
				}
				GL11.glTranslated(-a, -b, -c);
				GL11.glRotatef(-r, 0, 1, 0);
				GL11.glRotatef(-renderq, 1, 0, 0);
				GL11.glTranslated(-dx, -dy, -dz);
				GL11.glScaled(1D/sc, -1D/sc, 1D/sc);
			}
			else {
				double dx = x;
				double dy = y;
				double dz = 0;
				GL11.glTranslated(dx, dy, dz);
				GL11.glScaled(sc, -sc, sc);
				GL11.glRotatef(renderq, 1, 0, 0);
				GL11.glRotatef(r, 0, 1, 0);
				ReikaTextureHelper.bindTerrainTexture();
				GL11.glTranslated(a, b, c);
				int meta = et.getBlockMetadata();
				if (et == ElectriTiles.BATTERY) {
					meta = (int)(-1+System.currentTimeMillis()/2000)%BatteryType.batteryList.length;
				}
				rb.renderBlockAsItem(et.getBlockInstance(), meta, 1);
				GL11.glTranslated(-a, -b, -c);
				GL11.glRotatef(-r, 0, 1, 0);
				GL11.glRotatef(-renderq, 1, 0, 0);
				GL11.glScaled(1D/sc, -1D/sc, 1D/sc);
				GL11.glTranslated(-dx, -dy, -dz);
			}
		}
	}

	private void recalcResistorColors() {
		resistorB1 = ColorBand.bandList[rand.nextInt(ColorBand.bandList.length)];
		resistorB2 = ColorBand.bandList[rand.nextInt(ColorBand.bandList.length)];
		resistorB3 = ColorBand.bandList[rand.nextInt(ColorBand.GRAY.ordinal())];
	}

	@Override
	protected void drawAuxGraphics(int posX, int posY, float ptick) {
		ElectriBook h = (ElectriBook)this.getEntry();
		ReikaGuiAPI api = ReikaGuiAPI.instance;

	}

	@Override
	protected HandbookEntry getEntry() {
		return ElectriBook.getFromScreenAndPage(screen, page);
	}

	@Override
	public boolean isLimitedView() {
		return false;
	}

	@Override
	protected PageType getGuiLayout() {
		ElectriBook h = (ElectriBook)this.getEntry();
		if (h.isParent())
			return PageType.PLAIN;
		if (subpage == 1)
			return PageType.PLAIN;
		if (h.isMachine())
			return PageType.MACHINERENDER;
		return PageType.PLAIN;
	}

	@Override
	protected void bindTexture() {
		ElectriBook h = (ElectriBook)this.getEntry();
		/*
		if (h == ElectriBook.FUSIONINFO) {
			ReikaTextureHelper.bindTexture(ElectriCraft.class, "Textures/GUI/Handbook/fusion.png");
		}
		else if (h == ElectriBook.FISSIONINFO) {
			ReikaTextureHelper.bindTexture(ElectriCraft.class, "Textures/GUI/Handbook/fission.png");
		}
		else*/
		super.bindTexture();
	}

	@Override
	public List<HandbookEntry> getAllTabsOnScreen() {
		List<ElectriBook> li = ElectriBook.getEntriesForScreen(screen);
		return new ArrayList(li);
	}

}
