package br.pucpr.cg;

import br.pucpr.mage.*;
import br.pucpr.mage.phong.DirectionalLight;
import br.pucpr.mage.phong.Material;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.io.*;
import java.util.Scanner;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;
import static org.lwjgl.opengl.GL11.*;

/**
 * Created by Mylla on 22/06/2017.
 * TDE E TRABALHO: CONSTRUA UM TERRENO DINAMICAMENTE.
 * UTILIZAMOS A CLASSE NOISE PARA CRIAR UM ARRAY GERADO COM PERLIN NOISE
 * O TERRENO TAMBEM CONSTA TEXTURA COMO EXIGIDO NA ATIVIDADE.
 */
public class TerrainPerlinNoise implements Scene{
    Scanner scanner = new Scanner(new File("DATA.txt"));

    private Keyboard keys = Keyboard.getInstance();
    private static final String PATH = "C:\\Users\\maiki\\Pictures\\img\\opengl\\textures\\";

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
    private Material material;

    private float angleX = 0.0f;
    private float angleY = 0.5f;

    private float terrainValue;

    private Noise noise;
    private int width;
    private int height;
    private int[] input = new int[3];

    private boolean wireFrame;  //true =  wirefram, false =  fill

    public TerrainPerlinNoise() throws FileNotFoundException {
    }

    @Override
    public void init() {
        //LE ARQUIVO
        int i = 0;
        while (scanner.hasNextInt())
        {
            input[i++] = scanner.nextInt();
        }

        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        wireFrame = false;

        width = input[0];
        height = input[1];
        noise = new Noise(width, height, input[2]);
        terrainValue = 1.0f;

        try {
            mesh = MeshFactory.loadTerrain(noise.GetNoise(), 100.0f);
            System.out.println("ok!");
        } catch (IOException e){
            e.printStackTrace();
            System.exit(1);
        }

        material = new Material(
                new Vector3f(1.0f, 1.0f, 1.0f), //ambient
                new Vector3f(0.7f, 0.7f, 0.7f), //diffuse
                new Vector3f(0.1f, 0.1f, 0.1f), //specular
                1000.0f  //specular power
        );
        material.setTexture("uTexture", new Texture(PATH + "grass.png"));
        camera.getPosition().y = 500.5f;
        camera.getPosition().z = 300.5f;
        camera.getPosition().x = 5.5f;
    }

    @Override
    public void update(float secs) {
        float rotateSpeed = 30.0f;
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

    public static void main(String[] args) throws FileNotFoundException {
        new Window(new TerrainPerlinNoise(), "Terrain", 1024,768).show();
    }

}
