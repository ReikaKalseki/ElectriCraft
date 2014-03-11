/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ElectroCraft;

import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import Reika.DragonAPI.Instantiable.Rendering.ItemSpriteSheetRenderer;
import Reika.ElectroCraft.Auxiliary.ElectroRenderList;
import Reika.ElectroCraft.Base.ElectroTERenderer;
import Reika.ElectroCraft.Registry.ElectroItems;
import Reika.ElectroCraft.Registry.ElectroTiles;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ElectroClient extends ElectroCommon
{
	private final ItemSpriteSheetRenderer items = new ItemSpriteSheetRenderer(ElectroCraft.instance, ElectroCraft.class, "Textures/Items/items.png");

	public static final ElectroItemRenderer machineItems = new ElectroItemRenderer();

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
		for (int i = 0; i < ElectroTiles.TEList.length; i++) {
			ElectroTiles m = ElectroTiles.TEList[i];
			if (m.hasRender()) {
				ElectroTERenderer render = ElectroRenderList.instantiateRenderer(m);
				ClientRegistry.bindTileEntitySpecialRenderer(m.getTEClass(), render);
			}
		}

		MinecraftForgeClient.registerItemRenderer(ElectroItems.PLACER.getShiftedID(), machineItems);
		MinecraftForgeClient.registerItemRenderer(ElectroItems.WIRE.getShiftedID(), machineItems);
	}

	private void registerSpriteSheets() {
		for (int i = 0; i < ElectroItems.itemList.length; i++) {
			ElectroItems ii = ElectroItems.itemList[i];
			if (!ii.isPlacerItem())
				MinecraftForgeClient.registerItemRenderer(ElectroItems.itemList[i].getShiftedID(), items);
		}
	}

	@Override
	public World getClientWorld()
	{
		return FMLClientHandler.instance().getClient().theWorld;
	}

}
