package com.labdogstudio.tutorial.tut7;

import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;

public class TestColorAttribute extends ColorAttribute {
	public final static String DiffuseUAlias = "diffuseUColor";
	public final static long DiffuseU = register(DiffuseUAlias);

	public final static String DiffuseVAlias = "diffuseVColor";
	public final static long DiffuseV = register(DiffuseVAlias);

	static {
		Mask = Mask | DiffuseU | DiffuseV;
	}

	public TestColorAttribute(long type, float r, float g, float b, float a) {
		super(type, r, g, b, a);
	}
}