package com.graphics;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

public class AppFlappyBird {
    // Dimensiones de la ventana del juego
    public static final int ANCHO = 900;
    public static final int ALTO = 700;
    // Posición estática en el eje X donde se encuentran los pájaros 
    public static final float BIRD_X = -0.45f;
    // Fuerza de gravedad que empuja a los pájaros hacia abajo constantemente
    public static final float GRAVEDAD = -2.2f;
    // Fuerza vertical que se aplica cuando un pájaro salta
    public static final float IMPULSO_SALTO = 0.95f;
    // Distancia vertical entre la tubería superior y la inferior (el hueco para pasar)
    public static final float GAP_ALTO = 0.48f;
    // Grosor o ancho de las tuberías
    public static final float TUBERIA_ANCHO = 0.18f;

    private long window; // ID de la ventana de GLFW
    private Renderer renderer; // Clase encargada de dibujar en OpenGL
    private Interfas interfas; // Clase encargada de dibujar los menús y marcadores (HUD)
    private InputManager input; // Clase para manejar el teclado
    private Pajaro pajaro1, pajaro2, pajaro3; // Objetos que representan a los tres jugadores
    private List<Tuberia> tuberias; // Lista que guarda las tuberías activas en pantalla
    private Random random; // Generador de números aleatorios para las alturas de tuberías

    // Offsets para el efecto Parallax
    private float offsetNubes = 0;
    private float offsetMontanas = 0;

    // Variables de control del flujo del juego
    private float timerSpawn; // Temporizador para generar nuevas tuberías
    private boolean started, gameOver; // Estados del juego (iniciado / terminado)
    private float velocidadTuberiasActual, tiempoEntreTuberiasActual; // Dificultad dinámica

    public void run() {
        init(); // Inicializa GLFW, OpenGL y los objetos del juego
        resetGame(); // Configura los valores iniciales para empezar a jugar
        loop(); // Bucle principal del juego 
        cleanup(); // Libera la memoria y cierra la ventana al terminar
    }

    private void init() {
        if (!GLFW.glfwInit())
            throw new IllegalStateException("No se pudo iniciar GLFW");
        // Crea la ventana con el título especificado
        window = GLFW.glfwCreateWindow(ANCHO, ALTO, "Flappy Bird - 2 Jugadores", 0, 0);
        GLFW.glfwMakeContextCurrent(window);
        GLFW.glfwSwapInterval(1); // Activa el V-Sync
        GLFW.glfwShowWindow(window);
        GL.createCapabilities(); // Habilita las funciones de OpenGL

        renderer = new Renderer();
        interfas = new Interfas(renderer);
        input = new InputManager(window);
        tuberias = new ArrayList<>();
        random = new Random();
        SoundManager.init(); // Inicializar el sistema de audio de forma procedimental
    }

    private void resetGame() {
        // Inicializa el Pájaro 1 (Amarillo, se controla con ESPACIO)
        pajaro1 = new Pajaro(BIRD_X, 0.98f, 0.85f, 0.20f, GLFW.GLFW_KEY_SPACE);
        // Inicializa el Pájaro 2 (Azul, se controla con W)
        pajaro2 = new Pajaro(BIRD_X, 0.20f, 0.85f, 0.98f, GLFW.GLFW_KEY_W);
        pajaro3 = new Pajaro(BIRD_X, 0.85f, 0.20f, 0.98f, GLFW.GLFW_KEY_A);
        tuberias.clear(); // Borra cualquier tubería antigua
        timerSpawn = 0;
        started = false; // Esperando a que alguien salte para iniciar
        gameOver = false;
        velocidadTuberiasActual = 0.65f; // Velocidad inicial moderada
        tiempoEntreTuberiasActual = 1.5f; // Cada segundo y medio aparece un tubo
    }

    private void procesarLogica(float dt) {
        if (!started || gameOver)
            return; // Si no ha empezado o ya perdieron, no actualiza la física

        // Actualiza los offsets multiplicando por dt (delta time)
        offsetNubes -= 0.05f * dt;
        offsetMontanas -= 0.12f * dt; // Las montañas se mueven un poco más rápido que las nubes

        // Si el offset llega a -2.0, se reinicia a 0 para crear el bucle infinito
        if (offsetNubes < -2.0f)
            offsetNubes = 0;
        if (offsetMontanas < -2.0f)
            offsetMontanas = 0;

        // Actualiza la posición Y de ambos pájaros
        pajaro1.actualizar(dt);
        pajaro2.actualizar(dt);
        pajaro3.actualizar(dt);
        // Si los tres pájaros mueren, el juego termina
        if (!pajaro1.vivo && !pajaro2.vivo && !pajaro3.vivo) {
            gameOver = true;
            return;
        }

        // Dificultad incremental: La velocidad sube levemente según el puntaje más alto
        int maxP = Math.max(pajaro1.puntaje, Math.max(pajaro2.puntaje, pajaro3.puntaje));
        velocidadTuberiasActual = 0.65f + (Math.min(maxP, 20) * 0.02f);

        // Generador (spawner) de tuberías usando el timer
        timerSpawn += dt;
        if (timerSpawn >= tiempoEntreTuberiasActual) {
            timerSpawn = 0; // Reinicia el timer
            // Añade un tubo nuevo en la derecha de la pantalla (1.2f) con altura aleatoria
            tuberias.add(new Tuberia(1.2f, -0.3f + random.nextFloat() * 0.6f));
        }

        // Actualiza las posiciones de las tuberías y revisa colisiones
        Iterator<Tuberia> it = tuberias.iterator();
        while (it.hasNext()) {
            Tuberia t = it.next();
            t.x -= velocidadTuberiasActual * dt; // Mueve el tubo a la izquierda

            // Verifica si el tubo cruzó al pájaro para dar un punto
            if (t.x + (TUBERIA_ANCHO / 2) < BIRD_X && !t.puntuada) {
                t.puntuada = true;
                boolean puntoAnotado = false;
                if (pajaro1.vivo) {
                    pajaro1.puntaje++;
                    puntoAnotado = true;
                }
                if (pajaro2.vivo) {
                    pajaro2.puntaje++;
                    puntoAnotado = true;
                }
                if (pajaro3.vivo) {
                    pajaro3.puntaje++;
                    puntoAnotado = true;
                }
                if (puntoAnotado)
                    SoundManager.playPunto(); // Reproduce sonido al cruzar
                if (pajaro1.puntaje >= 6 || pajaro2.puntaje >= 6 || pajaro3.puntaje >= 6) {
                    gameOver = true;
                    return;
                }
            }

            // Verifica colisiones de cada pájaro vivo contra el tubo actual
            if (pajaro1.vivo && colisiona(pajaro1, t))
                pajaro1.morir();
            if (pajaro2.vivo && colisiona(pajaro2, t))
                pajaro2.morir();
            if (pajaro3.vivo && colisiona(pajaro3, t))
                pajaro3.morir();
            // Si el tubo sale por la izquierda de la pantalla, se elimina de la lista
            if (t.x < -1.3f)
                it.remove();
        }
    }

    // Comprueba si el rectángulo de choque de un pájaro toca la tubería superior o inferior
    private boolean colisiona(Pajaro p, Tuberia t) {
        // ¿El pájaro está en la posición horizontal del tubo?
        if (p.x + 0.04f > t.x - 0.09f && p.x - 0.04f < t.x + 0.09f) {
            // ¿El pájaro toca la parte de arriba o la parte de abajo del tubo?
            return p.y + 0.04f > t.gapCentroY + (GAP_ALTO / 2) || p.y - 0.04f < t.gapCentroY - (GAP_ALTO / 2);
        }
        return false;
    }

    private void render() {
        // Limpia la pantalla y la rellena de color de fondo 
        GL11.glClearColor(0.1f, 0.1f, 0.2f, 1.0f);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
        renderer.iniciarFrame(); // Activa el programa de shaders

        // 1. FONDO (Cielo y Sol)
        renderer.dibujar(0, 0.4f, 0, 0, 2.0f, 1.2f, 0, 0.4f, 0.7f, 1.0f); // Degradado/Color sólido cielo
        renderer.dibujarSol(0.7f, 0.75f); // Dibuja el sol rotativo en la esquina superior derecha

        // 2. PARALLAX (Nubes y Montañas)
        // Dibuja dos juegos seguidos (i=0, i=1) para crear la ilusión de escenario infinito
        for (int i = 0; i < 2; i++) {
            float shift = i * 2.0f; // Distancia entre el primer grupo y la copia del fondo

            // Nubes: Diferentes posiciones usando su respectivo offset
            renderer.dibujarNube(-0.5f + offsetNubes + shift, 0.75f);
            renderer.dibujarNube(0.2f + offsetNubes + shift, 0.85f);
            renderer.dibujarNube(0.9f + offsetNubes + shift, 0.65f);
            renderer.dibujarNube(1.5f + offsetNubes + shift, 0.78f);

            // Montañas: dibujadas de atrás hacia adelante para simular profundidad
            // Capa 3: muy al fondo (más oscuras y pequeñas)
            renderer.dibujarMontana(-0.7f + offsetMontanas + shift, -0.45f, 0.7f, 0.1f, 0.25f, 0.1f);
            renderer.dibujarMontana(0.2f + offsetMontanas + shift, -0.4f, 0.6f, 0.1f, 0.25f, 0.1f);
            renderer.dibujarMontana(1.1f + offsetMontanas + shift, -0.5f, 0.8f, 0.12f, 0.28f, 0.12f);

            // Capa 1: medio
            renderer.dibujarMontana(-0.4f + offsetMontanas + shift, -0.6f, 0.9f, 0.15f, 0.35f, 0.15f);
            renderer.dibujarMontana(0.5f + offsetMontanas + shift, -0.55f, 1.1f, 0.15f, 0.35f, 0.15f);
            renderer.dibujarMontana(1.4f + offsetMontanas + shift, -0.65f, 0.8f, 0.15f, 0.35f, 0.15f);

            // Capa 2: frente (más claras y grandes)
            renderer.dibujarMontana(0.0f + offsetMontanas + shift, -0.65f, 0.7f, 0.2f, 0.45f, 0.2f);
            renderer.dibujarMontana(0.9f + offsetMontanas + shift, -0.6f, 0.8f, 0.22f, 0.48f, 0.22f);
            renderer.dibujarMontana(1.8f + offsetMontanas + shift, -0.7f, 0.6f, 0.2f, 0.45f, 0.2f);
        }

        // 3. TUBERÍAS (Tubos verdes estilo retro)
        for (Tuberia t : tuberias) {
            float gTop = t.gapCentroY + (GAP_ALTO / 2); // Borde inferior del tubo de arriba
            float gBot = t.gapCentroY - (GAP_ALTO / 2); // Borde superior del tubo de abajo

            // Tubería de arriba (Cuerpo principal, Brillo a la izquierda y la tapa en la
            // punta)
            renderer.dibujar(t.x, gTop + 0.5f, 0, 0, TUBERIA_ANCHO, 1.0f, 0, 0.0f, 0.5f, 0.0f); // Cuerpo Verde oscuro
            renderer.dibujar(t.x - 0.05f, gTop + 0.5f, 0, 0, 0.04f, 1.0f, 0, 0.2f, 0.8f, 0.2f); // Brillo lateral
            renderer.dibujar(t.x, gTop + 0.05f, 0, 0, TUBERIA_ANCHO + 0.04f, 0.1f, 0, 0.0f, 0.4f, 0.0f); // Tapa más
                                                                                                         // ancha

            // Tubería de abajo (Igual construcción pero invertida respecto al GAP)
            renderer.dibujar(t.x, gBot - 0.5f, 0, 0, TUBERIA_ANCHO, 1.0f, 0, 0.0f, 0.5f, 0.0f);
            renderer.dibujar(t.x - 0.05f, gBot - 0.5f, 0, 0, 0.04f, 1.0f, 0, 0.2f, 0.8f, 0.2f);
            renderer.dibujar(t.x, gBot - 0.05f, 0, 0, TUBERIA_ANCHO + 0.04f, 0.1f, 0, 0.0f, 0.4f, 0.0f); // Tapa
        }

        // 4. SUELO (Base sólida donde termina el área de juego)
        renderer.dibujar(0, -0.85f, 0, 0, 2.0f, 0.3f, 0, 0.4f, 0.2f, 0.1f); // Tierra marrón profundo
        renderer.dibujar(0, -0.72f, 0, 0, 2.0f, 0.05f, 0, 0.0f, 0.8f, 0.0f); // Pasto superior (línea verde)

        // 5. PÁJAROS
        if (pajaro1.vivo) {
            renderer.dibujarPajaro(pajaro1);
        }
        if (pajaro2.vivo) {
            renderer.dibujarPajaro(pajaro2);
        }
        if (pajaro3.vivo) {
            renderer.dibujarPajaro(pajaro3);
        }

        // 6. UI (User Interface: Textos, Marcadores, y Pantallas finales)
        if (gameOver) {
            // Si el juego terminó, dibuja el gran cuadro correspondiente de la clase Interfas
            if (pajaro1.puntaje >= 6 || pajaro2.puntaje >= 6 || pajaro3.puntaje >= 6) {
                interfas.dibujarTerminado(pajaro1.puntaje, pajaro2.puntaje, pajaro3.puntaje);
            } else {
                interfas.dibujarGameOver(pajaro1.puntaje, pajaro2.puntaje, pajaro3.puntaje);
            }
        } else {
            // Si sigue el juego, dibuja el HUD normal superior
            int maxP = Math.max(pajaro1.puntaje, Math.max(pajaro2.puntaje, pajaro3.puntaje));
            int nivel = (maxP / 10) + 1; // Un nivel nuevo por cada 10 puntos
            float progreso = (maxP % 10) / 10.0f; // Progreso en decimal del 0.0 al 1.0
            interfas.dibujarHUD(pajaro1.puntaje, pajaro2.puntaje, pajaro3.puntaje, nivel, progreso);
        }

        actualizarTitulo(); // Actualiza el texto de la barra de la ventana (útil para debugear)
    }

    private void actualizarTitulo() {
        String msg = String.format("P1: %d | P2: %d | P3: %d", pajaro1.puntaje, pajaro2.puntaje, pajaro3.puntaje);
        if (gameOver) {
            if (pajaro1.puntaje >= 6 || pajaro2.puntaje >= 6 || pajaro3.puntaje >= 6) {
                msg += " - JUEGO A TERMINADO!!";
            } else {
                msg += " - GAME OVER (R para reiniciar)";
            }
        } else if (!started) {
            msg += " - SPACE o W para saltar";
        }
        GLFW.glfwSetWindowTitle(window, msg); // Pone el título en el borde superior de Windows
    }

    private void loop() {
        float lastTime = (float) GLFW.glfwGetTime(); // Toma el tiempo inicial
        // Mientras la ventana no deba cerrarse (por ejemplo pulsando la X roja de Windows)
        while (!GLFW.glfwWindowShouldClose(window)) {
            float now = (float) GLFW.glfwGetTime();
            // dt = delta time, tiempo transcurrido desde el último frame 
            float dt = Math.min(now - lastTime, 0.033f);
            lastTime = now;

            input.actualizar(); // Revisa qué teclas se están apretando

            // Si apretaron la letra 'R' en el teclado y ya perdieron, reinicia la partida
            if (input.fueTeclaRecienPresionada(GLFW.GLFW_KEY_R) && gameOver)
                resetGame();

            // Si el jugador 1 aprieta ESPACIO y está vivo, el juego arranca y el pájaro salta
            if (input.fueTeclaRecienPresionada(pajaro1.teclaControl) && pajaro1.vivo) {
                started = true;
                pajaro1.saltar();
            }
            // Si el jugador 2 aprieta W y está vivo, el juego arranca y el pájaro salta
            if (input.fueTeclaRecienPresionada(pajaro2.teclaControl) && pajaro2.vivo) {
                started = true;
                pajaro2.saltar();
            }
            // Si el jugador 3 aprieta A y está vivo, el juego arranca y el pájaro salta
            if (input.fueTeclaRecienPresionada(pajaro3.teclaControl) && pajaro3.vivo) {
                started = true;
                pajaro3.saltar();
            }

            procesarLogica(dt); // Mueve todos los elementos usando la física
            render(); // Dibuja todos los elementos gráficos actualizados

            GLFW.glfwSwapBuffers(window); // Intercambia los buffers oculto y visible (previene parpadeos)
            GLFW.glfwPollEvents(); // Procesa eventos del sistema operativo (clics, teclas, etc)
        }
    }

    private void cleanup() {
        // Libera la memoria de OpenGL
        renderer.limpiar();
        // Destruye la ventana y termina la ejecución de GLFW
        GLFW.glfwDestroyWindow(window);
        GLFW.glfwTerminate();
    }

    public static void main(String[] args) {
        new AppFlappyBird().run();
    }
}