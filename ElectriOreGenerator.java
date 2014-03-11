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

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;
import Reika.ElectroCraft.Registry.ElectroOres;
import cpw.mods.fml.common.IWorldGenerator;

public class ElectroOreGenerator implements IWorldGenerator {

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkgen, IChunkProvider provider) {
		for (int i = 0; i < ElectroOres.oreList.length; i++) {
			ElectroOres ore = ElectroOres.oreList[i];
			if (ore.canGenerateInChunk(world, chunkX, chunkZ)) {
				this.generate(ore, world, random, chunkX*16, chunkZ*16);
			}
		}
	}

	public static void generate(ElectroOres ore, World world, Random random, int chunkX, int chunkZ) {
		//ReikaJavaLibrary.pConsole("Generating "+ore);
		//ReikaJavaLibrary.pConsole(chunkX+", "+chunkZ);
		int id = ore.getBlockID();
		int meta = ore.getBlockMetadata();
		int passes = ore.perChunk;
		for (int i = 0; i < passes; i++) {
			int posX = chunkX + random.nextInt(16);
			int posZ = chunkZ + random.nextInt(16);
			int posY = ore.minY + random.nextInt(ore.maxY-ore.minY+1);


			if (ore.canGenAt(world, posX, posY, posZ)) {
				if ((new WorldGenMinable(id, meta, ore.veinSize, ore.getReplaceableBlock())).generate(world, random, posX, posY, posZ))
					;//ReikaJavaLibrary.pConsole(ore+" @ "+posX+", "+posY+", "+posZ, ore == ElectroOres.MAGNETITE);
			}
		}
	}

}
