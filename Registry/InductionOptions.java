/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.RotationalInduction.Registry;

import Reika.DragonAPI.Interfaces.ConfigList;
import Reika.RotationalInduction.Induction;

public enum InductionOptions implements ConfigList {

	EMPTY("EMPTY", true);

	private String label;
	private boolean defaultState;
	private int defaultValue;
	private float defaultFloat;
	private Class type;
	private boolean enforcing = false;

	public static final InductionOptions[] optionList = InductionOptions.values();

	private InductionOptions(String l, boolean d) {
		label = l;
		defaultState = d;
		type = boolean.class;
	}

	private InductionOptions(String l, boolean d, boolean tag) {
		label = l;
		defaultState = d;
		type = boolean.class;
		enforcing = true;
	}

	private InductionOptions(String l, int d) {
		label = l;
		defaultValue = d;
		type = int.class;
	}

	private InductionOptions(String l, float d) {
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
		return (Boolean)Induction.config.getControl(this.ordinal());
	}

	public int getValue() {
		return (Integer)Induction.config.getControl(this.ordinal());
	}

	public float getFloat() {
		return (Float)Induction.config.getControl(this.ordinal());
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
