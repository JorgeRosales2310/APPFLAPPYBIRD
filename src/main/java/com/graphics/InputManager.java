package com.graphics;

import org.lwjgl.glfw.GLFW;

public class InputManager {
    // Referencia a la ventana de GLFW donde ocurren los eventos de teclado
    private long window;
    // Arreglo que guarda el estado de todas las teclas en el frame actual (true = presionada)
    private boolean[] currentKeys = new boolean[GLFW.GLFW_KEY_LAST];
    // Arreglo que guarda el estado de todas las teclas en el frame anterior
    private boolean[] previousKeys = new boolean[GLFW.GLFW_KEY_LAST];

    // Constructor: Inicializa el manejador de entrada conectándolo a la ventana principal
    public InputManager(long window) {
        this.window = window;
    }

    // Se llama una vez por cada frame del juego
    public void actualizar() {
        // Copia el estado de las teclas actuales al arreglo de "teclas anteriores"
        System.arraycopy(currentKeys, 0, previousKeys, 0, currentKeys.length);
        // Recorre las teclas válidas (empezando desde el espacio = 32)
        for (int i = 32; i < currentKeys.length; i++) {
            // Pregunta a GLFW si la tecla 'i' está siendo presionada en este momento
            currentKeys[i] = GLFW.glfwGetKey(window, i) == GLFW.GLFW_PRESS;
        }
    }

    // Retorna true si la tecla se mantiene presionada
    public boolean esTeclaPresionada(int key) {
        return currentKeys[key];
    }

    // Retorna true SOLO en el instante en que la tecla se presiona (no si se mantiene apretada)
    public boolean fueTeclaRecienPresionada(int key) {
        return currentKeys[key] && !previousKeys[key];
    }
}