package Reika.ElectriCraft.Items;

import net.minecraft.item.ItemStack;
import Reika.ElectriCraft.Base.ElectriItemBase;
import Reika.ElectriCraft.Registry.BatteryType;


public class ItemEnergyCrystal extends ElectriItemBase {

	public ItemEnergyCrystal(int tex) {
		super(tex);
	}

	@Override
	public boolean hasEffect(ItemStack is) {
		return is.getItemDamage() >= BatteryType.batteryList.length-1;
	}

}
