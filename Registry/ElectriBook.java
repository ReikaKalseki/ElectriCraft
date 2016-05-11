/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ElectriCraft.Registry;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import Reika.DragonAPI.Instantiable.GUI.ImagedGuiButton;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.ElectriCraft.Auxiliary.ElectriBookData;
import Reika.ElectriCraft.Auxiliary.ElectriDescriptions;
import Reika.RotaryCraft.RotaryCraft;
import Reika.RotaryCraft.Auxiliary.Interfaces.HandbookEntry;
import Reika.RotaryCraft.GUIs.GuiHandbook;

public enum ElectriBook implements HandbookEntry {

	//---------------------TOC--------------------//
	TOC("Table Of Contents", ""),
	INFO("Info", ElectriItems.BOOK.getStackOf()),
	CONVERSION("Conversion", ElectriTiles.GENERATOR.getCraftedProduct()),
	TRANSPORT("Transport", WireType.SUPERCONDUCTOR.getCraftedProduct()),
	STORAGE("Storage", BatteryType.STAR.getCraftedProduct()),
	UTILITY("Utility", ElectriTiles.TRANSFORMER.getCraftedProduct()),
	MODINTERFACE("Mod Interaction", ElectriTiles.CABLE.getCraftedProduct()),
	//---------------------INFO--------------------//
	INTRO("Introduction", ""),
	PHYSICS("Electric Physics", Items.book),
	SOURCESINK("Sources and Sinks", WireType.GOLD.getCraftedInsulatedProduct()),
	NETWORKS("Electric Networks", WireType.SUPERCONDUCTOR.getCraftedInsulatedProduct()),
	LIMITS("Limits", WireType.COPPER.getCraftedProduct()),

	//--------------------PROCESSING---------------//
	CONVDESC("Conversion Machines", ""),
	GENERATOR(ElectriTiles.GENERATOR),
	MOTOR(ElectriTiles.MOTOR),

	TRANSDESC("Electrical Transport", ""),
	WIRES(ElectriTiles.WIRE),
	RELAY(ElectriTiles.RELAY),
	RESISTOR(ElectriTiles.RESISTOR),

	STORAGEDESC("Electrical Storage", ""),
	BATTERY(ElectriTiles.BATTERY),

	UTILDESC("Utility Blocks", ""),
	TRANSFORMER(ElectriTiles.TRANSFORMER),
	METER(ElectriTiles.METER),

	MODDESC("Mod Interaction Devices", ""),
	RFBATT(ElectriTiles.RFBATTERY),
	RFCABLE(ElectriTiles.CABLE),
	EUSPLITTER(ElectriTiles.EUSPLIT);

	private final ItemStack iconItem;
	private final String pageTitle;
	private boolean isParent = false;
	private ElectriTiles machine;
	private ElectriItems item;

	public static final ElectriBook[] tabList = values();

	private ElectriBook() {
		this("");
	}

	private ElectriBook(ElectriTiles r) {
		this(r.getName(), r.getCraftedProduct());
		machine = r;
	}

	private ElectriBook(ElectriItems i) {
		this(i.getBasicName(), i.getStackOf());
		item = i;
	}


	private ElectriBook(ItemStack item) {
		this("", item);
	}

	private ElectriBook(String name, String s) {
		this(name);
		isParent = true;
	}

	private ElectriBook(String name) {
		this(name, (ItemStack)null);
	}

	private ElectriBook(String name, ElectriItems i) {
		this(name, i.getStackOf());
	}

	private ElectriBook(String name, ElectriTiles r) {
		this(name, r.getCraftedProduct());
	}

	private ElectriBook(String name, Item icon) {
		this(name, new ItemStack(icon));
	}

	private ElectriBook(String name, Block icon) {
		this(name, new ItemStack(icon));
	}

	private ElectriBook(String name, ItemStack icon) {
		iconItem = icon;
		pageTitle = name;
	}

	public static ElectriBook getFromScreenAndPage(int screen, int page) {
		//ReikaJavaLibrary.pConsole(screen+"   "+page);
		if (screen < INTRO.getScreen())
			return TOC;
		ElectriBook h = ElectriBookData.getMapping(screen, page);
		return h != null ? h : TOC;
	}

	public static void addRelevantButtons(int j, int k, int screen, List<GuiButton> li) {
		int id = 0;
		for (int i = 0; i < tabList.length; i++) {
			if (tabList[i].getScreen() == screen) {
				li.add(new ImagedGuiButton(id, j-20, k+id*20, 20, 20, 0, 0, tabList[i].getTabImageFile(), RotaryCraft.class));
				//ReikaJavaLibrary.pConsole("Adding "+tabList[i]+" with ID "+id+" to screen "+screen);
				id++;
			}
		}
	}

	public static List<ElectriBook> getEntriesForScreen(int screen) {
		//ReikaJavaLibrary.pConsole(screen);
		List<ElectriBook> li = new ArrayList<ElectriBook>();
		for (int i = 0; i < tabList.length; i++) {
			if (tabList[i].getScreen() == screen) {
				li.add(tabList[i]);
			}
		}
		return li;
	}

	public static List<ElectriBook> getTOCTabs() {
		ArrayList<ElectriBook> li = new ArrayList<ElectriBook>();
		for (int i = 0; i < tabList.length; i++) {
			if (tabList[i].isParent && tabList[i] != TOC)
				li.add(tabList[i]);
		}
		return li;
	}

	public static List<ElectriBook> getMachineTabs() {
		List<ElectriBook> tabs = new ArrayList<ElectriBook>();
		for (int i = 0; i < tabList.length; i++) {
			ElectriBook h = tabList[i];
			if (h.isMachine() && !h.isParent)
				tabs.add(h);
		}
		return tabs;
	}

	public static ElectriBook[] getInfoTabs() {
		int size = CONVDESC.ordinal()-INTRO.ordinal()-1;
		ElectriBook[] tabs = new ElectriBook[size];
		System.arraycopy(tabList, INTRO.ordinal()+1, tabs, 0, size);
		return tabs;
	}

	public static List<ElectriBook> getCategoryTabs() {
		ArrayList<ElectriBook> li = new ArrayList<ElectriBook>();
		for (int i = 0; i < tabList.length; i++) {
			if (tabList[i].isParent && tabList[i] != TOC)
				li.add(tabList[i]);
		}
		return li;
	}

	public boolean isMachine() {
		return machine != null;
	}

	public ElectriTiles getMachine() {
		return machine;
	}

	public ElectriItems getItem() {
		return item;
	}

	@Override
	public ItemStack getTabIcon() {
		return iconItem;
	}

	public String getData() {
		if (this == TOC)
			return ElectriDescriptions.getTOC();
		return ElectriDescriptions.getData(this);
	}

	public String getNotes(int subpage) {
		return ElectriDescriptions.getNotes(this);
	}

	@Override
	public boolean sameTextAllSubpages() {
		return false;
	}

	@Override
	public String getTitle() {
		if (this == WIRES)
			return "Wires";
		if (this == BATTERY)
			return "Batteries";
		return pageTitle;
	}

	@Override
	public boolean hasMachineRender() {
		return this.isMachine();
	}

	@Override
	public boolean hasSubpages() {
		return this.isMachine();
	}

	public String getTabImageFile() {
		//return "/Reika/RotaryCraft/Textures/GUI/Handbook/tabs_"+this.getParent().name().toLowerCase()+".png";
		return "/Reika/RotaryCraft/Textures/GUI/Handbook/tabs_"+TOC.name().toLowerCase(Locale.ENGLISH)+".png";
	}

	public int getRelativeScreen() {
		int offset = this.ordinal()-this.getParent().ordinal();
		return offset/GuiHandbook.PAGES_PER_SCREEN;
	}

	public ElectriBook getParent() {
		ElectriBook parent = null;
		for (int i = 0; i < tabList.length; i++) {
			if (tabList[i].isParent) {
				if (this.ordinal() >= tabList[i].ordinal()) {
					parent = tabList[i];
				}
			}
		}
		//ReikaJavaLibrary.pConsole("Setting parent for "+this+" to "+parent);
		return parent;
	}

	public boolean isParent() {
		return isParent;
	}

	public int getBaseScreen() {
		int sc = 0;
		for (int i = 0; i < this.ordinal(); i++) {
			ElectriBook h = tabList[i];
			if (h.isParent) {
				sc += h.getNumberChildren()/GuiHandbook.PAGES_PER_SCREEN+1;
			}
		}
		return sc;
	}

	public int getNumberChildren() {
		if (!isParent)
			return 0;
		int ch = 0;
		for (int i = this.ordinal()+1; i < tabList.length; i++) {
			ElectriBook h = tabList[i];
			if (h.isParent) {
				return ch;
			}
			else {
				ch++;
			}
		}
		return ch;
	}

	public int getRelativePage() {
		int offset = this.ordinal()-this.getParent().ordinal();
		return offset;
	}

	public int getRelativeTabPosn() {
		int offset = this.ordinal()-this.getParent().ordinal();
		return offset-this.getRelativeScreen()*GuiHandbook.PAGES_PER_SCREEN;
	}

	public int getScreen() {
		return this.getParent().getBaseScreen()+this.getRelativeScreen();
	}

	public int getPage() {
		return (this.ordinal()-this.getParent().ordinal())%GuiHandbook.PAGES_PER_SCREEN;
	}

	@Override
	public boolean isConfigDisabled() {
		return false;
	}

	public List<ItemStack> getItems(int subpage) {
		if (this == WIRES) {
			ArrayList<ItemStack> li = new ArrayList();
			for (int i = 0; i < WireType.wireList.length; i++) {
				WireType w = WireType.wireList[i];
				li.add(w.getCraftedProduct());
				li.add(w.getCraftedInsulatedProduct());
			}
			return li;
		}
		if (this == BATTERY) {
			ArrayList<ItemStack> li = new ArrayList();
			for (int i = 0; i < BatteryType.batteryList.length; i++) {
				BatteryType w = BatteryType.batteryList[i];
				li.add(w.getCraftedProduct());
			}
			return li;
		}
		return ReikaJavaLibrary.makeListFrom(this.getMachine().getCraftedProduct());
	}

}
