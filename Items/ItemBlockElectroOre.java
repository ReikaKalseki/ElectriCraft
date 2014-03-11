/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ElectroCraft.Items;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import Reika.ElectroCraft.Registry.ElectroOres;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBlockElectroOre extends ItemBlock {

	public ItemBlockElectroOre(int ID) {
		super(ID);
		hasSubtypes = true;
		this.setMaxDamage(0);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(int ID, CreativeTabs cr, List li)
	{
		for (int i = 0; i < this.getDataValues(); i++) {
			ItemStack item = new ItemStack(ID, 1, i);
			li.add(item);
		}
	}

	private int getDataValues() {
		return ElectroOres.oreList.length;
	}

	@Override
	public String getItemDisplayName(ItemStack is) {
		ElectroOres ore = ElectroOres.oreList[is.getItemDamage()];
		return ore.oreName;
	}

	@Override
	public String getUnlocalizedName(ItemStack is) {
		int d = is.getItemDamage();
		return super.getUnlocalizedName() + "." + d;
	}

	@Override
	public int getMetadata(int meta)
	{
		return meta;
	}
}
