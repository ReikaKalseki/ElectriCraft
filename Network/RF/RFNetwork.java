/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ElectriCraft.Network.RF;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.ElectriCraft.ElectriCraft;
import Reika.ElectriCraft.ElectriNetworkManager;
import Reika.ElectriCraft.NetworkObject;
import Reika.ElectriCraft.Auxiliary.ElectriNetworkEvent.ElectriNetworkRepathEvent;
import Reika.ElectriCraft.Auxiliary.ElectriNetworkEvent.ElectriNetworkTickEvent;
import Reika.ElectriCraft.TileEntities.ModInterface.TileEntityRFCable;
import cofh.api.energy.IEnergyConnection;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;

public class RFNetwork implements NetworkObject {

	private final Collection<TileEntityRFCable> cables = new ArrayList();
	private final HashMap<WorldLocation, EnergyInteraction> endpoints = new HashMap();
	private int energy = 0;
	private int networkLimit;
	private boolean disabled;
	private int tick;

	public void setIOLimit(int limit) {
		if (networkLimit != limit) {
			networkLimit = limit;
			for (TileEntityRFCable cable : cables) {
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
		ElectriNetworkManager.instance.addNetwork(this);
	}

	public void tick(ElectriNetworkTickEvent evt) {
		tick++;
		if (tick%1000 == 0)
			this.checkValidity();

		if (!disabled && !cables.isEmpty() && !endpoints.isEmpty()) {
			ArrayList<EnergyInteraction> collectibles = new ArrayList();
			ArrayList<EnergyInteraction> insertibles = new ArrayList();
			int maxCanPush = energy;
			//ReikaJavaLibrary.pConsole(this.getIOLimit(), Side.SERVER);
			Iterator<Entry<WorldLocation, EnergyInteraction>> it = endpoints.entrySet().iterator();
			while (it.hasNext()) {
				Entry<WorldLocation, EnergyInteraction> e = it.next();
				EnergyInteraction ei = e.getValue();
				if (ei.valid()) {
					maxCanPush += ei.getTotalInsertible();
					if (ei.isCollectible()) {
						collectibles.add(ei);
					}
					if (ei.isInsertible()) {
						insertibles.add(ei);
					}
				}
				else {
					it.remove();
				}
			}

			//ReikaJavaLibrary.pConsole(endpoints);

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

	public void checkValidity() {
		Iterator<TileEntityRFCable> it = cables.iterator();
		while (it.hasNext()) {
			TileEntityRFCable te = it.next();
			if (te.isInvalid())
				it.remove();
		}
		/*
		Iterator<Entry<WorldLocation, EnergyInteraction>> it2 = endpoints.entrySet().iterator();
		while (it2.hasNext()) {
			Entry<WorldLocation, EnergyInteraction> e = it2.next();
			EnergyInteraction ei = e.getValue();
			if (!ei.valid())
				it2.remove();
		}*/
		if (cables.isEmpty()/* || endpoints.isEmpty()*/)
			this.clear(false);
	}

	@Override
	public void repath(ElectriNetworkRepathEvent evt) {

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
		for (TileEntityRFCable te : cables) {
			te.findAndJoinNetwork(te.worldObj, te.xCoord, te.yCoord, te.zCoord);
		}
		this.clear(true);
	}

	public void addConnection(IEnergyConnection ih, ForgeDirection dir) {
		if (ih instanceof TileEntityRFCable)
			return;
		EnergyInteraction has = this.getInteractionFor(ih);
		if (has == null) {
			endpoints.put(new WorldLocation((TileEntity)ih), new EnergyInteraction(ih, dir));
		}
		else {
			has.addSide(dir);
		}
	}

	public void merge(RFNetwork n) {
		if (n != this) {
			ArrayList<TileEntityRFCable> li = new ArrayList();
			for (TileEntityRFCable wire : n.cables) {
				li.add(wire);
			}
			for (EnergyInteraction ei : n.endpoints.values()) {
				EnergyInteraction has = this.getInteractionFor(ei.getTile());
				if (has == null) {
					endpoints.put(ei.location, ei);
				}
				else {
					has.merge(ei);
				}
			}
			n.clear(false);
			for (TileEntityRFCable wire : li) {
				wire.setNetwork(this);
			}
			if (n.getIOLimit() != 0 && n.networkLimit != networkLimit)
				this.setIOLimit(Math.min(n.getIOLimit(), this.getIOLimit()));
		}
		this.updateWires();
	}

	private EnergyInteraction getInteractionFor(IEnergyConnection tile) {
		return endpoints.get(new WorldLocation((TileEntity)tile));
	}

	private void updateWires() {

	}

	private void clear(boolean clearTiles) {
		if (clearTiles) {
			for (TileEntityRFCable cable : cables) {
				cable.resetNetwork();
			}
		}

		cables.clear();
		endpoints.clear();
		energy = 0;
		disabled = true;

		ElectriNetworkManager.instance.scheduleNetworkDiscard(this);
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

	private static class EnergyInteraction {

		private final WorldLocation location;
		private final ArrayList<ForgeDirection> sides = new ArrayList();
		private final boolean canExtract;
		private final boolean canReceive;

		private EnergyInteraction(IEnergyConnection ih, ForgeDirection... dirs) {
			location = new WorldLocation((TileEntity)ih);
			for (int i = 0; i < dirs.length; i++) {
				this.addSide(dirs[i]);
			}
			canExtract = ih instanceof IEnergyProvider;
			canReceive = ih instanceof IEnergyReceiver;
		}

		public boolean isInsertible() {
			return this.getTotalInsertible() > 0;
		}

		public boolean isCollectible() {
			return this.getTotalCollectible() > 0;
		}

		public boolean contains(IEnergyConnection tile) {
			return tile == this.getTile();
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
			if (!canExtract)
				return 0;
			int total = 0;
			IEnergyConnection tile = this.getTile();
			for (int i = 0; i < sides.size(); i++) {
				ForgeDirection dir = sides.get(i);
				if (tile.canConnectEnergy(dir)) {
					int collect = max-total;
					total += ((IEnergyProvider)tile).extractEnergy(dir, collect, false);
				}
			}
			return total;
		}

		public int addEnergy(int max) {
			if (!canReceive)
				return 0;
			int total = 0;
			IEnergyConnection tile = this.getTile();
			for (int i = 0; i < sides.size(); i++) {
				ForgeDirection dir = sides.get(i);
				if (tile.canConnectEnergy(dir)) {
					int add = max-total;
					total += ((IEnergyReceiver)tile).receiveEnergy(dir, add, false);
				}
			}
			return total;
		}

		public int getTotalCollectible() {
			if (!canExtract)
				return 0;
			int total = 0;
			IEnergyConnection tile = this.getTile();
			for (int i = 0; i < sides.size(); i++) {
				ForgeDirection dir = sides.get(i);
				if (tile.canConnectEnergy(dir)) {
					total += ((IEnergyProvider)tile).extractEnergy(dir, Integer.MAX_VALUE, true);
				}
			}
			return total;
		}

		public int getTotalInsertible() {
			if (!canReceive)
				return 0;
			int total = 0;
			IEnergyConnection tile = this.getTile();
			for (int i = 0; i < sides.size(); i++) {
				ForgeDirection dir = sides.get(i);
				if (tile.canConnectEnergy(dir)) {
					total += ((IEnergyReceiver)tile).receiveEnergy(dir, Integer.MAX_VALUE, true);
				}
			}
			return total;
		}

		@Override
		public String toString() {
			return this.getTile()+" @ "+sides;
		}

		public boolean valid() {
			return this.getTile() != null;
		}

		private IEnergyConnection getTile() {
			TileEntity te = location.getTileEntity();
			return te instanceof IEnergyConnection ? (IEnergyConnection)te : null;
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof EnergyInteraction) {
				EnergyInteraction ei = (EnergyInteraction)o;
				return ei.location.equals(location) && ei.sides.equals(sides);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return location.hashCode()^sides.hashCode();
		}
	}

}
