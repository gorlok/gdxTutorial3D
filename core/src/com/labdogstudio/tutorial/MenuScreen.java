package com.labdogstudio.tutorial;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.labdogstudio.tutorial.tut1.Basic3DTest;
import com.labdogstudio.tutorial.tut1.Basic3DTest2;
import com.labdogstudio.tutorial.tut10.ShapeTest;
import com.labdogstudio.tutorial.tut11.BulletTest;
import com.labdogstudio.tutorial.tut11.BulletTest2;
import com.labdogstudio.tutorial.tut11.BulletTest3;
import com.labdogstudio.tutorial.tut12.BulletTest4;
import com.labdogstudio.tutorial.tut12.BulletTest5;
import com.labdogstudio.tutorial.tut12.BulletTest6;
import com.labdogstudio.tutorial.tut2.LoadModelsTest;
import com.labdogstudio.tutorial.tut3.LoadSceneTest1;
import com.labdogstudio.tutorial.tut3.LoadSceneTest2;
import com.labdogstudio.tutorial.tut3.LoadSceneTest3;
import com.labdogstudio.tutorial.tut3.LoadSceneTest4;
import com.labdogstudio.tutorial.tut4.BehindTheScenesTest;
import com.labdogstudio.tutorial.tut5.BehindTheScenesTest2;
import com.labdogstudio.tutorial.tut6.ShaderTest;
import com.labdogstudio.tutorial.tut7.ShaderTest2;
import com.labdogstudio.tutorial.tut7.ShaderTest3;
import com.labdogstudio.tutorial.tut8.FrustumCullingTest;
import com.labdogstudio.tutorial.tut8.FrustumCullingTest2;
import com.labdogstudio.tutorial.tut8.Test;
import com.labdogstudio.tutorial.tut9.RayPickingTest;

public class MenuScreen extends BaseScreen {
	
	private Stage stage;
	private TextureAtlas atlas;
	private Skin skin;
	private Table table;
	
	@SuppressWarnings("rawtypes")
	static Class[] testClazz = {Basic3DTest.class, Basic3DTest2.class, 
			LoadModelsTest.class, LoadSceneTest1.class, LoadSceneTest2.class, LoadSceneTest3.class, LoadSceneTest4.class, 
			BehindTheScenesTest.class, BehindTheScenesTest2.class, ShaderTest.class, ShaderTest2.class, ShaderTest3.class, 
			FrustumCullingTest.class, FrustumCullingTest2.class, Test.class, RayPickingTest.class, ShapeTest.class,
			BulletTest.class, BulletTest2.class, BulletTest3.class, BulletTest4.class, BulletTest5.class, BulletTest6.class};
	static String[] testNames = {"Basic 3D Test", "Basic 3D Test 2", 
			"Load Models Test", "Load Scene Test 1", "Load Scene Test 2", "Load Scene Test 3", "Load Scene Test 4",
			"Behind The Scenes Test", "Behind The Scenes Test 2", "Shader Test", "Shader Test 2", "Shader Test 3", 
			"Frustum Culling Test",	"Frustum Culling Test 2", "Test", "Ray Picking Test", "Shape Test",
			"Bullet Test", "Bullet Test 2", "Bullet Test 3", "Bullet Test 4", "Bullet Test 5", "Bullet Test 6"};
	
	public MenuScreen(Game game) {
		super(game);
	}
	
	@Override
	public void render(float delta) {
		super.render(delta);
		
		stage.act(delta);
		stage.draw();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void show() {
		super.show();
		
		stage = new Stage();
		
		Gdx.input.setInputProcessor(new InputMultiplexer(stage, new InputAdapter() {
			@Override
			public boolean keyUp(int keycode) {
				if (keycode == Keys.BACK || keycode == Keys.ESCAPE) {
					Gdx.app.exit();
				}
				return super.keyUp(keycode);
			}
		}));
		
		atlas = new TextureAtlas("skin/uiskin.atlas");
		skin = new Skin(Gdx.files.internal("skin/uiskin.json"), atlas);

		table = new Table(skin);
		table.setFillParent(true);
		
		Label heading = new Label("TUTORIALS", skin);
		table.add(heading).spaceBottom(20).row();

		TextButton exitButton = new TextButton("EXIT", skin);
		exitButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.app.exit();
			}
		});

		Table scrollTable = new Table(skin);
		for (int i = 0; i < testClazz.length; i++) {
			scrollTable.add(createTestButton(testNames[i], testClazz[i])).spaceBottom(2).row();
		}
		scrollTable.add(exitButton).spaceBottom(2).row();
		
		ScrollPane scroller = new ScrollPane(scrollTable);
		table.add(scroller).padBottom(20).fill().expand();
		
		stage.addActor(table);
	}

	private TextButton createTestButton(final String title, final Class<? extends BaseScreen> clazz) {
		TextButton test1Button = new TextButton(title, skin);
		test1Button.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				BaseScreen screen;
				try {
					screen = clazz.getDeclaredConstructor(Game.class).newInstance(game);
					game.setScreen(screen);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		test1Button.pad(5);
		return test1Button;
	}
	
	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		
		stage.getViewport().update(width, height, false);
		table.invalidateHierarchy();
	}

	@Override
	public void dispose() {
		super.dispose();
		
		atlas.dispose();
		skin.dispose();
		stage.dispose();
	}
	

}
