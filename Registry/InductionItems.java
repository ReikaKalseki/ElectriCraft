/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.RotationalInduction.Registry;

import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.StatCollector;
import net.minecraftforge.oredict.ShapedOreRecipe;
import Reika.DragonAPI.Exception.RegistrationException;
import Reika.DragonAPI.Interfaces.RegistryEnum;
import Reika.DragonAPI.Libraries.ReikaRecipeHelper;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.RotaryCraft.Auxiliary.WorktableRecipes;
import Reika.RotationalInduction.Induction;
import Reika.RotationalInduction.Base.InductionItemBase;
import Reika.RotationalInduction.Items.ItemInductionPlacer;
import Reika.RotationalInduction.Items.ItemWirePlacer;
import cpw.mods.fml.common.registry.GameRegistry;

public enum InductionItems implements RegistryEnum {

	PLACER(0, false, "item.placer", ItemInductionPlacer.class),
	INGOTS(16, true, "item.inductioningots", InductionItemBase.class),
	WIRE(32, true, "item.inductionwire", ItemWirePlacer.class);

	private int index;
	private boolean hasSubtypes;
	private String name;
	private Class itemClass;

	private int maxindex;

	private InductionItems(int tex, boolean sub, String n, Class <?extends Item> iCl) {
		index = tex;
		hasSubtypes = sub;
		name = n;
		itemClass = iCl;
	}

	public static final InductionItems[] itemList = values();

	public Class[] getConstructorParamTypes() {
		return new Class[]{int.class, int.class}; // ID, Sprite index
	}

	public Object[] getConstructorParams() {
		return new Object[]{Induction.config.getItemID(this.ordinal()), this.getTextureIndex()};
	}

	public int getTextureIndex() {
		return index;
	}

	public static boolean isRegistered(ItemStack is) {
		return isRegistered(is.itemID);
	}

	public static boolean isRegistered(int id) {
		for (int i = 0; i < itemList.length; i++) {
			if (itemList[i].getShiftedID() == id)
				return true;
		}
		return false;
	}

	public static InductionItems getEntryByID(int id) {
		for (int i = 0; i < itemList.length; i++) {
			if (itemList[i].getShiftedID() == id)
				return itemList[i];
		}
		//throw new RegistrationException(RotationalInduction.instance, "Item ID "+id+" was called to the item registry but does not exist there!");
		return null;
	}

	public static InductionItems getEntry(ItemStack is) {
		if (is == null)
			return null;
		return getEntryByID(is.itemID);
	}

	public String getName(int dmg) {
		if (this.hasMultiValuedName())
			return this.getMultiValuedName(dmg);
		return name;
	}

	public String getBasicName() {
		String sg = name;
		if (name.startsWith("#"))
			sg = name.substring(1);
		return StatCollector.translateToLocal(sg);
	}

	public String getMultiValuedName(int dmg) {
		if (!this.hasMultiValuedName())
			throw new RuntimeException("Item "+name+" was called for a multi-name, yet does not have one!");
		if (this == INGOTS)
			return StatCollector.translateToLocal("ingot."+InductionOres.oreList[dmg].name().toLowerCase());
		throw new RuntimeException("Item "+name+" was called for a multi-name, but it was not registered!");
	}

	public String getUnlocalizedName() {
		return ReikaStringParser.stripSpaces(name).toLowerCase();
	}

	public int getID() {
		return Induction.config.getItemID(this.ordinal());
	}

	public int getShiftedID() {
		return Induction.config.getItemID(this.ordinal())+256;
	}

	public Item getItemInstance() {
		return Induction.items[this.ordinal()];
	}

	public boolean hasMultiValuedName() {
		return this == INGOTS;
	}

	public boolean isCreativeOnly() {
		return false;
	}

	public int getNumberMetadatas() {
		if (!hasSubtypes)
			return 1;
		switch(this) {
		case INGOTS:
			return InductionOres.oreList.length;
		case WIRE:
			return 64;
		default:
			throw new RegistrationException(Induction.instance, "Item "+name+" has subtypes but the number was not specified!");
		}
	}

	public ItemStack getCraftedProduct(int amt) {
		return new ItemStack(this.getShiftedID(), amt, 0);
	}

	public ItemStack getCraftedMetadataProduct(int amt, int meta) {
		return new ItemStack(this.getShiftedID(), amt, meta);
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

	@Override
	public Class<? extends ItemBlock> getItemBlock() {
		return null;
	}

	@Override
	public boolean hasItemBlock() {
		return false;
	}

	@Override
	public String getConfigName() {
		return this.getBasicName();
	}

	@Override
	public int getDefaultID() {
		return 15000+this.ordinal();
	}

	@Override
	public boolean isBlock() {
		return false;
	}

	@Override
	public boolean isItem() {
		return true;
	}

	@Override
	public String getCategory() {
		return "Item IDs";
	}

	public boolean isDummiedOut() {
		return itemClass == null;
	}

	public void addRecipe(Object... params) {
		if (!this.isDummiedOut()) {
			GameRegistry.addRecipe(this.getStackOf(), params);
			WorktableRecipes.getInstance().addRecipe(this.getStackOf(), params);
		}
	}

	public void addSizedRecipe(int num, Object... params) {
		if (!this.isDummiedOut()) {
			GameRegistry.addRecipe(this.getCraftedProduct(num), params);
			WorktableRecipes.getInstance().addRecipe(this.getCraftedProduct(num), params);
		}
	}

	public void addMetaRecipe(int meta, Object... params) {
		if (!this.isDummiedOut()) {
			GameRegistry.addRecipe(this.getStackOfMetadata(meta), params);
			WorktableRecipes.getInstance().addRecipe(this.getStackOfMetadata(meta), params);
		}
	}

	public void addSizedMetaRecipe(int meta, int num, Object... params) {
		if (!this.isDummiedOut()) {
			GameRegistry.addRecipe(this.getCraftedMetadataProduct(num, meta), params);
			WorktableRecipes.getInstance().addRecipe(this.getCraftedMetadataProduct(num, meta), params);
		}
	}

	public void addShapelessRecipe(Object... params) {
		if (!this.isDummiedOut()) {
			GameRegistry.addShapelessRecipe(this.getStackOf(), params);
			WorktableRecipes.getInstance().addShapelessRecipe(this.getStackOf(), params);
		}
	}

	public void addRecipe(IRecipe ir) {
		if (!this.isDummiedOut()) {
			GameRegistry.addRecipe(ir);
			WorktableRecipes.addRecipe(ir);
		}
	}

	public void addOreRecipe(Object... in) {
		if (!this.isDummiedOut()) {
			ItemStack out = this.getStackOf();
			boolean added = ReikaRecipeHelper.addOreRecipe(out, in);
			if (added)
				WorktableRecipes.addRecipe(new ShapedOreRecipe(out, in));
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
			return true;
		default:
			return false;
		}
	}
}
