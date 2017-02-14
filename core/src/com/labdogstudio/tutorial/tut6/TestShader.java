package com.labdogstudio.tutorial.tut6;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class TestShader implements Shader {
	ShaderProgram program;
    Camera camera;
    RenderContext context;
    int u_projViewTrans;
    int u_worldTrans;
    
	@Override
	public void init() {
        String vert = Gdx.files.internal("tut6/data/test.vertex.glsl").readString();
        String frag = Gdx.files.internal("tut6/data/test.fragment.glsl").readString();
        program = new ShaderProgram(vert, frag);
        if (!program.isCompiled())
            throw new GdxRuntimeException(program.getLog());
        u_projViewTrans = program.getUniformLocation("u_projViewTrans");
        u_worldTrans = program.getUniformLocation("u_worldTrans");        
	}
	
	@Override
	public void dispose() {
		program.dispose();
	}

	@Override
	public int compareTo(Shader other) {
		return 0;
	}

	@Override
	public boolean canRender(Renderable instance) {
		return true;
	}

	@Override
	public void begin(Camera camera, RenderContext context) {
        this.camera = camera;
        this.context = context;
        program.begin();
        program.setUniformMatrix(u_projViewTrans, camera.combined);
        //context.setDepthTest(true, GL20.GL_LEQUAL);
        context.setDepthTest(GL20.GL_LEQUAL);
        context.setCullFace(GL20.GL_BACK);        
	}

	@Override
	public void render(Renderable renderable) {
		program.setUniformMatrix(u_worldTrans, renderable.worldTransform);
		renderable.meshPart.render(program);
	}

	@Override
	public void end() {
		program.end();
	}

}
