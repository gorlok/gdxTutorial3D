package com.labdogstudio.tutorial.tut1;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.utils.Array;
import com.labdogstudio.tutorial.BaseScreen;
import com.labdogstudio.tutorial.MenuScreen;

public class Basic3DTest2 extends BaseScreen {

	Environment environment;
	PerspectiveCamera cam;
	ModelBatch modelBatch;
	Model outerModel, innerModel;
	Array<ModelInstance> instances = new Array<ModelInstance>();
	CameraInputController camController;

	public Basic3DTest2(Game game) {
		super(game);
	}

	@Override
	public void show() {
		super.show();

		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

		modelBatch = new ModelBatch();
		cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.position.set(10f, 10f, 10f);
		cam.lookAt(0, 0, 0);
		cam.near = 1f;
		cam.far = 300f;
		cam.update();

		ModelBuilder modelBuilder = new ModelBuilder();

		innerModel = modelBuilder.createBox(3f, 3f, 3f, new Material(ColorAttribute.createDiffuse(Color.MAGENTA)),
				Usage.Position | Usage.Normal);
		instances.add(new ModelInstance(innerModel));
		
		outerModel = modelBuilder.createBox(5f, 5f, 5f, new Material(ColorAttribute.createDiffuse(1f, 1f, 1f, .2f)),
				Usage.Position | Usage.Normal);
		outerModel.materials.get(0).set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA));
		instances.add(new ModelInstance(outerModel));

		camController = new CameraInputController(cam);
		Gdx.input.setInputProcessor(new InputMultiplexer(camController, new InputAdapter() {
			@Override
			public boolean keyUp(int keycode) {
				if (keycode == Keys.BACK || keycode == Keys.ESCAPE) {
					game.setScreen(new MenuScreen(game));
				}
				return super.keyUp(keycode);
			}
		}));
	}

	@Override
	public void render(float delta) {
		super.render(delta);

		camController.update();

		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		modelBatch.begin(cam);
		modelBatch.render(instances, environment);
		modelBatch.end();
	}

	@Override
	public void hide() {
		super.hide();

		modelBatch.dispose();
		outerModel.dispose();
	}

}
