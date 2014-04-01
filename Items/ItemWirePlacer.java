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

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.ElectriCraft.ElectriCraft;
import Reika.ElectriCraft.Registry.ElectriItems;
import Reika.ElectriCraft.Registry.ElectriTiles;
import Reika.ElectriCraft.Registry.WireType;
import Reika.ElectriCraft.TileEntities.TileEntityWire;
import Reika.RotaryCraft.API.Fillable;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemWirePlacer extends Item implements Fillable {

	public ItemWirePlacer(int par1, int tex) {
		super(par1);
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
		maxStackSize = 64;
		this.setCreativeTab(ElectriCraft.tabElectri);
	}

	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer ep, World world, int x, int y, int z, int side, float par8, float par9, float par10) {
		if (!ReikaWorldHelper.softBlocks(world, x, y, z) && world.getBlockMaterial(x, y, z) != Material.water && world.getBlockMaterial(x, y, z) != Material.lava) {
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
			if (!ReikaWorldHelper.softBlocks(world, x, y, z) && world.getBlockMaterial(x, y, z) != Material.water && world.getBlockMaterial(x, y, z) != Material.lava)
				return false;
		}
		AxisAlignedBB box = AxisAlignedBB.getBoundingBox(x, y, z, x+1, y+1, z+1);
		List inblock = world.getEntitiesWithinAABB(EntityLivingBase.class, box);
		if (inblock.size() > 0)
			return false;
		if (!ep.canPlayerEdit(x, y, z, 0, is))
			return false;
		if (!this.canBePlaced(is))
			return false;
		else
		{
			if (!ep.capabilities.isCreativeMode)
				--is.stackSize;
			int meta = is.getItemDamage()%WireType.INS_OFFSET;
			world.setBlock(x, y, z, ElectriTiles.WIRE.getBlockID(), meta, 3);
		}
		world.playSoundEffect(x+0.5, y+0.5, z+0.5, ElectriTiles.WIRE.getPlaceSound(), 1F, 1.5F);
		TileEntityWire te = (TileEntityWire)world.getBlockTileEntity(x, y, z);
		te.placer = ep.getEntityName();
		te.insulated = is.getItemDamage() >= WireType.INS_OFFSET;
		return true;
	}

	private boolean canBePlaced(ItemStack is) {
		if (is.getItemDamage()%WireType.INS_OFFSET == WireType.SUPERCONDUCTOR.ordinal()) {
			return is.stackTagCompound != null && is.stackTagCompound.getBoolean("fluid");
		}
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List) {
		for (int i = 0; i < 32; i++) {
			ItemStack item = new ItemStack(par1, 1, i);
			if (ElectriItems.WIRE.isAvailableInCreative(item)) {
				par3List.add(item);
				if (i%WireType.INS_OFFSET == WireType.SUPERCONDUCTOR.ordinal()) {
					ItemStack item2 = item.copy();
					item2.stackTagCompound = new NBTTagCompound();
					item2.stackTagCompound.setBoolean("fluid", true);
					item2.stackTagCompound.setInteger("lvl", this.getCapacity(item2));
					par3List.add(item2);
				}
			}
		}
	}

	public ItemStack getFilledSuperconductor(boolean insulated) {
		ItemStack item2 = insulated ? WireType.SUPERCONDUCTOR.getCraftedInsulatedProduct() : WireType.SUPERCONDUCTOR.getCraftedProduct();
		item2.stackTagCompound = new NBTTagCompound();
		item2.stackTagCompound.setBoolean("fluid", true);
		item2.stackTagCompound.setInteger("lvl", this.getCapacity(item2));
		return item2;
	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer ep, List li, boolean par4) {
		if (is.getItemDamage()%WireType.INS_OFFSET == WireType.SUPERCONDUCTOR.ordinal()) {
			if (is.stackTagCompound != null && is.stackTagCompound.getBoolean("fluid")) {
				li.add("Filled with Coolant");
			}
			else
				li.add("Cannot be placed until filled with coolant");
		}
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
	public final void registerIcons(IconRegister ico) {}

	@Override
	public boolean isValidFluid(Fluid f, ItemStack is) {
		return this.canFill(is) ? this.isColdFluid(f) : false;
	}

	private boolean isColdFluid(Fluid f) {
		if (f.equals(FluidRegistry.getFluid("liquid nitrogen")))
			return true;
		if (f.equals(FluidRegistry.getFluid("cryotheum")))
			return true;
		return false;
	}

	@Override
	public int getCapacity(ItemStack is) {
		return 25;
	}

	@Override
	public int getCurrentFillLevel(ItemStack is) {
		return is.stackTagCompound != null ? is.stackTagCompound.getInteger("lvl") : 0;
	}

	@Override
	public int addFluid(ItemStack is, Fluid f, int amt) {
		if (this.canFill(is)) {
			if (is.stackTagCompound == null)
				is.stackTagCompound = new NBTTagCompound();
			int liq = this.getCurrentFillLevel(is);
			int added = Math.min(amt, this.getCapacity(is)-liq);
			is.stackTagCompound.setInteger("lvl", added+liq);
			if (this.isFull(is))
				is.stackTagCompound.setBoolean("fluid", true);
			return added;
		}
		return 0;
	}

	@Override
	public boolean isFull(ItemStack is) {
		return is.stackTagCompound != null && this.getCurrentFillLevel(is) >= this.getCapacity(is);
	}

	private boolean canFill(ItemStack is) {
		if (is.getItemDamage()%WireType.INS_OFFSET == WireType.SUPERCONDUCTOR.ordinal()) {
			if (!this.isFull(is)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Fluid getCurrentFluid(ItemStack is) {
		return this.getCurrentFillLevel(is) > 0 ? FluidRegistry.getFluid("liquid nitrogen") : null;
	}

}
