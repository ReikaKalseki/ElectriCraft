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

import java.util.Locale;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.StatCollector;
import net.minecraftforge.oredict.ShapedOreRecipe;
import Reika.DragonAPI.Exception.RegistrationException;
import Reika.DragonAPI.Interfaces.Registry.ItemEnum;
import Reika.DragonAPI.Libraries.ReikaRecipeHelper;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.ElectriCraft.ElectriCraft;
import Reika.ElectriCraft.Base.ElectriItemBase;
import Reika.ElectriCraft.Items.ItemBatteryPlacer;
import Reika.ElectriCraft.Items.ItemElectriBook;
import Reika.ElectriCraft.Items.ItemElectriPlacer;
import Reika.ElectriCraft.Items.ItemRFBatteryPlacer;
import Reika.ElectriCraft.Items.ItemWirePlacer;
import Reika.RotaryCraft.Auxiliary.RecipeManagers.RecipeHandler.RecipeLevel;
import Reika.RotaryCraft.Auxiliary.RecipeManagers.WorktableRecipes;
import cpw.mods.fml.common.registry.GameRegistry;

public enum ElectriItems implements ItemEnum {

	PLACER(0, false, "item.placer", ItemElectriPlacer.class),
	INGOTS(16, true, "item.Electriingots", ElectriItemBase.class),
	WIRE(1, true, "machine.wire", ItemWirePlacer.class),
	BATTERY(2, true, "machine.battery", ItemBatteryPlacer.class),
	CRAFTING(32, true, "item.crafting", ElectriItemBase.class),
	CRYSTAL(48, true, "item.electricrystal", ElectriItemBase.class),
	RFBATTERY(3, false, "machine.rfbattery", ItemRFBatteryPlacer.class),
	BOOK(4,	false, "item.electribook", ItemElectriBook.class);

	private int index;
	private boolean hasSubtypes;
	private String name;
	private Class itemClass;

	private int maxindex;

	private ElectriItems(int tex, boolean sub, String n, Class <?extends Item> iCl) {
		index = tex;
		hasSubtypes = sub;
		name = n;
		itemClass = iCl;
	}

	public static final ElectriItems[] itemList = values();

	public Class[] getConstructorParamTypes() {
		return new Class[]{int.class}; // ID, Sprite index
	}

	public Object[] getConstructorParams() {
		return new Object[]{this.getTextureIndex()};
	}

	public int getTextureIndex() {
		return index;
	}

	public static boolean isRegistered(ItemStack is) {
		return isRegistered(is.getItem());
	}

	public static boolean isRegistered(Item id) {
		for (int i = 0; i < itemList.length; i++) {
			if (itemList[i].getItemInstance() == id)
				return true;
		}
		return false;
	}

	public static ElectriItems getEntryByID(Item id) {
		for (int i = 0; i < itemList.length; i++) {
			if (itemList[i].getItemInstance() == id)
				return itemList[i];
		}
		//throw new RegistrationException(ElectriCraft.instance, "Item ID "+id+" was called to the item registry but does not exist there!");
		return null;
	}

	public static ElectriItems getEntry(ItemStack is) {
		if (is == null)
			return null;
		return getEntryByID(is.getItem());
	}

	public String getName(int dmg) {
		if (this.hasMultiValuedName())
			return this.getMultiValuedName(dmg);
		return name;
	}

	public String getBasicName() {
		return StatCollector.translateToLocal(name);
	}

	public String getMultiValuedName(int dmg) {
		if (!this.hasMultiValuedName())
			throw new RuntimeException("Item "+name+" was called for a multi-name, yet does not have one!");
		switch(this) {
			case INGOTS:
				return StatCollector.translateToLocal("ingot."+ElectriOres.oreList[dmg].name().toLowerCase(Locale.ENGLISH));
			case CRAFTING:
				return ElectriCrafting.craftingList[dmg].getName();
			case CRYSTAL:
				if (dmg == BatteryType.batteryList.length)
					return StatCollector.translateToLocal("energycrystal.rf");
				return StatCollector.translateToLocal("energycrystal."+BatteryType.batteryList[dmg].name().toLowerCase(Locale.ENGLISH));
			case BATTERY:
				return BatteryType.batteryList[dmg].getName();
			case PLACER:
				return ElectriTiles.TEList[dmg].getName();
			case WIRE:
				int d = dmg%WireType.INS_OFFSET;
				String s = dmg >= WireType.INS_OFFSET ? "wire.insulated." : "wire.";
				return d < WireType.wireList.length ? StatCollector.translateToLocal(s+WireType.wireList[d].name().toLowerCase(Locale.ENGLISH)) : "";
			default:
				throw new RuntimeException("Item "+name+" was called for a multi-name, but it was not registered!");
		}
	}

	public String getUnlocalizedName() {
		return ReikaStringParser.stripSpaces(name).toLowerCase(Locale.ENGLISH);
	}

	public Item getItemInstance() {
		return ElectriCraft.items[this.ordinal()];
	}

	public boolean hasMultiValuedName() {
		return hasSubtypes || this == PLACER;
	}

	public boolean isCreativeOnly() {
		return false;
	}

	public int getNumberMetadatas() {
		if (!hasSubtypes)
			return 1;
		switch(this) {
			case INGOTS:
				return ElectriOres.oreList.length;
			case WIRE:
				return WireType.INS_OFFSET*2;
			case CRAFTING:
				return ElectriCrafting.craftingList.length;
			case CRYSTAL:
				return BatteryType.batteryList.length+1;
			case BATTERY:
				return BatteryType.batteryList.length;
			default:
				throw new RegistrationException(ElectriCraft.instance, "Item "+name+" has subtypes but the number was not specified!");
		}
	}

	public ItemStack getCraftedProduct(int amt) {
		return new ItemStack(this.getItemInstance(), amt, 0);
	}

	public ItemStack getCraftedMetadataProduct(int amt, int meta) {
		return new ItemStack(this.getItemInstance(), amt, meta);
	}

	public ItemStack getStackOf() {
		return this.getCraftedProduct(1);
	}

	public ItemStack getStackOfMetadata(int meta) {
		return this.getCraftedMetadataProduct(1, meta);
	}

	public boolean overridesRightClick(ItemStack is) {
		switch(this) {
			default:
				return false;
		}
	}

	@Override
	public Class getObjectClass() {
		return itemClass;
	}

	public boolean isDummiedOut() {
		return itemClass == null;
	}

	public void addRecipe(Object... params) {
		if (!this.isDummiedOut()) {
			GameRegistry.addRecipe(this.getStackOf(), params);
			WorktableRecipes.getInstance().addRecipe(this.getStackOf(), RecipeLevel.CORE, params);
		}
	}

	public void addSizedRecipe(int num, Object... params) {
		if (!this.isDummiedOut()) {
			GameRegistry.addRecipe(this.getCraftedProduct(num), params);
			WorktableRecipes.getInstance().addRecipe(this.getCraftedProduct(num), RecipeLevel.CORE, params);
		}
	}

	public void addMetaRecipe(int meta, Object... params) {
		if (!this.isDummiedOut()) {
			GameRegistry.addRecipe(this.getStackOfMetadata(meta), params);
			WorktableRecipes.getInstance().addRecipe(this.getStackOfMetadata(meta), RecipeLevel.CORE, params);
		}
	}

	public void addSizedMetaRecipe(int meta, int num, Object... params) {
		if (!this.isDummiedOut()) {
			GameRegistry.addRecipe(this.getCraftedMetadataProduct(num, meta), params);
			WorktableRecipes.getInstance().addRecipe(this.getCraftedMetadataProduct(num, meta), RecipeLevel.CORE, params);
		}
	}

	public void addShapelessRecipe(Object... params) {
		if (!this.isDummiedOut()) {
			GameRegistry.addShapelessRecipe(this.getStackOf(), params);
			WorktableRecipes.getInstance().addShapelessRecipe(this.getStackOf(), RecipeLevel.CORE, params);
		}
	}

	public void addRecipe(IRecipe ir) {
		if (!this.isDummiedOut()) {
			GameRegistry.addRecipe(ir);
			WorktableRecipes.getInstance().addRecipe(ir, RecipeLevel.CORE);
		}
	}

	public void addOreRecipe(Object... in) {
		if (!this.isDummiedOut()) {
			ItemStack out = this.getStackOf();
			boolean added = ReikaRecipeHelper.addOreRecipe(out, in);
			if (added)
				WorktableRecipes.getInstance().addRecipe(new ShapedOreRecipe(out, in), RecipeLevel.CORE);
		}
	}

	public boolean isAvailableInCreativeInventory() {
		if (this.isDummiedOut())
			return false;
		return true;
	}

	public boolean isAvailableInCreative(ItemStack item) {
		switch(this) {
			case WIRE:
				if (item.getItemDamage() < WireType.wireList.length)
					return true;
				return ReikaMathLibrary.isValueInsideBoundsIncl(16, 16+WireType.wireList.length-1, item.getItemDamage());
			default:
				return true;
		}
	}

	@Override
	public boolean overwritingItem() {
		return false;
	}

	public boolean isPlacerItem() {
		switch(this) {
			case WIRE:
			case PLACER:
			case BATTERY:
			case RFBATTERY:
				return true;
			default:
				return false;
		}
	}
}
