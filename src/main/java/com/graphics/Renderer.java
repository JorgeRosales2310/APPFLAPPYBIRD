package com.graphics;

import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class Renderer {
    // Variables de OpenGL 
    private int vao, vbo, programa; // IDs para el cuadrado base (Quad)
    private int vaoTri, vboTri; // IDs para el triángulo base
    // Ubicaciones de las variables dentro del Shader (Uniforms)
    private int uPosBase, uLocalOffset, uScale, uRotation, uColor;

    public Renderer() {
        crearShaders(); // Compila el mini-programa que pinta los píxeles
        crearQuadBase(); // Carga la forma de un cuadrado en la tarjeta gráfica
        crearTrianguloBase(); // Carga la forma de un triángulo en la tarjeta gráfica
    }

    private void crearShaders() {
        // Shader de Vértices: Calcula en qué lugar de la pantalla va cada punto
        String vSrc = "#version 330 core\n" +
                "layout (location=0) in vec3 aPos;\n" +
                "uniform vec2 uPosBase;\n" +
                "uniform vec2 uLocalOffset;\n" +
                "uniform vec2 uScale;\n" +
                "uniform float uRotation;\n" +
                "void main() {\n" +
                "    float c = cos(uRotation);\n" +
                "    float s = sin(uRotation);\n" +
                "    vec2 pos = vec2(aPos.x * c - aPos.y * s, aPos.x * s + aPos.y * c);\n" +
                "    pos = pos * uScale + uLocalOffset + uPosBase;\n" +
                "    gl_Position = vec4(pos, 0.0, 1.0);\n" +
                "}";

        // Shader de Fragmentos: Le da el color (R, G, B) a la figura
        String fSrc = "#version 330 core\n" +
                "uniform vec3 uColor;\n" +
                "out vec4 FragColor;\n" +
                "void main() {\n" +
                "    FragColor = vec4(uColor, 1.0);\n" +
                "}";

        // Compila ambos shaders y los une en un "programa" ejecutable por OpenGL
        int vShader = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
        GL20.glShaderSource(vShader, vSrc);
        GL20.glCompileShader(vShader);

        int fShader = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
        GL20.glShaderSource(fShader, fSrc);
        GL20.glCompileShader(fShader);

        programa = GL20.glCreateProgram();
        GL20.glAttachShader(programa, vShader);
        GL20.glAttachShader(programa, fShader);
        GL20.glLinkProgram(programa);

        // Obtiene las direcciones de las variables para luego mandarles datos desde Java
        uPosBase = GL20.glGetUniformLocation(programa, "uPosBase");
        uLocalOffset = GL20.glGetUniformLocation(programa, "uLocalOffset");
        uScale = GL20.glGetUniformLocation(programa, "uScale");
        uRotation = GL20.glGetUniformLocation(programa, "uRotation");
        uColor = GL20.glGetUniformLocation(programa, "uColor");
    }

    private void crearQuadBase() {
        // Los 6 vértices que forman un cuadrado usando 2 triángulos juntos
        float[] vertices = {
                -0.5f, -0.5f, 0,
                0.5f, -0.5f, 0,
                0.5f, 0.5f, 0,
                -0.5f, -0.5f, 0,
                0.5f, 0.5f, 0,
                -0.5f, 0.5f, 0
        };
        // Guarda esta figura en la memoria de video (VRAM)
        vao = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vao);
        vbo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        FloatBuffer buf = BufferUtils.createFloatBuffer(vertices.length);
        buf.put(vertices).flip();
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buf, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);
        GL20.glEnableVertexAttribArray(0);
    }

    private void crearTrianguloBase() {
        // Los 3 vértices que forman un triángulo con base plana
        float[] vertices = {
                0.0f, 0.5f, 0, // Arriba al centro
                -0.5f, -0.5f, 0, // Abajo a la izquierda
                0.5f, -0.5f, 0 // Abajo a la derecha
        };
        // Guarda esta figura (triángulo) en otra zona de memoria
        vaoTri = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vaoTri);
        vboTri = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboTri);
        FloatBuffer buf = BufferUtils.createFloatBuffer(vertices.length);
        buf.put(vertices).flip();
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buf, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);
        GL20.glEnableVertexAttribArray(0);
    }

    // Le avisa a OpenGL que vamos a empezar a dibujar usando el shader principal
    public void iniciarFrame() {
        GL20.glUseProgram(programa);
        GL30.glBindVertexArray(vao); // Por defecto se usará el cuadrado
    }

    // Método general para dibujar un cuadrado/rectángulo
    public void dibujar(float baseX, float baseY, float locX, float locY, float scaleX, float scaleY, float rot,
            float r, float g, float b) {
        // Envia los datos al shader
        GL20.glUniform2f(uPosBase, baseX, baseY);
        GL20.glUniform2f(uLocalOffset, locX, locY);
        GL20.glUniform2f(uScale, scaleX, scaleY);
        GL20.glUniform1f(uRotation, rot);
        GL20.glUniform3f(uColor, r, g, b);
        // Dibuja los 6 vértices del Quad (cuadrado)
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 6);
    }

    // Método general para dibujar un triángulo (Ideal para montañas)
    public void dibujarTriangulo(float baseX, float baseY, float locX, float locY, float scaleX, float scaleY,
            float rot,
            float r, float g, float b) {
        GL30.glBindVertexArray(vaoTri); // Cambiamos al molde del triángulo
        GL20.glUniform2f(uPosBase, baseX, baseY);
        GL20.glUniform2f(uLocalOffset, locX, locY);
        GL20.glUniform2f(uScale, scaleX, scaleY);
        GL20.glUniform1f(uRotation, rot);
        GL20.glUniform3f(uColor, r, g, b);
        // Dibuja los 3 vértices
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 3);
        GL30.glBindVertexArray(vao); // Vuelve al molde de cuadrado por defecto
    }

    public void dibujarPajaro(Pajaro p) {
        if (!p.vivo && p.y < -0.85f)
            return; // Si está muerto y bajo tierra, ya no lo dibuja

        // Si está vivo rota levemente al subir; si muere cae en picada (-1.5f rad)
        float rot = p.vivo ? (p.velY * 0.4f) : -1.5f;
        // Simula el aleteo usando la función Seno con el tiempo real del juego
        float aleteo = p.vivo ? (float) Math.sin(GLFW.glfwGetTime() * 20f) * 0.03f : 0f;

        // Calculamos el seno y coseno para rotar los offsets locales en Java
        float c = (float) Math.cos(rot);
        float s = (float) Math.sin(rot);

        // Cola (Triángulo en la parte trasera apuntando a la izquierda)
        float tx = -0.06f * c - (-0.01f) * s;
        float ty = -0.06f * s + (-0.01f) * c;
        dibujarTriangulo(p.x, p.y, tx, ty, 0.05f, 0.05f, rot + 1.5708f, p.r * 0.8f, p.g * 0.8f, p.b * 0.8f);

        // Cuerpo (Borde negro y relleno de color)
        dibujar(p.x, p.y, 0, 0, 0.11f, 0.09f, rot, 0.1f, 0.1f, 0.1f);
        dibujar(p.x, p.y, 0, 0, 0.10f, 0.08f, rot, p.r, p.g, p.b);

        // Ala (Blanca, se mueve arriba y abajo con la variable 'aleteo')
        float ax = -0.02f * c - aleteo * s;
        float ay = -0.02f * s + aleteo * c;
        dibujar(p.x, p.y, ax, ay, 0.05f, 0.04f, rot, 1.0f, 1.0f, 1.0f);

        // Ojo y pupila negra
        float ox = 0.03f * c - 0.02f * s;
        float oy = 0.03f * s + 0.02f * c;
        dibujar(p.x, p.y, ox, oy, 0.035f, 0.035f, rot, 1.0f, 1.0f, 1.0f);
        float px_ = 0.04f * c - 0.02f * s;
        float py_ = 0.04f * s + 0.02f * c;
        dibujar(p.x, p.y, px_, py_, 0.015f, 0.015f, rot, 0.0f, 0.0f, 0.0f);

        // Pico naranja asomando (Triángulo apuntando a la derecha)
        float bx = 0.06f * c - (-0.01f) * s;
        float by = 0.06f * s + (-0.01f) * c;
        dibujarTriangulo(p.x, p.y, bx, by, 0.04f, 0.04f, rot - 1.5708f, 1.0f, 0.5f, 0.0f);
    }

    public void dibujarNube(float x, float y) {
        // Nube construida pegando varios cuadrados blancos descentrados
        dibujar(x, y, 0, 0, 0.2f, 0.1f, 0, 1.0f, 1.0f, 1.0f); // Centro
        dibujar(x - 0.08f, y - 0.02f, 0, 0, 0.15f, 0.08f, 0, 1.0f, 1.0f, 1.0f); // Izquierda 
        dibujar(x + 0.08f, y - 0.02f, 0, 0, 0.15f, 0.08f, 0, 1.0f, 1.0f, 1.0f); // Derecha
        dibujar(x - 0.04f, y + 0.04f, 0, 0, 0.12f, 0.08f, 0, 1.0f, 1.0f, 1.0f); // Arriba Izquierda
        dibujar(x + 0.05f, y + 0.03f, 0, 0, 0.1f, 0.08f, 0, 1.0f, 1.0f, 1.0f); // Arriba derecha
    }

    public void dibujarSol(float x, float y) {
        // Sol elaborado con rayos que giran
        float rayR = 1.0f, rayG = 0.8f, rayB = 0.0f;
        float coreR = 1.0f, coreG = 0.95f, coreB = 0.2f;

        float time = (float) GLFW.glfwGetTime();
        float rot = time * 0.5f; // Velocidad de rotación constante

        // Rayos (4 líneas que cruzan el sol rotando)
        dibujar(x, y, 0, 0, 0.35f, 0.30f, rot, rayR, rayG, rayB);
        dibujar(x, y, 0, 0, 0.35f, 0.30f, rot + 1.5708f, rayR, rayG, rayB);
        dibujar(x, y, 0, 0, 0.35f, 0.30f, rot + 0.7854f, rayR, rayG, rayB);
        dibujar(x, y, 0, 0, 0.35f, 0.30f, rot - 0.7854f, rayR, rayG, rayB);

        // Núcleo central (creado superponiendo dos cuadrados para formar un octágono)
        dibujar(x, y, 0, 0, 0.18f, 0.18f, 0, coreR, coreG, coreB);
        dibujar(x, y, 0, 0, 0.18f, 0.18f, 0.7854f, coreR, coreG, coreB);
    }

    public void dibujarMontana(float x, float y, float tamano, float r, float g, float b) {
        float width = tamano * 1.5f;
        float height = tamano;

        // Borde negro: un triángulo más grande centrado para que sobresalga en todas las puntas (incluyendo la cima)
        float borde = 0.04f;
        dibujarTriangulo(x, y - borde/4, 0, 0, width + borde*2.0f, height + borde*1.5f, 0, 0.0f, 0.0f, 0.0f);
        
        dibujarTriangulo(x, y, 0, 0, width, height, 0, r, g, b);
        
        // Pico nevado: un triángulo blanco en la parte superior
        float heightNieve = height * 0.35f;
        float widthNieve = width * 0.35f;
        float offsetY = (height - heightNieve) / 2.0f; // Sube la nieve a la cima
        dibujarTriangulo(x, y + offsetY, 0, 0, widthNieve, heightNieve, 0, 0.98f, 0.98f, 1.0f);

        float dripW = widthNieve * 0.33f;
        float dripH = heightNieve * 0.5f;
        float dripY = y + offsetY - heightNieve / 2.0f - dripH / 2.0f + 0.005f; 
        
        dibujarTriangulo(x - dripW, dripY, 0, 0, dripW, dripH, 3.14159f, 0.98f, 0.98f, 1.0f); // Izquierda
        dibujarTriangulo(x, dripY, 0, 0, dripW, dripH, 3.14159f, 0.98f, 0.98f, 1.0f); // Centro
        dibujarTriangulo(x + dripW, dripY, 0, 0, dripW, dripH, 3.14159f, 0.98f, 0.98f, 1.0f); // Derecha
    }
        

    // Libera los recursos de OpenGL al salir
    public void limpiar() {
        GL30.glDeleteVertexArrays(vao);

        GL20.glDeleteProgram(programa);
        
    }
}
