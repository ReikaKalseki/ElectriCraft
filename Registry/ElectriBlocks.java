/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ElectriCraft.Registry;

import Reika.DragonAPI.Interfaces.BlockEnum;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;
import Reika.ElectriCraft.ElectriCraft;
import Reika.ElectriCraft.Blocks.BlockElectriOre;
import Reika.ElectriCraft.Blocks.BlockElectricBattery;
import Reika.ElectriCraft.Blocks.BlockElectricMachine;
import Reika.ElectriCraft.Blocks.BlockRFCable;
import Reika.ElectriCraft.Blocks.BlockWire;
import Reika.ElectriCraft.Items.ItemBlockElectriOre;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;

public enum ElectriBlocks implements BlockEnum {
	WIRE(BlockWire.class, "Wire", false),
	MACHINE(BlockElectricMachine.class, "Converter", false),
	ORE(BlockElectriOre.class, ItemBlockElectriOre.class, "ElectriOre", true),
	BATTERY(BlockElectricBattery.class, "ElectriBattery", true),
	CABLE(BlockRFCable.class, "RFCable", false);

	private Class blockClass;
	private String blockName;
	private Class itemBlock;
	private boolean model;

	public static final ElectriBlocks[] blockList = values();

	private ElectriBlocks(Class <? extends Block> cl, Class<? extends ItemBlock> ib, String n, boolean m) {
		blockClass = cl;
		blockName = n;
		itemBlock = ib;
		model = m;
	}

	private ElectriBlocks(Class <? extends Block> cl, String n) {
		this(cl, null, n, false);
	}

	private ElectriBlocks(Class <? extends Block> cl, String n, boolean m) {
		this(cl, null, n, m);
	}

	public Block getBlockInstance() {
		return ElectriCraft.blocks[this.ordinal()];
	}

	public Material getBlockMaterial() {
		return this == ORE ? Material.rock : Material.iron;
	}

	@Override
	public Class[] getConstructorParamTypes() {
		return new Class[]{Material.class};
	}

	@Override
	public Object[] getConstructorParams() {
		return new Object[]{this.getBlockMaterial()};
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

	public boolean isDummiedOut() {
		return blockClass == null;
	}

	public boolean isModelled() {
		return model;
	}

	public Item getItem() {
		return Item.getItemFromBlock(this.getBlockInstance());
	}

}