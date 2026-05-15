# APPFLAPPYBIRD
PROGRAMACION GRAFICA
# Flappy Bird - Multijugador Geométrico

## Integrantes
- [Tu Nombre y Apellido]

## Controles del Juego
El juego admite dos jugadores simultáneos en la misma ventana:
- **Jugador 1 (Pájaro Amarillo):** Presionar la tecla `ESPACIO` para saltar.
- **Jugador 2 (Pájaro Azul):** Presionar la tecla `W` para saltar.
- **Reinicio:** Presionar la tecla `R` cuando aparezca la pantalla de Game Over.

## Instrucciones de Compilación y Ejecución
Este proyecto utiliza Maven para la gestión de dependencias (LWJGL para OpenGL). 
Para ejecutarlo desde la terminal, navega a la carpeta raíz del proyecto y usa:

```bash
mvn clean compile exec:java -Dexec.mainClass="com.graphics.AppFlappyBird"
