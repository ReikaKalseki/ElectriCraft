/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ElectriCraft.Registry;

import Reika.DragonAPI.Interfaces.ConfigList;
import Reika.ElectriCraft.ElectriCraft;

public enum ElectriOptions implements ConfigList {

	RETROGEN("Retrogenerate Ores", false),
	DISCRETE("Ore Discretization", 1);

	private String label;
	private boolean defaultState;
	private int defaultValue;
	private float defaultFloat;
	private Class type;
	private boolean enforcing = false;

	public static final ElectriOptions[] optionList = ElectriOptions.values();

	private ElectriOptions(String l, boolean d) {
		label = l;
		defaultState = d;
		type = boolean.class;
	}

	private ElectriOptions(String l, boolean d, boolean tag) {
		label = l;
		defaultState = d;
		type = boolean.class;
		enforcing = true;
	}

	private ElectriOptions(String l, int d) {
		label = l;
		defaultValue = d;
		type = int.class;
	}

	private ElectriOptions(String l, float d) {
		label = l;
		defaultFloat = d;
		type = float.class;
	}

	public boolean isBoolean() {
		return type == boolean.class;
	}

	public boolean isNumeric() {
		return type == int.class;
	}

	public boolean isDecimal() {
		return type == float.class;
	}

	public Class getPropertyType() {
		return type;
	}

	public String getLabel() {
		return label;
	}

	public boolean getState() {
		return (Boolean)ElectriCraft.config.getControl(this.ordinal());
	}

	public int getValue() {
		return (Integer)ElectriCraft.config.getControl(this.ordinal());
	}

	public float getFloat() {
		return (Float)ElectriCraft.config.getControl(this.ordinal());
	}

	public boolean isDummiedOut() {
		return type == null;
	}

	@Override
	public boolean getDefaultState() {
		return defaultState;
	}

	@Override
	public int getDefaultValue() {
		return defaultValue;
	}

	@Override
	public float getDefaultFloat() {
		return defaultFloat;
	}

	@Override
	public boolean isEnforcingDefaults() {
		return enforcing;
	}

	@Override
	public boolean shouldLoad() {
		return true;
	}

}
