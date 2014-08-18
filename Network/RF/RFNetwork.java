/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ElectriCraft.Network.RF;

import Reika.ElectriCraft.ElectriCraft;
import Reika.ElectriCraft.NetworkObject;
import Reika.ElectriCraft.Auxiliary.ElectriNetworkTickEvent;
import Reika.ElectriCraft.TileEntities.TileEntityRFCable;

import java.util.ArrayList;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import cofh.api.energy.IEnergyHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class RFNetwork implements NetworkObject {

	private ArrayList<TileEntityRFCable> cables = new ArrayList();
	private ArrayList<EnergyInteraction> endpoints = new ArrayList();
	private int energy = 0;
	private int networkLimit;

	public void setIOLimit(int limit) {
		if (networkLimit != limit) {
			networkLimit = limit;
			for (int i = 0; i < cables.size(); i++) {
				TileEntityRFCable cable = cables.get(i);
				if (cable.getRFLimit() != networkLimit) {
					cable.setRFLimit(networkLimit);
				}
			}
		}
		//ReikaJavaLibrary.pConsole("L:"+limit);
		//Thread.dumpStack();
	}

	public int getIOLimit() {
		return networkLimit;
	}

	public RFNetwork() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void tick(ElectriNetworkTickEvent evt) {
		if (!cables.isEmpty()) {
			ArrayList<EnergyInteraction> collectibles = new ArrayList();
			ArrayList<EnergyInteraction> insertibles = new ArrayList();
			int maxCanPush = energy;
			//ReikaJavaLibrary.pConsole(this.getIOLimit(), Side.SERVER);
			for (int i = 0; i < endpoints.size(); i++) {
				EnergyInteraction ei = endpoints.get(i);
				maxCanPush += ei.getTotalInsertible();
				if (ei.isCollectible()) {
					collectibles.add(ei);
				}
				if (ei.isInsertible()) {
					insertibles.add(ei);
				}
			}

			//ReikaJavaLibrary.pConsole(this.getIOLimit(), Side.SERVER);
			maxCanPush = Math.min(this.getIOLimit(), maxCanPush);

			for (int i = 0; i < collectibles.size() && energy < maxCanPush; i++) {
				EnergyInteraction ei = collectibles.get(i);
				int space = maxCanPush-energy;
				energy += ei.collectEnergy(space);
			}

			for (int i = 0; i < insertibles.size() && energy > 0; i++) {
				EnergyInteraction ei = insertibles.get(i);
				int add = Math.min(energy, 1+energy/insertibles.size());
				energy -= ei.addEnergy(add);
			}
		}
	}

	public void addElement(TileEntityRFCable te) {
		if (!cables.contains(te)) {
			cables.add(te);
			if (te.getRFLimit() > 0 && te.getRFLimit() != networkLimit) {
				this.setIOLimit(Math.min(te.getRFLimit(), this.getIOLimit()));
			}
		}
	}

	public void removeElement(TileEntityRFCable te) {
		cables.remove(te);
		this.rebuild();
	}

	private void rebuild() {
		ElectriCraft.logger.debug("Remapping RF network "+this);
		for (int i = 0; i < cables.size(); i++) {
			TileEntityRFCable te = cables.get(i);
			te.findAndJoinNetwork(te.worldObj, te.xCoord, te.yCoord, te.zCoord);
		}
		this.clear(true);
	}

	public void addConnection(IEnergyHandler ih, ForgeDirection dir) {
		if (ih instanceof TileEntityRFCable)
			return;
		EnergyInteraction has = this.getInteractionFor(ih);
		if (has == null) {
			endpoints.add(new EnergyInteraction(ih, dir));
		}
		else {
			has.addSide(dir);
		}
	}

	public void merge(RFNetwork n) {
		if (n != this) {
			ArrayList<TileEntityRFCable> li = new ArrayList();
			for (int i = 0; i < n.cables.size(); i++) {
				TileEntityRFCable wire = n.cables.get(i);
				li.add(wire);
			}
			for (int i = 0; i < n.endpoints.size(); i++) {
				EnergyInteraction ei = n.endpoints.get(i);
				EnergyInteraction has = this.getInteractionFor(ei.tile);
				if (has == null) {
					endpoints.add(ei);
				}
				else {
					has.merge(ei);
				}
			}
			n.clear(false);
			for (int i = 0; i < li.size(); i++) {
				TileEntityRFCable wire = li.get(i);
				wire.setNetwork(this);
			}
			if (n.getIOLimit() != 0 && n.networkLimit != networkLimit)
				this.setIOLimit(Math.min(n.getIOLimit(), this.getIOLimit()));
		}
		this.updateWires();
	}

	private EnergyInteraction getInteractionFor(IEnergyHandler tile) {
		for (int i = 0; i < endpoints.size(); i++) {
			EnergyInteraction ei = endpoints.get(i);
			if (ei.contains(tile))
				return ei;
		}
		return null;
	}

	private void updateWires() {

	}

	private void clear(boolean clearTiles) {
		if (clearTiles) {
			for (int i = 0; i < cables.size(); i++) {
				cables.get(i).resetNetwork();
			}
		}

		cables.clear();
		endpoints.clear();
		energy = 0;

		try {
			MinecraftForge.EVENT_BUS.unregister(this);
		}
		catch (Exception e) { //randomly??
			e.printStackTrace();
		}
	}

	private static class EnergyInteraction {
		private final IEnergyHandler tile;
		private final ArrayList<ForgeDirection> sides = new ArrayList();

		private EnergyInteraction(IEnergyHandler ih, ForgeDirection... dirs) {
			tile = ih;
			for (int i = 0; i < dirs.length; i++) {
				this.addSide(dirs[i]);
			}
		}

		public boolean isInsertible() {
			return this.getTotalInsertible() > 0;
		}

		public boolean isCollectible() {
			return this.getTotalCollectible() > 0;
		}

		public boolean contains(IEnergyHandler tile) {
			return tile == this.tile;
		}

		public void addSide(ForgeDirection dir) {
			if (!sides.contains(dir))
				sides.add(dir);
		}

		public void merge(EnergyInteraction ei) {
			for (int i = 0; i < ei.sides.size(); i++) {
				this.addSide(ei.sides.get(i));
			}
		}

		public int collectEnergy(int max) {
			int total = 0;
			for (int i = 0; i < sides.size(); i++) {
				ForgeDirection dir = sides.get(i);
				if (tile.canInterface(dir)) {
					int collect = max-total;
					total += tile.extractEnergy(dir, collect, false);
				}
			}
			return total;
		}

		public int addEnergy(int max) {
			int total = 0;
			for (int i = 0; i < sides.size(); i++) {
				ForgeDirection dir = sides.get(i);
				if (tile.canInterface(dir)) {
					int add = max-total;
					total += tile.receiveEnergy(dir, add, false);
				}
			}
			return total;
		}

		public int getTotalCollectible() {
			int total = 0;
			for (int i = 0; i < sides.size(); i++) {
				ForgeDirection dir = sides.get(i);
				if (tile.canInterface(dir)) {
					total += tile.extractEnergy(dir, Integer.MAX_VALUE, true);
				}
			}
			return total;
		}

		public int getTotalInsertible() {
			int total = 0;
			for (int i = 0; i < sides.size(); i++) {
				ForgeDirection dir = sides.get(i);
				if (tile.canInterface(dir)) {
					total += tile.receiveEnergy(dir, Integer.MAX_VALUE, true);
				}
			}
			return total;
		}

		@Override
		public String toString() {
			return tile+" @ "+sides;
		}
	}

	@Override
	public String toString() {
		return cables.size()+": "+endpoints.toString();
	}

	public int drainEnergy(int maxReceive, boolean simulate) {
		maxReceive = Math.min(maxReceive, this.getIOLimit());
		int drain = Math.min(maxReceive, energy);
		if (!simulate)
			energy -= drain;
		return drain;
	}

	public int addEnergy(int maxAdd, boolean simulate) {
		//ReikaJavaLibrary.pConsole(this.getIOLimit()+"/"+energy, Side.SERVER);
		if (energy >= this.getIOLimit())
			return 0;
		maxAdd = Math.min(this.getIOLimit(), maxAdd);
		if (!simulate)
			energy += maxAdd;
		return maxAdd;
	}

}
