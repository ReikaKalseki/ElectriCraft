/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ElectriCraft.Base;

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import Reika.DragonAPI.Interfaces.IndexedItemSprites;
import Reika.ElectriCraft.ElectriCraft;
import Reika.ElectriCraft.Registry.ElectriItems;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ElectriItemBase extends Item implements IndexedItemSprites {

	private int index;

	public ElectriItemBase(int ID, int tex) {
		super(ID);
		index = tex;
		this.setCreativeTab(ElectriCraft.tabElectri);
		if (this.getDataValues() > 1) {
			hasSubtypes = true;
			this.setMaxDamage(0);
		}
	}

	@Override
	public int getItemSpriteIndex(ItemStack is) {
		return index+this.getTextureOffset(is);
	}

	public int getTextureOffset(ItemStack is) {
		return is.getItemDamage();
	}

	@Override
	public final void registerIcons(IconRegister ico) {}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(int ID, CreativeTabs cr, List li)
	{
		ElectriItems ri = ElectriItems.getEntryByID(ID);
		for (int i = 0; i < this.getDataValues(); i++) {
			ItemStack item = new ItemStack(ID, 1, i);
			if (ri.isAvailableInCreative(item))
				li.add(item);
		}
	}

	public final int getDataValues() {
		ElectriItems i = ElectriItems.getEntryByID(itemID);
		if (i == null)
			return 0;
		return i.getNumberMetadatas();
	}

	@Override
	public String getUnlocalizedName(ItemStack is) {
		if (this.getDataValues() <= 1)
			return super.getUnlocalizedName(is);
		int d = is.getItemDamage();
		return super.getUnlocalizedName() + "." + d;
	}

	public Class getTextureReferenceClass() {
		return ElectriCraft.class;
	}

	@Override
	public String getTexture(ItemStack is) {
		return "/Reika/ElectriCraft/Textures/Items/items1.png";
	}
}
