package br.pucpr.cg;

import br.pucpr.mage.*;
import br.pucpr.mage.phong.DirectionalLight;
import br.pucpr.mage.phong.Material;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.io.File;
import java.io.IOException;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;
import static org.lwjgl.opengl.GL11.*;

/**
 * Created by Mylla on 22/06/2017.
 */
public class TerrainPerlinNoise implements Scene{
    private Keyboard keys = Keyboard.getInstance();

    //Dados da cena
    private Camera camera = new Camera();
    private DirectionalLight light = new DirectionalLight(
            new Vector3f(1.0f, -3.0f, -1.0f), //direction
            new Vector3f(0.02f, 0.02f, 0.02f),  //ambiente
            new Vector3f(1.0f, 1.0f, 1.0f), //diffuse
            new Vector3f(1.0f, 1.0f, 1.0f)  //specular
    );

    //Dados da malha
    private Mesh mesh;
    private Material material = new Material(
            new Vector3f(1.0f, 1.0f, 1.0f), //ambient
            new Vector3f(0.7f, 0.7f, 0.7f), //diffuse
            new Vector3f(1.0f, 1.0f, 1.0f), //specular
            1000.0f  //specular power
    );

    private float angleX = 0.0f;
    private float angleY = 0.5f;

    private float terrainValue;

    private Noise noise;
    private int width;
    private int height;

    private boolean wireFrame;  //true =  wirefram, false =  fill

    @Override
    public void init() {
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        wireFrame = false;
        width = 500;
        height = 500;
        noise = new Noise(width, height, 0);
        terrainValue = 1.0f;

        try {
            mesh = MeshFactory.loadTerrain(noise.GetNoise(), 100.0f);
            System.out.println("ok!");
        } catch (IOException e){
            e.printStackTrace();
            System.exit(1);
        }

        camera.getPosition().y = 200.0f;
        camera.getPosition().z = 200.0f;
    }

    @Override
    public void update(float secs) {
        float rotateSpeed = 75.0f;
        float speed = 100.0f;

        if (keys.isPressed(GLFW_KEY_ESCAPE)) {
            glfwSetWindowShouldClose(glfwGetCurrentContext(), GLFW_TRUE);
            return;
        }

        /**
         * TDE: PERMITE O USUARIO SE MOVER NA CENA
         *
         */
        if (keys.isDown(GLFW_KEY_W)) {
            camera.moveFront(speed * secs);
        }

        if (keys.isDown(GLFW_KEY_S)) {
            camera.moveFront(-speed * secs);
        }

        if (keys.isDown(GLFW_KEY_C)) {
            camera.strafeRight(speed * secs);
        }

        if (keys.isDown(GLFW_KEY_Z)) {
            camera.strafeLeft(speed * secs);
        }

        /**
         * ROTAÇAO DA CAMERA
         */
        if (keys.isDown(GLFW_KEY_LEFT)) {
            camera.rotate((float) Math.toRadians(rotateSpeed) * secs);
        }

        if (keys.isDown(GLFW_KEY_RIGHT)) {
            camera.rotate(-(float) Math.toRadians(rotateSpeed) * secs);
        }

        if (keys.isDown(GLFW_KEY_UP)) {
            camera.rotateX((float) Math.toRadians(rotateSpeed) * secs, 0);//moveUp(speed * secs);
        }

        if (keys.isDown(GLFW_KEY_DOWN)) {
            camera.rotateX(-(float) Math.toRadians(rotateSpeed) * secs, 1);
        }

        /**
         * TECLAS PARA AUMENTAR E DIMINUIR O TERRENO
         */
        if (keys.isPressed(GLFW_KEY_L)) {
            terrainValue += 0.1;
        }

        if (keys.isPressed(GLFW_KEY_K)) {
            if (terrainValue - 0.1f >= 0)
                terrainValue -= 0.1f;
        }

        /**
         * ATIVA E DESATIVA O WIFEFRAME
         */
        if (keys.isPressed(GLFW_KEY_P)) {
            wireFrame = !wireFrame;
        }
    }

    @Override
    public void draw() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);


        /**
         * DESENHA O TERRENO COM OU SEM WIREFRAMA
         */
        if (wireFrame)
            glPolygonMode( GL_FRONT_AND_BACK, GL_LINE );
        else
            glPolygonMode( GL_FRONT_AND_BACK, GL_FILL );


        Shader shader = mesh.getShader();
        shader.bind()
                .setUniform("uProjection", camera.getProjectionMatrix())
                .setUniform("uView", camera.getViewMatrix())
                .setUniform("uCameraPosition", camera.getPosition())
                .setUniform("aValue", terrainValue);

        light.apply(shader);
        material.apply(shader);
        shader.unbind();

        mesh.setUniform("uWorld", new Matrix4f().rotateX(angleX).rotateY(angleY));
        mesh.draw();
    }

    public void deinit() {}

    public static void main(String[] args) {
        new Window(new TerrainPerlinNoise(), "Terrain", 1024,768).show();
    }

}
