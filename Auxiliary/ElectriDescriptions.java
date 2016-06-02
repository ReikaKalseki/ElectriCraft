/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ElectriCraft.Auxiliary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.Language;
import net.minecraftforge.common.MinecraftForge;
import Reika.DragonAPI.Instantiable.Event.Client.ResourceReloadEvent;
import Reika.DragonAPI.Instantiable.IO.XMLInterface;
import Reika.DragonAPI.Libraries.Java.ReikaObfuscationHelper;
import Reika.ElectriCraft.ElectriCraft;
import Reika.ElectriCraft.Network.WireNetwork;
import Reika.ElectriCraft.Registry.ElectriBook;
import Reika.ElectriCraft.Registry.ElectriTiles;
import Reika.ElectriCraft.Registry.WireType;
import Reika.ElectriCraft.TileEntities.TileEntityTransformer;
import Reika.ElectriCraft.TileEntities.ModInterface.TileEntityRFBattery;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public final class ElectriDescriptions {

	private static String PARENT = getParent();
	public static final String DESC_SUFFIX = ":desc";
	public static final String NOTE_SUFFIX = ":note";

	private static HashMap<ElectriBook, String> data = new HashMap<ElectriBook, String>();
	private static HashMap<ElectriBook, String> notes = new HashMap<ElectriBook, String>();

	private static HashMap<ElectriTiles, Object[]> machineData = new HashMap<ElectriTiles, Object[]>();
	private static HashMap<ElectriTiles, Object[]> machineNotes = new HashMap<ElectriTiles, Object[]>();
	private static HashMap<ElectriBook, Object[]> miscData = new HashMap<ElectriBook, Object[]>();

	private static ArrayList<ElectriBook> categories = new ArrayList<ElectriBook>();

	private static final boolean mustLoad = !ReikaObfuscationHelper.isDeObfEnvironment();
	private static final XMLInterface parents = new XMLInterface(ElectriCraft.class, PARENT+"categories.xml", mustLoad);
	private static final XMLInterface machines = new XMLInterface(ElectriCraft.class, PARENT+"machines.xml", mustLoad);
	private static final XMLInterface infos = new XMLInterface(ElectriCraft.class, PARENT+"info.xml", mustLoad);

	private static String getParent() {
		Language language = Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage();
		String lang = language.getLanguageCode();
		if (hasLocalizedFor(language) && !"en_US".equals(lang))
			return "Resources/"+lang+"/";
		return "Resources/";
	}

	private static boolean hasLocalizedFor(Language language) {
		String lang = language.getLanguageCode();
		Object o = ElectriCraft.class.getResourceAsStream("Resources/"+lang+"/categories.xml");
		return o != null;
	}

	public static String getTOC() {
		List<ElectriBook> toctabs = ElectriBook.getTOCTabs();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < toctabs.size(); i++) {
			ElectriBook h = toctabs.get(i);
			sb.append("Page ");
			sb.append(h.getScreen());
			sb.append(" - ");
			sb.append(h.getTitle());
			if (i < toctabs.size()-1)
				sb.append("\n");
		}
		return sb.toString();
	}

	private static void addData(ElectriTiles m, Object... data) {
		machineData.put(m, data);
	}

	private static void addData(ElectriBook h, Object... data) {
		miscData.put(h, data);
	}

	private static void addData(ElectriBook h, int[] data) {
		Object[] o = new Object[data.length];
		for (int i = 0; i < o.length; i++)
			o[i] = data[i];
		miscData.put(h, o);
	}

	private static void addNotes(ElectriTiles m, Object... data) {
		machineNotes.put(m, data);
	}

	public static void reload() {
		PARENT = getParent();

		data.clear();
		loadNumericalData();

		machines.reread();
		infos.reread();

		parents.reread();

		loadData();
	}

	private static void addEntry(ElectriBook h, String sg) {
		data.put(h, sg);
	}

	public static void loadData() {
		List<ElectriBook> parenttabs = ElectriBook.getCategoryTabs();

		List<ElectriBook> machinetabs = ElectriBook.getMachineTabs();
		ElectriBook[] infotabs = ElectriBook.getInfoTabs();

		for (int i = 0; i < parenttabs.size(); i++) {
			ElectriBook h = parenttabs.get(i);
			String desc = parents.getValueAtNode("categories:"+h.name().toLowerCase(Locale.ENGLISH));
			addEntry(h, desc);
		}

		for (int i = 0; i < machinetabs.size(); i++) {
			ElectriBook h = machinetabs.get(i);
			ElectriTiles m = h.getMachine();
			String desc = machines.getValueAtNode("machines:"+m.name().toLowerCase(Locale.ENGLISH)+DESC_SUFFIX);
			String aux = machines.getValueAtNode("machines:"+m.name().toLowerCase(Locale.ENGLISH)+NOTE_SUFFIX);
			desc = String.format(desc, machineData.get(m));
			aux = String.format(aux, machineNotes.get(m));

			if (XMLInterface.NULL_VALUE.equals(desc))
				desc = "There is no handbook data for this machine yet.";

			if (m.isDummiedOut()) {
				desc += "\nThis machine is currently unavailable.";
				aux += "\nNote: Dummied Out";
			}

			addEntry(h, desc);
			notes.put(h, aux);
		}

		for (int i = 0; i < infotabs.length; i++) {
			ElectriBook h = infotabs[i];
			String desc = infos.getValueAtNode("info:"+h.name().toLowerCase(Locale.ENGLISH));
			desc = String.format(desc, miscData.get(h));
			addEntry(h, desc);
		}
	}


	public static String getData(ElectriBook h) {
		if (!data.containsKey(h))
			return "";
		return data.get(h);
	}

	public static String getNotes(ElectriBook h) {
		if (!notes.containsKey(h))
			return "";
		return notes.get(h);
	}

	static {
		loadNumericalData();
		MinecraftForge.EVENT_BUS.register(new ReloadListener());
	}

	public static class ReloadListener {

		@SubscribeEvent
		public void reload(ResourceReloadEvent evt) {
			ElectriDescriptions.reload();
		}

	}

	private static void loadNumericalData() {
		addData(ElectriBook.LIMITS, WireType.getLimitsForDisplay());
		addNotes(ElectriTiles.GENERATOR, WireNetwork.TORQUE_PER_AMP, WireNetwork.TORQUE_PER_AMP);
		addData(ElectriTiles.TRANSFORMER, TileEntityTransformer.MAXTEMP, TileEntityTransformer.MAXCURRENT);
		addData(ElectriTiles.RFBATTERY, TileEntityRFBattery.CAPACITY);
	}
}
