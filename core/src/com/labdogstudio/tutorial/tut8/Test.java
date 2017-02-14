package com.labdogstudio.tutorial.tut8;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.labdogstudio.tutorial.BaseScreen;

public class Test extends BaseScreen {
	
		static Mesh genCube () {
			Mesh mesh = new Mesh(true, 24, 36, new VertexAttribute(Usage.Position, 3, "a_position"), new VertexAttribute(Usage.Normal,
				3, "a_normal"), new VertexAttribute(Usage.TextureCoordinates, 2, "a_texcoords"));

			float[] cubeVerts = {-0.5f, -0.5f, -0.5f, -0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 0.5f, 0.5f, -0.5f, -0.5f, -0.5f, 0.5f, -0.5f,
				-0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, -0.5f, -0.5f, -0.5f, -0.5f, -0.5f, 0.5f, -0.5f, 0.5f, 0.5f, -0.5f,
				0.5f, -0.5f, -0.5f, -0.5f, -0.5f, 0.5f, -0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, -0.5f, 0.5f, -0.5f, -0.5f, -0.5f,
				-0.5f, -0.5f, 0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 0.5f, -0.5f, 0.5f, -0.5f, -0.5f, 0.5f, -0.5f, 0.5f, 0.5f, 0.5f, 0.5f,
				0.5f, 0.5f, -0.5f,};

			float[] cubeNormals = {0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,
				1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f,
				-1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f,
				-1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,};

			float[] cubeTex = {0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f,
				0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f,
				1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f,};

			float[] vertices = new float[24 * 8];
			int pIdx = 0;
			int nIdx = 0;
			int tIdx = 0;
			for (int i = 0; i < vertices.length;) {
				vertices[i++] = cubeVerts[pIdx++];
				vertices[i++] = cubeVerts[pIdx++];
				vertices[i++] = cubeVerts[pIdx++];
				vertices[i++] = cubeNormals[nIdx++];
				vertices[i++] = cubeNormals[nIdx++];
				vertices[i++] = cubeNormals[nIdx++];
				vertices[i++] = cubeTex[tIdx++];
				vertices[i++] = cubeTex[tIdx++];
			}

			short[] indices = {0, 2, 1, 0, 3, 2, 4, 5, 6, 4, 6, 7, 8, 9, 10, 8, 10, 11, 12, 15, 14, 12, 14, 13, 16, 17, 18, 16, 18, 19,
				20, 23, 22, 20, 22, 21};

			mesh.setVertices(vertices);
			mesh.setIndices(indices);

			return mesh;
		}

	ShaderProgram shader;
	Mesh mesh;
	Matrix4 projection = new Matrix4();
	Matrix4 view = new Matrix4();
	Matrix4 model = new Matrix4();
	Matrix4 combined = new Matrix4();
	Vector3 axis = new Vector3(1, 0, 1).nor();
	float angle = 45;

	public Test(Game game) {
		super(game);
	}

	@Override
	public void show() {
		super.show();

		// @off
		String vertexShader = "uniform mat4 u_mvpMatrix;                   \n"
				+ "attribute vec4 a_position;                  \n" + "void main()                                 \n"
				+ "{                                           \n" + "   gl_Position = u_mvpMatrix * a_position;  \n"
				+ "}                            \n";
		String fragmentShader = "#ifdef GL_ES\n" + "precision mediump float;\n" + "#endif\n"
				+ "void main()                                  \n" + "{                                            \n"
				+ "  gl_FragColor = vec4 ( 1.0, 0.0, 0.0, 1.0 );\n" + "}";
		// @on

		shader = new ShaderProgram(vertexShader, fragmentShader);
		mesh = genCube();		
		mesh.getVertexAttribute(Usage.Position).alias = "a_position";
	}

	@Override
	public void render(float delta) {
		super.render(delta);

		angle += Gdx.graphics.getDeltaTime() * 40.0f;
		float aspect = Gdx.graphics.getWidth() / (float) Gdx.graphics.getHeight();
		projection.setToProjection(1.0f, 20.0f, 60.0f, aspect);
		view.idt().trn(0, 0, -2.0f);
		model.setToRotation(axis, angle);
		combined.set(projection).mul(view).mul(model);

		Gdx.gl20.glViewport(0, 0, Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight());
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
		shader.begin();
		Gdx.gl20.glBlendColor(1f, 0f, 0f, 0.5f);
		shader.setUniformMatrix("u_mvpMatrix", combined);
		mesh.render(shader, GL20.GL_TRIANGLES);
		shader.end();

		Gdx.app.log("angle", "" + angle);
	}

}
