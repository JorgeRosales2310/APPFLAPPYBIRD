package com.graphics;

public class Tuberia {
    public float x; // Posición horizontal actual del tubo (se mueve de derecha a izquierda)
    public float gapCentroY; // Posición vertical del centro del hueco por donde pasan los pájaros
    public boolean puntuada; // Indica si los pájaros ya pasaron este tubo (para no dar el punto 2 veces)

    // Constructor: Define la posición inicial y la altura aleatoria del hueco
    public Tuberia(float x, float gapCentroY) {
        this.x = x;
        this.gapCentroY = gapCentroY;
        this.puntuada = false; // Al inicio ningún pájaro la ha cruzado
    }
}