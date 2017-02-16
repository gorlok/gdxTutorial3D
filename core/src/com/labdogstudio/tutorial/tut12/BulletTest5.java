package com.labdogstudio.tutorial.tut12;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
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
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.BoxShapeBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.CapsuleShapeBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.ConeShapeBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.SphereShapeBuilder;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.ContactListener;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseInterface;
import com.badlogic.gdx.physics.bullet.collision.btCapsuleShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btConeShape;
import com.badlogic.gdx.physics.bullet.collision.btCylinderShape;
import com.badlogic.gdx.physics.bullet.collision.btDbvtBroadphase;
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.badlogic.gdx.physics.bullet.dynamics.btConstraintSolver;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Disposable;
import com.labdogstudio.tutorial.BaseScreen;
import com.labdogstudio.tutorial.MenuScreen;

public class BulletTest5 extends BaseScreen {

    final static short GROUND_FLAG = 1<<8;
    final static short OBJECT_FLAG = 1<<9;
    final static short ALL_FLAG = -1;
    
    class MyContactListener extends ContactListener {
        @Override
        public boolean onContactAdded (int userValue0, int partId0, int index0, boolean match0, 
                        int userValue1, int partId1, int index1, boolean match1) {
            if (match0)
                ((ColorAttribute)instances.get(userValue0).materials.get(0).get(ColorAttribute.Diffuse)).color.set(Color.WHITE);
            if (match1)
                ((ColorAttribute)instances.get(userValue1).materials.get(0).get(ColorAttribute.Diffuse)).color.set(Color.WHITE);
            return true;
        }
    }
    
    static class MyMotionState extends btMotionState {
        Matrix4 transform;
        @Override
        public void getWorldTransform (Matrix4 worldTrans) {
            worldTrans.set(transform);
        }
        @Override
        public void setWorldTransform (Matrix4 worldTrans) {
            transform.set(worldTrans);
        }
    }    

	static class GameObject extends ModelInstance implements Disposable {
        public final btRigidBody body;
        public final MyMotionState motionState;
        
        public GameObject (Model model, String node, btRigidBody.btRigidBodyConstructionInfo constructionInfo) {
            super(model, node);
            motionState = new MyMotionState();
            motionState.transform = transform;
            body = new btRigidBody(constructionInfo);
            body.setMotionState(motionState);
        }

        @Override
        public void dispose () {
            body.dispose();
            motionState.dispose();
        }
        
        static class Constructor implements Disposable {
            public final Model model;
            public final String node;
            public final btCollisionShape shape;
            public final btRigidBody.btRigidBodyConstructionInfo constructionInfo;
            private static Vector3 localInertia = new Vector3();

            public Constructor (Model model, String node, btCollisionShape shape, float mass) {
                this.model = model;
                this.node = node;
                this.shape = shape;
                if (mass > 0f)
                    shape.calculateLocalInertia(mass, localInertia);
                else
                    localInertia.set(0, 0, 0);
                this.constructionInfo = new btRigidBody.btRigidBodyConstructionInfo(mass, null, shape, localInertia);
            }

            public GameObject construct () {
                return new GameObject(model, node, constructionInfo);
            }

            @Override
            public void dispose () {
                shape.dispose();
                constructionInfo.dispose();
            }
        }
	}

	PerspectiveCamera cam;
	CameraInputController camController;
	ModelBatch modelBatch;

	Model model;
	Array<GameObject> instances;
	ArrayMap<String, GameObject.Constructor> constructors;

	Environment environment;

	btCollisionConfiguration collisionConfig;
	btDispatcher dispatcher;

	MyContactListener contactListener;
	
    btBroadphaseInterface broadphase;
    btDynamicsWorld dynamicsWorld;
    btConstraintSolver constraintSolver;

	float spawnTimer;

	public BulletTest5(Game game) {
		super(game);
	}

	@Override
	public void show() {
		super.show();

		Bullet.init();

		modelBatch = new ModelBatch();

		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

		cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.position.set(3f, 7f, 10f);
		cam.lookAt(0, 4f, 0);
		cam.update();

		camController = new CameraInputController(cam);
		Gdx.input.setInputProcessor(new InputMultiplexer(this, camController));

		ModelBuilder mb = new ModelBuilder();
		mb.begin();
		mb.node().id = "ground";
		BoxShapeBuilder.build(mb.part("ground", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal,
				new Material(ColorAttribute.createDiffuse(Color.RED))), 5f, 1f, 5f);
		mb.node().id = "sphere";
		SphereShapeBuilder.build(mb.part("sphere", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal,
				new Material(ColorAttribute.createDiffuse(Color.GREEN))), 1f, 1f, 1f, 10, 10);
		mb.node().id = "box";
		BoxShapeBuilder.build(mb.part("box", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal,
				new Material(ColorAttribute.createDiffuse(Color.BLUE))), 1f, 1f, 1f);
		mb.node().id = "cone";
		ConeShapeBuilder.build(mb.part("cone", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal,
				new Material(ColorAttribute.createDiffuse(Color.YELLOW))), 1f, 2f, 1f, 10);
		mb.node().id = "capsule";
		CapsuleShapeBuilder.build(mb.part("capsule", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal,
				new Material(ColorAttribute.createDiffuse(Color.CYAN))), 0.5f, 2f, 10);
		mb.node().id = "cylinder";
		ConeShapeBuilder.build(mb.part("cylinder", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal,
				new Material(ColorAttribute.createDiffuse(Color.MAGENTA))), 1f, 2f, 1f, 10);
		model = mb.end();
		
        constructors = new ArrayMap<String, GameObject.Constructor>(String.class, GameObject.Constructor.class);
        constructors.put("ground", new GameObject.Constructor(model, "ground", new btBoxShape(new Vector3(2.5f, 0.5f, 2.5f)), 0f));
        constructors.put("sphere", new GameObject.Constructor(model, "sphere", new btSphereShape(0.5f), 1f));
        constructors.put("box", new GameObject.Constructor(model, "box", new btBoxShape(new Vector3(0.5f, 0.5f, 0.5f)), 1f));
        constructors.put("cone", new GameObject.Constructor(model, "cone", new btConeShape(0.5f, 2f), 1f));
        constructors.put("capsule", new GameObject.Constructor(model, "capsule", new btCapsuleShape(.5f, 1f), 1f));
        constructors.put("cylinder", new GameObject.Constructor(model, "cylinder", new btCylinderShape(new Vector3(.5f, 1f, .5f)), 1f));

        collisionConfig = new btDefaultCollisionConfiguration();
        dispatcher = new btCollisionDispatcher(collisionConfig);
        broadphase = new btDbvtBroadphase();
        constraintSolver = new btSequentialImpulseConstraintSolver();
        dynamicsWorld = new btDiscreteDynamicsWorld(dispatcher, broadphase, constraintSolver, collisionConfig);
        dynamicsWorld.setGravity(new Vector3(0, -10f, 0));
        contactListener = new MyContactListener();

        instances = new Array<GameObject>();
        GameObject object = constructors.get("ground").construct();
        instances.add(object);
        dynamicsWorld.addRigidBody(object.body);
        object.body.setContactCallbackFlag(GROUND_FLAG);
        object.body.setContactCallbackFilter(0);
	}

	@Override
	public void render(float xdelta) {
		super.render(xdelta);

		camController.update();

        final float delta = Math.min(1f / 30f, Gdx.graphics.getDeltaTime());

        dynamicsWorld.stepSimulation(delta, 5, 1f/60f);

        if ((spawnTimer -= delta) < 0) {
            spawn();
            spawnTimer = 1.5f;
        }

		Gdx.gl.glClearColor(0.3f, 0.3f, 0.3f, 1.f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		modelBatch.begin(cam);
		modelBatch.render(instances, environment);
		modelBatch.end();
	}

    public void spawn () {
        GameObject obj = constructors.values[1 + MathUtils.random(constructors.size - 2)].construct();
        obj.transform.setFromEulerAngles(MathUtils.random(360f), MathUtils.random(360f), MathUtils.random(360f));
        obj.transform.trn(MathUtils.random(-2.5f, 2.5f), 9f, MathUtils.random(-2.5f, 2.5f));
        obj.body.proceedToTransform(obj.transform);
        obj.body.setUserValue(instances.size);
        obj.body.setCollisionFlags(obj.body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
        instances.add(obj);
        dynamicsWorld.addRigidBody(obj.body);
        obj.body.setContactCallbackFlag(OBJECT_FLAG);
        obj.body.setContactCallbackFilter(GROUND_FLAG);        
    }
    
	@Override
	public boolean keyUp(int keycode) {
		if (keycode == Keys.BACK || keycode == Keys.ESCAPE) {
			game.setScreen(new MenuScreen(game));
			return true;
		}
		return false;
	}

	@Override
	public void hide() {
		super.hide();

        dynamicsWorld.dispose();
        constraintSolver.dispose();		
        broadphase.dispose();		
		
		for (GameObject obj : instances)
			obj.dispose();
		instances.clear();

		for (GameObject.Constructor ctor : constructors.values())
			ctor.dispose();
		constructors.clear();

		dispatcher.dispose();
		collisionConfig.dispose();
		
		modelBatch.dispose();
		model.dispose();

		contactListener.dispose();
	}

}