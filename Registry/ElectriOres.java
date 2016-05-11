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
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.oredict.OreDictionary;
import Reika.DragonAPI.Interfaces.Registry.OreEnum;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.ModInteract.ReikaTwilightHelper;
import Reika.ElectriCraft.ElectriCraft;

public enum ElectriOres implements OreEnum {

	COPPER(		32, 64, 	6, 	8, 	0, 	0.5F,	1,	"ore.copper"),
	TIN(		48, 72, 	8, 	8,	0, 	0.2F,	1,	"ore.tin"),
	SILVER(		0, 32, 		6, 	4, 	0, 	0.8F,	2,	"ore.silver"),
	NICKEL(		16, 48, 	8, 	6, 	0, 	0.3F,	1,	"ore.nickel"),
	ALUMINUM(	60, 128, 	8, 	10,	0, 	0.4F,	1,	"ore.aluminum"),
	PLATINUM(	0, 16, 		4, 	2, 	0, 	1F,		2,	"ore.platinum");

	public final int minY;
	public final int maxY;
	public final int veinSize;
	public final int perChunk;
	public final int dimensionID;
	public final String oreName;
	public final int harvestLevel;
	public final float xpDropped;

	public static final ElectriOres[] oreList = values();

	private static final EnumMap<ElectriOres, Boolean> equivalents = new EnumMap(ElectriOres.class);

	private ElectriOres(int min, int max, int size, int count, int dim, float xp, int level, String name) {
		minY = min;
		maxY = max;
		veinSize = size;
		perChunk = count;
		dimensionID = dim;
		oreName = StatCollector.translateToLocal(name);
		harvestLevel = level;
		xpDropped = xp;
	}

	@Override
	public String toString() {
		return this.name()+" "+perChunk+"x"+veinSize+" between "+minY+" and "+maxY;
	}

	public static ElectriOres getOre(IBlockAccess iba, int x, int y, int z) {
		Block id = iba.getBlock(x, y, z);
		int meta = iba.getBlockMetadata(x, y, z);
		if (id != ElectriBlocks.ORE.getBlockInstance())
			return null;
		return oreList[meta];
	}

	public static ElectriOres getOre(Block id, int meta) {
		if (id != ElectriBlocks.ORE.getBlockInstance())
			return null;
		return meta < oreList.length ? oreList[meta] : null;
	}

	public String getTextureName() {
		return "ElectriCraft:ore"+ReikaStringParser.capFirstChar(this.name());
	}

	public String getDictionaryName() {
		return "ore"+ReikaStringParser.capFirstChar(this.name());
	}

	public String getProductDictionaryName() {
		switch(this) {
			default:
				return "ingot"+ReikaStringParser.capFirstChar(this.name());
		}
	}

	public Block getBlock() {
		return ElectriBlocks.ORE.getBlockInstance();
	}


	public int getBlockMetadata() {
		return this.ordinal();
	}

	public ItemStack getOreBlock() {
		return new ItemStack(this.getBlock(), 1, this.getBlockMetadata());
	}

	public ItemStack getProduct() {
		return ElectriItems.INGOTS.getStackOfMetadata(this.ordinal());
	}

	public List<ItemStack> getOreDrop(int meta) {
		return ReikaJavaLibrary.makeListFrom(new ItemStack(ElectriBlocks.ORE.getBlockInstance(), 1, meta));
	}

	public String getProductName() {
		switch(this) {
			default:
				return StatCollector.translateToLocal("Items."+this.name().toLowerCase(Locale.ENGLISH));
		}
	}

	public Block getReplaceableBlock() {
		switch(dimensionID) {
			case 0:
				return Blocks.stone;
			case 1:
				return Blocks.end_stone;
			case -1:
				return Blocks.netherrack;
			case 7:
				return Blocks.stone;
			default:
				return Blocks.stone;
		}
	}

	public boolean isValidDimension(int id) {
		if (id == dimensionID)
			return true;
		if (id == ReikaTwilightHelper.getDimensionID() && dimensionID == 0)
			return true;
		if (dimensionID == 0 && id != -1 && id != 1)
			return true;
		return false;
	}

	public boolean isValidBiome(BiomeGenBase biome) {
		switch(this) {
			default:
				return true;
		}
	}

	public boolean canGenerateInChunk(World world, int chunkX, int chunkZ) {
		int id = world.provider.dimensionId;
		if (!this.shouldGen())
			return false;
		if (!this.isValidDimension(id))
			return false;
		return this.isValidBiome(world.getBiomeGenForCoords(chunkX, chunkZ)) || id == ReikaTwilightHelper.getDimensionID();
	}

	private boolean shouldGen() {
		return true;
	}

	public boolean canGenAt(World world, int x, int y, int z) {
		return this.isOreEnabled();
	}

	public boolean isOreEnabled() {
		if (ElectriCraft.config.isOreGenEnabled(this))
			return true;
		if (!this.hasEquivalents())
			return true;
		return false;
	}

	public boolean hasEquivalents() {
		Boolean b = equivalents.get(this);
		if (b == null) {
			ArrayList<ItemStack> li = OreDictionary.getOres(this.getDictionaryName());
			boolean flag = false;
			for (int i = 0; i < li.size() && !flag; i++) {
				ItemStack is = li.get(i);
				if (!ReikaItemHelper.matchStacks(is, this.getOreBlock())) {
					b = true;
					flag = true;
				}
			}
			b = flag;
			equivalents.put(this, b);
		}
		return b.booleanValue();
	}

	public boolean dropsSelf(int meta) {
		List<ItemStack> li = this.getOreDrop(meta);
		return li.size() == 1 && ReikaItemHelper.matchStackWithBlock(li.get(0), ElectriBlocks.ORE.getBlockInstance()) && li.get(0).getItemDamage() == meta;
	}

	@Override
	public int getHarvestLevel() {
		return harvestLevel;
	}

	@Override
	public float getXPDropped(World world, int x, int y, int z) {
		return xpDropped;
	}

	@Override
	public boolean dropsSelf(World world, int x, int y, int z) {
		return this.dropsSelf(world.getBlockMetadata(x, y, z));
	}

	@Override
	public String getHarvestTool() {
		return "pickaxe";
	}

	@Override
	public boolean enforceHarvestLevel() {
		return false;
	}

	@Override
	public TileEntity getTileEntity(World world, int x, int y, int z) {
		return null;
	}

	@Override
	public int getRandomGeneratedYCoord(World world, int posX, int posZ, Random random) {
		return minY+random.nextInt(maxY-minY+1);
	}
}
