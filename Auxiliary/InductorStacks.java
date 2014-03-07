/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.RotationalInduction.Auxiliary;

import net.minecraft.item.ItemStack;
import Reika.RotaryCraft.Auxiliary.ItemStacks;
import Reika.RotationalInduction.Registry.InductionOres;

public class InductorStacks {

	public static final ItemStack tinIngot = InductionOres.TIN.getProduct();
	public static final ItemStack silverIngot = InductionOres.SILVER.getProduct();
	public static final ItemStack nickelIngot = InductionOres.NICKEL.getProduct();
	public static final ItemStack copperIngot = InductionOres.COPPER.getProduct();
	public static final ItemStack platinumIngot = InductionOres.PLATINUM.getProduct();
	public static final ItemStack aluminumIngot = InductionOres.ALUMINUM.getProduct();
	public static final ItemStack superconductor = ItemStacks.pcb;
}
