/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ElectriCraft.Items;

import Reika.DragonAPI.Libraries.MathSci.ReikaEngLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.ElectriCraft.ElectriCraft;
import Reika.ElectriCraft.Registry.BatteryType;
import Reika.ElectriCraft.Registry.ElectriBlocks;
import Reika.ElectriCraft.Registry.ElectriItems;
import Reika.ElectriCraft.Registry.ElectriTiles;
import Reika.ElectriCraft.TileEntities.TileEntityBattery;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBatteryPlacer extends Item {

	public ItemBatteryPlacer(int tex) {
		super();
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
		maxStackSize = 64;
		this.setCreativeTab(ElectriCraft.tabElectri);
	}

	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer ep, World world, int x, int y, int z, int side, float par8, float par9, float par10) {
		if (!ReikaWorldHelper.softBlocks(world, x, y, z) && ReikaWorldHelper.getMaterial(world, x, y, z) != Material.water && ReikaWorldHelper.getMaterial(world, x, y, z) != Material.lava) {
			if (side == 0)
				--y;
			if (side == 1)
				++y;
			if (side == 2)
				--z;
			if (side == 3)
				++z;
			if (side == 4)
				--x;
			if (side == 5)
				++x;
			if (!ReikaWorldHelper.softBlocks(world, x, y, z) && ReikaWorldHelper.getMaterial(world, x, y, z) != Material.water && ReikaWorldHelper.getMaterial(world, x, y, z) != Material.lava)
				return false;
		}
		if (!this.checkValidBounds(is, ep, world, x, y, z))
			return false;
		AxisAlignedBB box = AxisAlignedBB.getBoundingBox(x, y, z, x+1, y+1, z+1);
		List inblock = world.getEntitiesWithinAABB(EntityLivingBase.class, box);
		if (inblock.size() > 0)
			return false;
		if (!ep.canPlayerEdit(x, y, z, 0, is))
			return false;
		else
		{
			if (!ep.capabilities.isCreativeMode)
				--is.stackSize;
			world.setBlock(x, y, z, ElectriBlocks.BATTERY.getBlockInstance(), is.getItemDamage(), 3);
		}
		world.playSoundEffect(x+0.5, y+0.5, z+0.5, ElectriTiles.BATTERY.getPlaceSound(), 1F, 1.5F);
		TileEntityBattery te = (TileEntityBattery)world.getTileEntity(x, y, z);
		te.setPlacer(ep);
		//te.setBlockMetadata(RotaryAux.get4SidedMetadataFromPlayerLook(ep));
		te.setEnergyFromNBT(is);

		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List) {
		for (int i = 0; i < BatteryType.batteryList.length; i++) {
			ItemStack item = new ItemStack(par1, 1, i);
			par3List.add(item);
		}
		ItemStack item = new ItemStack(par1, 1, BatteryType.STAR.ordinal());
		item.stackTagCompound = new NBTTagCompound();
		item.stackTagCompound.setLong("nrg", BatteryType.STAR.maxCapacity);
		par3List.add(item);
	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer ep, List li, boolean vb) {
		long e = 0;
		if (is.stackTagCompound != null) {
			e = is.stackTagCompound.getLong("nrg");
		}
		BatteryType bat = BatteryType.batteryList[is.getItemDamage()];
		long max = bat.maxCapacity;
		String sg = ReikaEngLibrary.getSIPrefix(e);
		String sg2 = ReikaEngLibrary.getSIPrefix(max);
		double b = ReikaMathLibrary.getThousandBase(e);
		double b2 = ReikaMathLibrary.getThousandBase(max);
		li.add(String.format("Stored Energy: %.1f %sJ/%.1f %sJ", b, sg, b2, sg2));
		int a = bat.outputCurrent;
		int v = bat.outputVoltage;
		long power = (long)a*(long)v;

		String ps = ReikaEngLibrary.getSIPrefix(power);
		double p = ReikaMathLibrary.getThousandBase(power);
		li.add(String.format("Emits %dA at %dV (%.3f%sW)", a, v, p, ps));
	}

	protected boolean checkValidBounds(ItemStack is, EntityPlayer ep, World world, int x, int y, int z) {
		return true;
	}

	@Override
	public int getMetadata(int meta) {
		return meta;
	}

	@Override
	public final String getUnlocalizedName(ItemStack is)
	{
		int d = is.getItemDamage();
		return super.getUnlocalizedName() + "." + String.valueOf(d);
	}

	@Override
	public final void registerIcons(IIconRegister ico) {}

	@Override
	public String getItemStackDisplayName(ItemStack is) {
		ElectriItems ir = ElectriItems.getEntry(is);
		return ir.hasMultiValuedName() ? ir.getMultiValuedName(is.getItemDamage()) : ir.getBasicName();
	}


}