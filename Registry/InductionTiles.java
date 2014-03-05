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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import Reika.DragonAPI.Exception.RegistrationException;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.RotaryCraft.Auxiliary.WorktableRecipes;
import Reika.RotaryCraft.Registry.ConfigRegistry;
import Reika.RotationalInduction.Induction;
import Reika.RotationalInduction.TileEntities.TileEntityGenerator;
import Reika.RotationalInduction.TileEntities.TileEntityMotor;
import Reika.RotationalInduction.TileEntities.TileEntityWire;
import cpw.mods.fml.common.registry.GameRegistry;

public enum InductionTiles {

	WIRE("induction.wire", InductionBlocks.WIRE,	TileEntityWire.class, 0, "RenderWire"),
	GENERATOR("induction.generator", InductionBlocks.CONVERTER, TileEntityGenerator.class, 0),
	MOTOR("induction.motor", InductionBlocks.CONVERTER, TileEntityMotor.class, 1);

	private String name;
	private final Class teClass;
	private int meta;
	private String render;
	private final InductionBlocks blockInstance;

	private static final HashMap<List<Integer>, InductionTiles> machineMappings = new HashMap();

	public static final InductionTiles[] TEList = values();

	private InductionTiles(String n, InductionBlocks block, Class<? extends TileEntity> tile, int m) {
		this(n, block, tile, m, null);
	}

	private InductionTiles(String n, InductionBlocks block, Class<? extends TileEntity> tile, int m, String r) {
		teClass = tile;
		name = n;
		render = r;
		meta = m;
		blockInstance = block;
	}

	public String getName() {
		return StatCollector.translateToLocal(name);
	}

	public Class getTEClass() {
		return teClass;
	}

	public static ArrayList<InductionTiles> getTilesOfBlock(InductionBlocks b) {
		ArrayList li = new ArrayList();
		for (int i = 0; i < TEList.length; i++) {
			if (TEList[i].blockInstance == b)
				li.add(TEList[i]);
		}
		return li;
	}

	public static TileEntity createTEFromIDAndMetadata(int id, int meta) {
		if (id == InductionBlocks.WIRE.getBlockID())
			return new TileEntityWire();
		InductionTiles index = getMachineFromIDandMetadata(id, meta);
		if (index == null) {
			Induction.logger.logError("ID "+id+" and metadata "+meta+" are not a valid machine identification pair!");
			return null;
		}
		Class TEClass = index.teClass;
		try {
			return (TileEntity)TEClass.newInstance();
		}
		catch (InstantiationException e) {
			e.printStackTrace();
			throw new RegistrationException(Induction.instance, "ID "+id+" and Metadata "+meta+" failed to instantiate its TileEntity of "+TEClass);
		}
		catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new RegistrationException(Induction.instance, "ID "+id+" and Metadata "+meta+" failed illegally accessed its TileEntity of "+TEClass);
		}
	}

	public static InductionTiles getMachineFromIDandMetadata(int id, int meta) {
		return machineMappings.get(Arrays.asList(id, meta));
	}

	public boolean isAvailableInCreativeInventory() {
		return true;
	}

	public boolean hasCustomItem() {
		return this == WIRE;
	}

	public static InductionTiles getTE(IBlockAccess iba, int x, int y, int z) {
		int id = iba.getBlockId(x, y, z);
		if (id == InductionBlocks.WIRE.getBlockID())
			return WIRE;
		int meta = iba.getBlockMetadata(x, y, z);
		return getMachineFromIDandMetadata(id, meta);
	}

	public ItemStack getCraftedProduct() {
		return new ItemStack(InductionItems.PLACER.getShiftedID(), 1, this.ordinal());
	}

	public TileEntity createTEInstanceForRender() {
		try {
			return (TileEntity)teClass.newInstance();
		}
		catch (InstantiationException e) {
			e.printStackTrace();
			throw new RegistrationException(Induction.instance, "Could not create TE instance to render "+this);
		}
		catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new RegistrationException(Induction.instance, "Could not create TE instance to render "+this);
		}
	}

	public boolean hasRender() {
		return render != null;
	}

	public String getRenderer() {
		if (!this.hasRender())
			throw new RuntimeException("Machine "+name+" has no render to call!");
		return "Reika.RotationalInduction.Renders."+render;
	}

	public int getBlockID() {
		return this.getBlockVariable().blockID;
	}

	public Block getBlockVariable() {
		return blockInstance.getBlockVariable();
	}

	public int getBlockMetadata() {
		return meta;
	}

	public boolean renderInPass1() {
		switch(this) {
		default:
			return false;
		}
	}

	public boolean isDummiedOut() {
		return false;
	}

	public void addRecipe(IRecipe ir) {
		if (!this.isDummiedOut()) {
			WorktableRecipes.getInstance().addRecipe(ir);
			if (ConfigRegistry.TABLEMACHINES.getState()) {
				GameRegistry.addRecipe(ir);
			}
		}
	}

	public void addRecipe(ItemStack is, Object... obj) {
		if (!this.isDummiedOut()) {
			WorktableRecipes.getInstance().addRecipe(is, obj);
			if (ConfigRegistry.TABLEMACHINES.getState()) {
				GameRegistry.addRecipe(is, obj);
			}
		}
	}

	public void addCrafting(Object... obj) {
		if (!this.isDummiedOut()) {
			WorktableRecipes.getInstance().addRecipe(this.getCraftedProduct(), obj);
			if (ConfigRegistry.TABLEMACHINES.getState()) {
				GameRegistry.addRecipe(this.getCraftedProduct(), obj);
			}
		}
	}

	public void addSizedCrafting(int num, Object... obj) {
		if (!this.isDummiedOut()) {
			WorktableRecipes.getInstance().addRecipe(ReikaItemHelper.getSizedItemStack(this.getCraftedProduct(), num), obj);
			if (ConfigRegistry.TABLEMACHINES.getState()) {
				GameRegistry.addRecipe(ReikaItemHelper.getSizedItemStack(this.getCraftedProduct(), num), obj);
			}
		}
	}

	public static void loadMappings() {
		for (int i = 0; i < InductionTiles.TEList.length; i++) {
			InductionTiles r = InductionTiles.TEList[i];
			int id = r.getBlockID();
			int meta = r.getBlockMetadata();
			List<Integer> li = Arrays.asList(id, meta);
			machineMappings.put(li, r);
		}
	}

	public String getPlaceSound() {
		switch(this) {
		case WIRE:
			return "step.cloth";
		default:
			return "step.stone";
		}
	}
}
