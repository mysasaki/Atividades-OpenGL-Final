package br.pucpr.cg;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.glfw.GLFW.*;

import br.pucpr.mage.*;
import br.pucpr.mage.phong.DirectionalLight;
import br.pucpr.mage.phong.Material;
import org.joml.Vector3f;
import org.joml.Matrix4f;

import java.io.File;
import java.io.IOException;


/**
 * ATIVIDADE 6: CRIE UMA TERRAINLOADER QUE CARREGUE UMA DAS IMAGENS DA PASTA "img/terrain" NA FORMA DE UM TERRENO 3D.
 *
 * A ATIVIDAE 7 TAMBÉM SE ENCONTRA NESTE MESMO PROJETO.
 */

public class TerrainLoader implements Scene{
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
            512.0f  //specular power
    );

    private float angleX = 0.0f;
    private float angleY = 0.5f;

    @Override
    public void init() {
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glPolygonMode(GL_FRONT_FACE, GL_LINE);
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        try {
            mesh = MeshFactory.loadTerrain(new File("C:/Users/Mylla/Documents/_PUCPR/3_SEMESTRE/PROG_3D/OpenGL/opengl/heights/volcano.png"), 0.5f);
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
         * ATIVIDADE 7: ALTERE O PROGRAMA PARA QUE PERMIRIR QUE O USUARIO SE DESLOQUE PELA CENA COM AS SETAS DIRECIONAIS.
         * USE AS TECLAS Z E C PARA STRAFE.
         * COMO NÃO FOI INFORMADO, TOMEI A LIBERDADE DE ATRIBUIR AS TECLAS W e S A MOVIMENTAÇÃO PARA FRENTE E PARA TRÁS.
         *
         */
        /*if (keys.isPressed(GLFW_KEY_H)) {

        }*/

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

        if (keys.isDown(GLFW_KEY_LEFT)) {
            camera.rotate((float) Math.toRadians(rotateSpeed) * secs);
        }

        if (keys.isDown(GLFW_KEY_RIGHT)) {
            camera.rotate(-(float) Math.toRadians(rotateSpeed) * secs);
        }

        if (keys.isDown(GLFW_KEY_UP)) {
            camera.rotateX((float) Math.toRadians(rotateSpeed) * secs);//moveUp(speed * secs);
        }

        if (keys.isDown(GLFW_KEY_DOWN)) {
            camera.rotateX(-(float) Math.toRadians(rotateSpeed) * secs);
        }

    }

    @Override
    public void draw() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        Shader shader = mesh.getShader();
        shader.bind()
                .setUniform("uProjection", camera.getProjectionMatrix())
                .setUniform("uView", camera.getViewMatrix())
                .setUniform("uCameraPosition", camera.getPosition());

        light.apply(shader);
        material.apply(shader);
        shader.unbind();

        mesh.setUniform("uWorld", new Matrix4f().rotateX(angleX).rotateY(angleY));
        mesh.draw();
    }

    public void deinit() {}

    public static void main(String[] args) {
        new Window(new TerrainLoader(), "Terrain", 1024,768).show();
    }

}
