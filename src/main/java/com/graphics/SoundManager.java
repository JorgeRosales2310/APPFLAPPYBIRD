package com.graphics;

import javax.sound.sampled.*;

public class SoundManager {
    private static Clip clipSalto;
    private static Clip clipPunto;
    private static Clip clipGolpe;

    public static void init() {
        try {
            // Formato de audio clásico calidad CD pero mono
            AudioFormat format = new AudioFormat(44100, 8, 1, true, false);
            
            // 1. Sonido de Salto (Frecuencia que sube rápidamente - Sweep)
            byte[] saltoData = new byte[8000];
            double phase = 0;
            for (int i = 0; i < saltoData.length; i++) {
                // La frecuencia sube de 300Hz a 800Hz
                double freq = 300 + (i / 8000.0) * 500; 
                phase += 2.0 * Math.PI * freq / 44100.0;
                double fadeOut = 1.0 - (i / 8000.0);
                saltoData[i] = (byte)(Math.sin(phase) * 50 * fadeOut);
            }
            clipSalto = AudioSystem.getClip();
            clipSalto.open(format, saltoData, 0, saltoData.length);

            // 2. Sonido de Punto (Efecto moneda)
            byte[] puntoData = new byte[16000];
            phase = 0;
            for (int i = 0; i < puntoData.length; i++) {
                double freq = (i < 4000) ? 987.77 : 1318.51; // B5 y E6
                phase += 2.0 * Math.PI * freq / 44100.0;
                double fadeOut = 1.0;
                if (i > 4000) {
                    fadeOut = 1.0 - ((i - 4000) / 12000.0);
                }
                // Se usa una "onda cuadrada" (square wave) para que suene como un juego de 8-bits
                byte sample = (byte)((Math.sin(phase) > 0 ? 1 : -1) * 30 * fadeOut);
                puntoData[i] = sample;
            }
            clipPunto = AudioSystem.getClip();
            clipPunto.open(format, puntoData, 0, puntoData.length);

            // 3. Sonido de Golpe/Muerte (Ruido blanco con decaimiento rápido)
            byte[] golpeData = new byte[12000];
            for (int i = 0; i < golpeData.length; i++) {
                // Decaimiento exponencial para que suene como un impacto seco
                double fadeOut = Math.pow(1.0 - (i / 12000.0), 3.0); 
                golpeData[i] = (byte)((Math.random() * 100 - 50) * fadeOut);
            }
            clipGolpe = AudioSystem.getClip();
            clipGolpe.open(format, golpeData, 0, golpeData.length);

        } catch (Exception e) {
            System.err.println("No se pudo iniciar el sistema de audio: " + e.getMessage());
        }
    }

    private static long lastSaltoTime = 0;

    public static void playSalto() {
        if (clipSalto != null) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastSaltoTime > 50) {
                clipSalto.setFramePosition(0);
                clipSalto.start();
                lastSaltoTime = currentTime;
            }
        }
    }

    public static void playPunto() {
        if (clipPunto != null) {
            clipPunto.setFramePosition(0);
            clipPunto.start();
        }
    }

    public static void playGolpe() {
        if (clipGolpe != null) {
            clipGolpe.setFramePosition(0);
            clipGolpe.start();
        }
    }
}
