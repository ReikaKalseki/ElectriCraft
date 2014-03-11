/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ElectroCraft.Registry;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemBlock;
import Reika.DragonAPI.Interfaces.RegistryEnum;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;
import Reika.ElectroCraft.ElectroCraft;
import Reika.ElectroCraft.Blocks.BlockConverter;
import Reika.ElectroCraft.Blocks.BlockElectroOre;
import Reika.ElectroCraft.Blocks.BlockWire;
import Reika.ElectroCraft.Items.ItemBlockElectroOre;

public enum ElectroBlocks implements RegistryEnum {
	WIRE(BlockWire.class, "Wire", false),
	CONVERTER(BlockConverter.class, "Converter", false),
	ORE(BlockElectroOre.class, ItemBlockElectroOre.class, "ElectroOre", true);

	private Class blockClass;
	private String blockName;
	private Class itemBlock;
	private boolean model;

	public static final ElectroBlocks[] blockList = values();

	private ElectroBlocks(Class <? extends Block> cl, Class<? extends ItemBlock> ib, String n, boolean m) {
		blockClass = cl;
		blockName = n;
		itemBlock = ib;
		model = m;
	}

	private ElectroBlocks(Class <? extends Block> cl, String n) {
		this(cl, null, n, false);
	}

	private ElectroBlocks(Class <? extends Block> cl, String n, boolean m) {
		this(cl, null, n, m);
	}

	public int getBlockID() {
		return ElectroCraft.config.getBlockID(this.ordinal());
	}

	public Material getBlockMaterial() {
		return this == ORE ? Material.rock : Material.iron;
	}

	@Override
	public Class[] getConstructorParamTypes() {
		return new Class[]{int.class, Material.class};
	}

	@Override
	public Object[] getConstructorParams() {
		return new Object[]{this.getBlockID(), this.getBlockMaterial()};
	}

	@Override
	public String getUnlocalizedName() {
		return ReikaStringParser.stripSpaces(blockName);
	}

	@Override
	public Class getObjectClass() {
		return blockClass;
	}

	@Override
	public String getBasicName() {
		return blockName;
	}

	@Override
	public String getMultiValuedName(int meta) {
		switch(this) {
		default:
			return "";
		}
	}

	@Override
	public boolean hasMultiValuedName() {
		return true;
	}

	@Override
	public int getNumberMetadatas() {
		switch(this) {
		default:
			return 1;
		}
	}

	@Override
	public Class<? extends ItemBlock> getItemBlock() {
		return itemBlock;
	}

	@Override
	public boolean hasItemBlock() {
		return itemBlock != null;
	}

	@Override
	public String getConfigName() {
		return this.getBasicName();
	}

	@Override
	public int getDefaultID() {
		return 720+this.ordinal();
	}

	@Override
	public boolean isBlock() {
		return true;
	}

	@Override
	public boolean isItem() {
		return false;
	}

	@Override
	public String getCategory() {
		return "Electro Blocks";
	}

	public boolean isDummiedOut() {
		return blockClass == null;
	}

	public Block getBlockVariable() {
		return ElectroCraft.blocks[this.ordinal()];
	}

	public boolean isModelled() {
		return model;
	}

	public int getID() {
		return this.getBlockID();
	}

	@Override
	public boolean overwritingItem() {
		return false;
	}

}
