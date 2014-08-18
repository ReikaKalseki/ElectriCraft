/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ElectriCraft;

import Reika.DragonAPI.DragonOptions;
import Reika.DragonAPI.Instantiable.Rendering.ItemSpriteSheetRenderer;
import Reika.ElectriCraft.Auxiliary.ElectriRenderList;
import Reika.ElectriCraft.Base.ElectriTERenderer;
import Reika.ElectriCraft.Registry.ElectriItems;
import Reika.ElectriCraft.Registry.ElectriTiles;

import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ElectriClient extends ElectriCommon
{
	private final ItemSpriteSheetRenderer items = new ItemSpriteSheetRenderer(ElectriCraft.instance, ElectriCraft.class, "Textures/Items/items.png");

	public static final ElectriItemRenderer machineItems = new ElectriItemRenderer();

	public static WireRenderer wire;
	public static CableRenderer cable;

	@Override
	public void registerSounds() {
		//MinecraftForge.EVENT_BUS.register(new SoundLoader(RotaryCraft.instance, SoundRegistry.soundList, SoundRegistry.SOUND_FOLDER));
	}

	@Override
	public void registerRenderers() {
		wireRender = RenderingRegistry.getNextAvailableRenderId();
		wire = new WireRenderer(wireRender);
		RenderingRegistry.registerBlockHandler(wireRender, wire);

		cableRender = RenderingRegistry.getNextAvailableRenderId();
		cable = new CableRenderer(cableRender);
		RenderingRegistry.registerBlockHandler(cableRender, cable);

		if (DragonOptions.NORENDERS.getState()) {
			ElectriCraft.logger.log("Disabling all machine renders for FPS and lag profiling.");
		}
		else {
			this.loadModels();
		}

		this.registerSpriteSheets();
	}

	@Override
	public void addArmorRenders() {

	}

	public void loadModels() {
		for (int i = 0; i < ElectriTiles.TEList.length; i++) {
			ElectriTiles m = ElectriTiles.TEList[i];
			if (m.hasRender()) {
				ElectriTERenderer render = ElectriRenderList.instantiateRenderer(m);
				ClientRegistry.bindTileEntitySpecialRenderer(m.getTEClass(), render);
			}
		}

		MinecraftForgeClient.registerItemRenderer(ElectriItems.PLACER.getItemInstance(), machineItems);
		MinecraftForgeClient.registerItemRenderer(ElectriItems.WIRE.getItemInstance(), machineItems);
		MinecraftForgeClient.registerItemRenderer(ElectriItems.BATTERY.getItemInstance(), machineItems);
	}

	private void registerSpriteSheets() {
		for (int i = 0; i < ElectriItems.itemList.length; i++) {
			ElectriItems ii = ElectriItems.itemList[i];
			if (!ii.isPlacerItem())
				MinecraftForgeClient.registerItemRenderer(ElectriItems.itemList[i].getItemInstance(), items);
		}
	}

	@Override
	public World getClientWorld()
	{
		return FMLClientHandler.instance().getClient().theWorld;
	}

}
