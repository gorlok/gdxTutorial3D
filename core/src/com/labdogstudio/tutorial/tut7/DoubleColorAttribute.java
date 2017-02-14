package com.labdogstudio.tutorial.tut7;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;

public class DoubleColorAttribute extends ColorAttribute {
	public final static String DiffuseUVAlias = "diffuseUVColor";
	public final static long DiffuseUV = register(DiffuseUVAlias);
	
	static {
		Mask = Mask | DiffuseUV;
	}

	public final Color color1 = new Color();
	public final Color color2 = new Color();

	protected DoubleColorAttribute(long type, Color c1, Color c2) {
		super(type);
		color1.set(c1);
		color2.set(c2);
	}

	@Override
	public Attribute copy() {
		return new DoubleColorAttribute(type, color1, color2);
	}

	@Override
	protected boolean equals(Attribute other) {
		DoubleColorAttribute attr = (DoubleColorAttribute) other;
		return type == other.type && color1.equals(attr.color1) && color2.equals(attr.color2);
	}

	@Override
	public int compareTo(Attribute other) {
		if (type != other.type)
			return (int) (type - other.type);
		DoubleColorAttribute attr = (DoubleColorAttribute) other;
		return color1.equals(attr.color1) ? attr.color2.toIntBits() - color2.toIntBits()
				: attr.color1.toIntBits() - color1.toIntBits();
	}
}