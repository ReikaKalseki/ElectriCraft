/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ElectriCraft.Registry;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.oredict.ShapedOreRecipe;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Exception.RegistrationException;
import Reika.DragonAPI.Instantiable.Data.Maps.BlockMap;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.ModRegistry.PowerTypes;
import Reika.ElectriCraft.ElectriCraft;
import Reika.ElectriCraft.Base.TileEntityWireComponent;
import Reika.ElectriCraft.Base.WiringTile;
import Reika.ElectriCraft.TileEntities.TileEntityBattery;
import Reika.ElectriCraft.TileEntities.TileEntityEUSplitter;
import Reika.ElectriCraft.TileEntities.TileEntityGenerator;
import Reika.ElectriCraft.TileEntities.TileEntityMeter;
import Reika.ElectriCraft.TileEntities.TileEntityMotor;
import Reika.ElectriCraft.TileEntities.TileEntityRFBattery;
import Reika.ElectriCraft.TileEntities.TileEntityRFCable;
import Reika.ElectriCraft.TileEntities.TileEntityRelay;
import Reika.ElectriCraft.TileEntities.TileEntityResistor;
import Reika.ElectriCraft.TileEntities.TileEntityTransformer;
import Reika.ElectriCraft.TileEntities.TileEntityWire;
import Reika.RotaryCraft.Auxiliary.Interfaces.NBTMachine;
import Reika.RotaryCraft.Auxiliary.RecipeManagers.WorktableRecipes;
import Reika.RotaryCraft.Registry.ConfigRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

public enum ElectriTiles {

	WIRE("electri.wire", 					ElectriBlocks.WIRE,			TileEntityWire.class, 			0, "RenderWire"),
	GENERATOR("machine.electrigenerator", 	ElectriBlocks.MACHINE, 		TileEntityGenerator.class, 		0, "RenderGenerator"),
	MOTOR("machine.electrimotor", 			ElectriBlocks.MACHINE, 		TileEntityMotor.class, 			1, "RenderMotor"),
	RESISTOR("machine.electriresistor", 	ElectriBlocks.MACHINE, 		TileEntityResistor.class, 		2, "RenderResistor"),
	RELAY("machine.electrirelay", 			ElectriBlocks.MACHINE, 		TileEntityRelay.class, 			3, "RenderRelay"),
	BATTERY("machine.electribattery", 		ElectriBlocks.BATTERY, 		TileEntityBattery.class, 		4),
	CABLE("machine.rfcable", 				ElectriBlocks.CABLE, 		TileEntityRFCable.class, 		0, "RenderCable"),
	METER("machine.wiremeter", 				ElectriBlocks.MACHINE, 		TileEntityMeter.class, 			4, "RenderElectricMeter"),
	RFBATTERY("machine.rfbattery", 			ElectriBlocks.RFBATTERY, 	TileEntityRFBattery.class, 		0, "RenderRFBattery"),
	TRANSFORMER("machine.transformer", 		ElectriBlocks.MACHINE, 		TileEntityTransformer.class, 	5, "RenderTransformer"),
	EUSPLIT("machine.eusplit", 				ElectriBlocks.EUSPLIT, 		TileEntityEUSplitter.class, 	0);

	private String name;
	private final Class teClass;
	private int meta;
	private String render;
	private final ElectriBlocks blockInstance;
	private TileEntity renderInstance;

	private static final BlockMap<ElectriTiles> machineMappings = new BlockMap();

	public static final ElectriTiles[] TEList = values();

	private ElectriTiles(String n, ElectriBlocks block, Class<? extends TileEntity> tile, int m) {
		this(n, block, tile, m, null);
	}

	private ElectriTiles(String n, ElectriBlocks block, Class<? extends TileEntity> tile, int m, String r) {
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

	public static ArrayList<ElectriTiles> getTilesOfBlock(ElectriBlocks b) {
		ArrayList li = new ArrayList();
		for (int i = 0; i < TEList.length; i++) {
			if (TEList[i].blockInstance == b)
				li.add(TEList[i]);
		}
		return li;
	}

	public static TileEntity createTEFromIDAndMetadata(Block id, int meta) {
		if (id == ElectriBlocks.WIRE.getBlockInstance())
			return new TileEntityWire();
		ElectriTiles index = getMachineFromIDandMetadata(id, meta);
		if (index == null) {
			ElectriCraft.logger.logError("ID "+id+" and metadata "+meta+" are not a valid machine identification pair!");
			return null;
		}
		Class TEClass = index.teClass;
		try {
			return (TileEntity)TEClass.newInstance();
		}
		catch (InstantiationException e) {
			e.printStackTrace();
			throw new RegistrationException(ElectriCraft.instance, "ID "+id+" and Metadata "+meta+" failed to instantiate its TileEntity of "+TEClass);
		}
		catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new RegistrationException(ElectriCraft.instance, "ID "+id+" and Metadata "+meta+" failed illegally accessed its TileEntity of "+TEClass);
		}
	}

	public static ElectriTiles getMachineFromIDandMetadata(Block id, int meta) {
		return machineMappings.get(id, meta);
	}

	public boolean isAvailableInCreativeInventory() {
		if (this == CABLE || this == RFBATTERY)
			return PowerTypes.RF.exists();
		return true;
	}

	public boolean hasCustomItem() {
		return this == WIRE || this == BATTERY || this == RFBATTERY;
	}

	public boolean isWiring() {
		return this == WIRE || this == CABLE;
	}

	public static ElectriTiles getTE(IBlockAccess iba, int x, int y, int z) {
		Block id = iba.getBlock(x, y, z);
		if (id == ElectriBlocks.WIRE.getBlockInstance())
			return WIRE;
		int meta = iba.getBlockMetadata(x, y, z);
		return getMachineFromIDandMetadata(id, meta);
	}

	public ItemStack getCraftedProduct() {
		if (this == WIRE) {
			return new ItemStack(ElectriItems.WIRE.getItemInstance());
		}
		else if (this == BATTERY) {
			return new ItemStack(ElectriItems.BATTERY.getItemInstance());
		}
		else if (this == RFBATTERY) {
			return new ItemStack(ElectriItems.RFBATTERY.getItemInstance());
		}
		else
			return new ItemStack(ElectriItems.PLACER.getItemInstance(), 1, this.ordinal());
	}

	public TileEntity createTEInstanceForRender() {
		if (renderInstance == null) {
			try {
				renderInstance = (TileEntity)teClass.newInstance();
			}
			catch (InstantiationException e) {
				e.printStackTrace();
				throw new RegistrationException(ElectriCraft.instance, "Could not create TE instance to render "+this);
			}
			catch (IllegalAccessException e) {
				e.printStackTrace();
				throw new RegistrationException(ElectriCraft.instance, "Could not create TE instance to render "+this);
			}
		}
		return renderInstance;
	}

	public boolean hasRender() {
		return render != null;
	}

	public String getRenderer() {
		if (!this.hasRender())
			throw new RuntimeException("Machine "+name+" has no render to call!");
		return "Reika.ElectriCraft.Renders."+render;
	}

	public Block getBlock() {
		return this.getBlockInstance();
	}

	public Block getBlockInstance() {
		return blockInstance.getBlockInstance();
	}

	public int getBlockMetadata() {
		return meta;
	}

	public boolean renderInPass1() {
		switch(this) {
		case TRANSFORMER:
			return true;
		default:
			return false;
		}
	}

	public boolean isDummiedOut() {
		if (DragonAPICore.isReikasComputer())
			return false;
		if (this == EUSPLIT && !PowerTypes.EU.exists())
			return true;
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

	public void addSizedOreCrafting(int size, Object... obj) {
		ItemStack is = this.getCraftedProduct();
		ShapedOreRecipe ir = new ShapedOreRecipe(ReikaItemHelper.getSizedItemStack(is, size), obj);
		if (!this.isDummiedOut()) {
			WorktableRecipes.getInstance().addRecipe(ir);
			if (ConfigRegistry.TABLEMACHINES.getState()) {
				GameRegistry.addRecipe(ir);
			}
		}
	}

	public void addOreCrafting(Object... obj) {
		ItemStack is = this.getCraftedProduct();
		ShapedOreRecipe ir = new ShapedOreRecipe(is, obj);
		if (!this.isDummiedOut()) {
			WorktableRecipes.getInstance().addRecipe(ir);
			if (ConfigRegistry.TABLEMACHINES.getState()) {
				GameRegistry.addRecipe(ir);
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
		for (int i = 0; i < ElectriTiles.TEList.length; i++) {
			ElectriTiles r = ElectriTiles.TEList[i];
			Block id = r.getBlock();
			int meta = r.getBlockMetadata();
			machineMappings.put(id, meta, r);
		}
	}

	public String getPlaceSound() {
		switch(this) {
		case WIRE:
		case CABLE:
			return "step.cloth";
		default:
			return "step.stone";
		}
	}

	public boolean isSpecialWiringPiece() {
		return TileEntityWireComponent.class.isAssignableFrom(teClass);
	}

	public boolean isWiringPiece() {
		return WiringTile.class.isAssignableFrom(teClass);
	}

	public static ElectriTiles getMachine(ItemStack item) {
		if (item.getItem() == ElectriItems.WIRE.getItemInstance())
			return WIRE;
		if (item.getItem() == ElectriItems.BATTERY.getItemInstance())
			return BATTERY;
		if (item.getItem() == ElectriItems.RFBATTERY.getItemInstance())
			return RFBATTERY;
		return TEList[item.getItemDamage()];
	}

	public boolean hasNBTVariants() {
		return NBTMachine.class.isAssignableFrom(teClass);
	}
}
