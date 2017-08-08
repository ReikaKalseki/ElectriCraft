/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ElectriCraft.World;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import Reika.DragonAPI.Interfaces.OreGenerator;
import Reika.DragonAPI.Interfaces.RetroactiveGenerator;
import Reika.ElectriCraft.Registry.ElectriOres;

public class ElectriOreGenerator implements RetroactiveGenerator {

	public static final ElectriOreGenerator instance = new ElectriOreGenerator();

	public final ArrayList<OreGenerator> generators = new ArrayList();

	private ElectriOreGenerator() {
		generators.add(new BasicElectriOreGenerator());
	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkgen, IChunkProvider provider) {
		for (int i = 0; i < ElectriOres.oreList.length; i++) {
			ElectriOres ore = ElectriOres.oreList[i];
			if (ore.canGenerateInChunk(world, chunkX, chunkZ)) {
				for (OreGenerator gen : generators) {
					gen.generateOre(ore, random, world, chunkX, chunkZ);
				}
			}
		}
	}

	@Override
	public boolean canGenerateAt(World world, int chunkX, int chunkZ) {
		return true;
	}

	@Override
	public String getIDString() {
		return "ElectriCraft Ores";
	}

}
