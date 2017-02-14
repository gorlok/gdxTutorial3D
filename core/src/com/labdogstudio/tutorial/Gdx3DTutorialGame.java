package com.labdogstudio.tutorial;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class Gdx3DTutorialGame extends Game {

	final static int V_WIDTH = 800;
	final static int V_HEIGHT = 480;
	final static boolean landscape = V_WIDTH / V_HEIGHT > 1;

	OrthographicCamera camera;

	@Override
	public void create() {
		camera = new OrthographicCamera();
		camera.setToOrtho(false, V_WIDTH, V_HEIGHT);
		
		Gdx.input.setCatchBackKey(true);
		setScreen(new MenuScreen(this));
	}

}
