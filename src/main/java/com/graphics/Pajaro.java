package com.graphics;

import org.lwjgl.glfw.GLFW;

public class Pajaro {
    public float x, y, velY; // Coordenadas y velocidad vertical actual
    public boolean vivo; // Estado del pájaro
    public int puntaje; // Marcador de puntos
    public float r, g, b; // Color del pájaro (RGB)
    public int teclaControl; // Qué tecla lo hace saltar (Espacio o W)

    // Constantes físicas del salto
    private static final float GRAVEDAD = -2.2f; // Fuerza que lo tira hacia el suelo
    private static final float IMPULSO_SALTO = 0.95f; // Fuerza hacia arriba al apretar la tecla
    private static final float VELOCIDAD_MAX_CAIDA = -1.8f; // Límite de velocidad cayendo

    public Pajaro(float x, float r, float g, float b, int teclaControl) {
        this.x = x; // Siempre empiezan en la misma posición X
        this.y = 0.0f; // Empiezan en el centro de la pantalla
        this.velY = 0.0f; // Empiezan sin moverse verticalmente
        this.vivo = true;
        this.puntaje = 0;
        this.r = r;
        this.g = g;
        this.b = b;
        this.teclaControl = teclaControl;
    }

    // Se llama cuando el usuario presiona la tecla de este pájaro
    public void saltar() {
        velY = IMPULSO_SALTO; // Anula la caída e impulsa hacia arriba
    }

    public void morir() {
        vivo = false;
    }

    // Calcula la nueva posición basada en la física de la gravedad
    public void actualizar(float dt) {
        if (!vivo) return; // Si ya chocó, no hace nada

        velY += GRAVEDAD * dt; // La gravedad va restando a la velocidad Y
        
        // Evitar que caiga demasiado rápido
        if (velY < VELOCIDAD_MAX_CAIDA)
            velY = VELOCIDAD_MAX_CAIDA;
            
        y += velY * dt; // Aplica la velocidad a la posición real

        // Si se va por arriba de la pantalla (y > 1.0) o toca el suelo (y < -0.8), muere
        if (y > 1.0f || y < -0.8f)
            morir();
    }
}