package com.labdogstudio.tutorial.tut6;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.DefaultTextureBinder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.labdogstudio.tutorial.BaseScreen;
import com.labdogstudio.tutorial.MenuScreen;

public class ShaderTest extends BaseScreen {

	public PerspectiveCamera cam;
	public CameraInputController camController;
	public Shader shader;
	public RenderContext renderContext;
	public Model model;
	public Renderable renderable;

	public ShaderTest(Game game) {
		super(game);
	}

	@Override
	public void show() {
		super.show();

		cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.position.set(2f, 2f, 2f);
		cam.lookAt(0, 0, 0);
		cam.near = 1f;
		cam.far = 300f;
		cam.update();

		ModelBuilder modelBuilder = new ModelBuilder();
		model = modelBuilder.createSphere(2f, 2f, 2f, 20, 20, new Material(),
				Usage.Position | Usage.Normal | Usage.TextureCoordinates);

		NodePart blockPart = model.nodes.get(0).parts.get(0);

		renderable = new Renderable();
		blockPart.setRenderable(renderable);
		renderable.environment = null;
		renderable.worldTransform.idt();
		//renderable.meshPart.primitiveType = GL20.GL_POINTS;

		renderContext = new RenderContext(new DefaultTextureBinder(DefaultTextureBinder.WEIGHTED, 1));

//		shader = new DefaultShader(renderable);
//		shader.init();
		
//		String vert = Gdx.files.internal("loadscene/data/test.vertex.glsl").readString();
//		String frag = Gdx.files.internal("loadscene/data/test.fragment.glsl").readString();
//		shader = new DefaultShader(renderable, new DefaultShader.Config(vert, frag));
//		shader.init();
		
		shader = new TestShader();
		shader.init();		

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

		renderContext.begin();
		shader.begin(cam, renderContext);
		shader.render(renderable);
		shader.end();
		renderContext.end();
	}

	@Override
	public void hide() {
		super.hide();

		shader.dispose();
		model.dispose();
	}

}
