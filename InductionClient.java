/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.RotationalInduction;

import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import Reika.DragonAPI.Instantiable.Rendering.ItemSpriteSheetRenderer;
import Reika.RotationalInduction.Auxiliary.InductionRenderList;
import Reika.RotationalInduction.Base.InductionTERenderer;
import Reika.RotationalInduction.Registry.InductionItems;
import Reika.RotationalInduction.Registry.InductionTiles;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class InductionClient extends InductionCommon
{
	public static final ItemSpriteSheetRenderer items = new ItemSpriteSheetRenderer(Induction.instance, Induction.class, "Textures/Items/items.png");

	public static final InductionItemRenderer machineItems = new InductionItemRenderer();

	public static WireRenderer wire;

	@Override
	public void registerSounds() {
		//MinecraftForge.EVENT_BUS.register(new SoundLoader(RotaryCraft.instance, SoundRegistry.soundList, SoundRegistry.SOUND_FOLDER));
	}

	@Override
	public void registerRenderers() {
		wireRender = RenderingRegistry.getNextAvailableRenderId();
		wire = new WireRenderer(wireRender);
		RenderingRegistry.registerBlockHandler(wireRender, wire);

		this.loadModels();

		this.registerSpriteSheets();
	}

	@Override
	public void addArmorRenders() {

	}

	public void loadModels() {
		for (int i = 0; i < InductionTiles.TEList.length; i++) {
			InductionTiles m = InductionTiles.TEList[i];
			if (m.hasRender()) {
				InductionTERenderer render = InductionRenderList.instantiateRenderer(m);
				ClientRegistry.bindTileEntitySpecialRenderer(m.getTEClass(), render);
			}
		}

		MinecraftForgeClient.registerItemRenderer(InductionItems.PLACER.getShiftedID(), machineItems);
		MinecraftForgeClient.registerItemRenderer(InductionItems.WIRE.getShiftedID(), machineItems);
	}

	private void registerSpriteSheets() {
		for (int i = 0; i < InductionItems.itemList.length; i++) {
			InductionItems ii = InductionItems.itemList[i];
			if (!ii.isPlacerItem())
				MinecraftForgeClient.registerItemRenderer(InductionItems.itemList[i].getShiftedID(), items);
		}
	}

	@Override
	public World getClientWorld()
	{
		return FMLClientHandler.instance().getClient().theWorld;
	}

}
