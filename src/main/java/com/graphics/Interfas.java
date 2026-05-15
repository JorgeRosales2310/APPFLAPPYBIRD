package com.graphics;

public class Interfas {

    // Referencia al motor de renderizado para poder dibujar primitivas (rectángulos, etc.)
    private Renderer renderer;

    // Constructor: recibe el renderer principal y lo guarda para usarlo en los métodos de dibujo
    public Interfas(Renderer renderer) {
        this.renderer = renderer;
    }

    // Método principal para dibujar el HUD durante el juego
    public void dibujarHUD(int score1, int score2, int nivel, float progresoNivel) {
        // Colores base de los paneles superiores (Azul oscuro para el contraste con el cielo)
        float rPanel = 0.05f;
        float gPanel = 0.1f;
        float bPanel = 0.2f;

        // Panel Jugador 1 (Izquierda)
        // Dibuja el fondo del panel izquierdo
        renderer.dibujar(-0.65f, 0.85f, 0, 0, 0.38f, 0.15f, 0, rPanel, gPanel, bPanel);
        // Dibuja el ícono del Pájaro Amarillo (Jugador 1) en el panel
        dibujarCabezaPajaro(-0.78f, 0.85f, 0.85f, 0.85f, 0.20f); 
        // Dibuja el puntaje actual del Jugador 1 en color amarillo
        dibujarNumero(score1, -0.55f, 0.85f, 1.2f, 1.0f, 0.9f, 0.2f); 

        // Panel Nivel (Centro)
        // Dibuja el fondo del panel central
        renderer.dibujar(0.0f, 0.88f, 0, 0, 0.3f, 0.1f, 0, rPanel, gPanel, bPanel);
        // Dibuja el fondo oscuro de la barra de progreso del nivel
        renderer.dibujar(-0.05f, 0.88f, 0, 0, 0.12f, 0.03f, 0, 0.2f, 0.2f, 0.2f);
        
        // Calcula el ancho de la barra de progreso basado en el porcentaje completado (progresoNivel)
        float fillWidth = 0.12f * progresoNivel;
        if (fillWidth > 0) {
            // Dibuja el relleno de la barra de progreso en color naranja/dorado.
            // Se ajusta la posición X para que la barra crezca de izquierda a derecha.
            renderer.dibujar(-0.05f - 0.06f + (fillWidth / 2.0f), 0.88f, 0, 0, fillWidth, 0.03f, 0, 0.8f, 0.6f, 0.2f);
        }
        // Dibuja el número del nivel actual a la derecha de la barra de progreso
        dibujarNumero(nivel, 0.08f, 0.88f, 0.8f, 1.0f, 1.0f, 1.0f);

        // Panel Jugador 2 (Derecha) 
        // Dibuja el fondo del panel derecho
        renderer.dibujar(0.65f, 0.85f, 0, 0, 0.38f, 0.15f, 0, rPanel, gPanel, bPanel);
        // Dibuja el ícono del Pájaro Azul (Jugador 2) en el panel
        dibujarCabezaPajaro(0.52f, 0.85f, 0.20f, 0.85f, 0.98f); 
        // Dibuja el puntaje actual del Jugador 2 en color azul claro
        dibujarNumero(score2, 0.75f, 0.85f, 1.2f, 0.2f, 0.85f, 0.98f); 
    }

    // Método para dibujar la pantalla de "Game Over" cuando ambos pierden
    public void dibujarGameOver(int score1, int score2) {
        // --- Fondo oscurecido (Overlay) ---
        // Dibuja un rectángulo negro semi-transparente (alfa = 0.3f) que cubre toda la pantalla
        renderer.dibujar(0, 0, 0, 0, 2.0f, 2.0f, 0, 0, 0, 0.3f);

        // Panel principal (Marrón/Rojo oscuro)
        // Dibuja el recuadro central grande donde irán los puntajes finales
        renderer.dibujar(0, 0.0f, 0, 0, 1.5f, 1.0f, 0, 0.2f, 0.05f, 0.05f);

        // Texto GAME OVER
        // Llama al método custom para dibujar las letras G-A-M-E O-V-E-R en color dorado
        dibujarTexto("GAME OVER", 0.0f, 0.40f, 1.0f, 0.9f, 0.8f, 0.2f); 

        // Puntuaciones Finales
        // Dibuja el ícono del Jugador 1 (Amarillo) en la mitad superior del panel
        dibujarCabezaPajaro(-0.25f, -0.1f, 0.85f, 0.85f, 0.20f);
        // Muestra el puntaje final del Jugador 1
        dibujarNumero(score1, -0.05f, -0.1f, 1.5f, 1.0f, 0.9f, 0.2f);

        // Dibuja el ícono del Jugador 2 (Azul) en la mitad inferior del panel
        dibujarCabezaPajaro(-0.25f, -0.3f, 0.20f, 0.85f, 0.98f);
        // Muestra el puntaje final del Jugador 2
        dibujarNumero(score2, -0.05f, -0.3f, 1.5f, 0.2f, 0.85f, 0.98f);
    }

    // Método auxiliar para dibujar un pájaro en miniatura (solo la cabeza)
    private void dibujarCabezaPajaro(float x, float y, float r, float g, float b) {
        // Dibuja el cuerpo principal del pájaro usando el color especificado
        renderer.dibujar(x, y, 0, 0, 0.08f, 0.07f, 0, r, g, b);
        // Dibuja la parte blanca del ojo
        renderer.dibujar(x, y, 0.02f, 0.015f, 0.025f, 0.025f, 0, 1.0f, 1.0f, 1.0f);
        // Dibuja la pupila negra dentro del ojo
        renderer.dibujar(x, y, 0.03f, 0.015f, 0.01f, 0.01f, 0, 0.0f, 0.0f, 0.0f);
        // Dibuja el pico naranja apuntando a la derecha
        renderer.dibujar(x, y, 0.045f, -0.01f, 0.035f, 0.02f, 0, 1.0f, 0.5f, 0.0f);
    }

    // Método que convierte un número entero a texto para dibujarlo dígito por dígito
    private void dibujarNumero(int numero, float x, float y, float tam, float r, float g, float b) {
        // Convierte el número entero a cadena de texto (ej. 123 -> "123")
        String numStr = String.valueOf(numero);
        float offset = 0; // Desplazamiento horizontal para cada nuevo dígito
        float espacio = 0.08f * tam; // Separación calculada en base al tamaño

        // Calcula la posición inicial (inicioX) para que todo el número quede centrado
        float inicioX = x - ((numStr.length() - 1) * espacio) / 2.0f;

        // Recorre cada carácter (dígito) del string
        for (int i = 0; i < numStr.length(); i++) {
            // Convierte el carácter (ej. '5') a valor numérico entero (5)
            int digito = numStr.charAt(i) - '0';
            // Llama al método que dibuja físicamente un solo dígito en pantalla
            dibujarDigito(digito, inicioX + offset, y, tam, r, g, b);
            // Incrementa el desplazamiento para que el siguiente dígito se dibuje más a la derecha
            offset += espacio;
        }
    }

    // Método que dibuja un dígito específico simulando un display digital de 7 segmentos
    private void dibujarDigito(int digito, float x, float y, float tam, float r, float g, float b) {
        // Definición de las proporciones de los segmentos en base al tamaño total (tam)
        float w = 0.04f * tam; // Ancho del dígito
        float h = 0.05f * tam; // Mitad de la altura del dígito
        float t = 0.012f * tam; // Grosor (thickness) de cada línea/segmento

        // Arreglo booleano para los 7 segmentos: 
        // [top, top-left, top-right, middle, bottom-left, bottom-right, bottom]
        boolean[] segs = new boolean[7]; 

        // Configuración lógica: Se encienden (true) o apagan (false) los segmentos según el número
        switch(digito) {
            case 0: segs = new boolean[]{true, true, true, false, true, true, true}; break; // El 0 no tiene segmento central
            case 1: segs = new boolean[]{false, false, true, false, false, true, false}; break; // Solo los dos de la derecha
            case 2: segs = new boolean[]{true, false, true, true, true, false, true}; break; // Forma de 'Z' o '2'
            case 3: segs = new boolean[]{true, false, true, true, false, true, true}; break; // Tres rayas horizontales y dos derechas
            case 4: segs = new boolean[]{false, true, true, true, false, true, false}; break; // El 4
            case 5: segs = new boolean[]{true, true, false, true, false, true, true}; break; // Forma de 'S' o '5'
            case 6: segs = new boolean[]{true, true, false, true, true, true, true}; break; // El 6
            case 7: segs = new boolean[]{true, false, true, false, false, true, false}; break; // El 7
            case 8: segs = new boolean[]{true, true, true, true, true, true, true}; break; // Todos encendidos para el 8
            case 9: segs = new boolean[]{true, true, true, true, false, true, true}; break; // El 9
        }

        //Renderizado de cada segmento encendido usando rectángulos
        
        // Dibuja el segmento superior (Top)
        if(segs[0]) renderer.dibujar(x, y, 0, h, w + t, t, 0, r, g, b);
        // Dibuja el segmento superior izquierdo (Top Left)
        if(segs[1]) renderer.dibujar(x, y, -w/2, h/2, t, h, 0, r, g, b);
        // Dibuja el segmento superior derecho (Top Right)
        if(segs[2]) renderer.dibujar(x, y, w/2, h/2, t, h, 0, r, g, b);
        // Dibuja el segmento central (Middle)
        if(segs[3]) renderer.dibujar(x, y, 0, 0, w + t, t, 0, r, g, b);
        // Dibuja el segmento inferior izquierdo (Bottom Left)
        if(segs[4]) renderer.dibujar(x, y, -w/2, -h/2, t, h, 0, r, g, b);
        // Dibuja el segmento inferior derecho (Bottom Right)
        if(segs[5]) renderer.dibujar(x, y, w/2, -h/2, t, h, 0, r, g, b);
        // Dibuja el segmento inferior (Bottom)
        if(segs[6]) renderer.dibujar(x, y, 0, -h, w + t, t, 0, r, g, b);
    }

    // Método para imprimir textos (cadenas de caracteres) en pantalla letra por letra
    private void dibujarTexto(String texto, float x, float y, float tam, float r, float g, float b) {
        float offset = 0; // Desplazamiento horizontal para separar las letras
        float espacio = 0.08f * tam; // Cantidad de espacio entre cada carácter

        // Calcula la posición inicial para que el texto entero quede centrado en 'x'
        float inicioX = x - ((texto.length() - 1) * espacio) / 2.0f;

        // Recorre todos los caracteres de la palabra
        for (int i = 0; i < texto.length(); i++) {
            char c = texto.charAt(i);
            // Si no es un espacio en blanco, dibuja la letra
            if (c != ' ') {
                dibujarLetra(c, inicioX + offset, y, tam, r, g, b);
            }
            // Mueve el cursor a la derecha para la próxima letra (incluso si fue un espacio)
            offset += espacio;
        }
    }

    // Mini-motor de fuentes: dibuja una letra específica usando bloques geométricos (estilo pixel art)
    private void dibujarLetra(char c, float x, float y, float tam, float r, float g, float b) {
        float w = 0.04f * tam; // Ancho base de la letra
        float h = 0.05f * tam; // Mitad de la altura
        float t = 0.012f * tam; // Grosor del "pincel"

        // Según la letra solicitada, se construyen sus palos y barras sumando rectángulos
        switch(c) {
            case 'G':
                renderer.dibujar(x, y, 0, h, w + t, t, 0, r, g, b); // Barra superior
                renderer.dibujar(x, y, -w/2, 0, t, h * 2 + t, 0, r, g, b); // Barra vertical izquierda larga
                renderer.dibujar(x, y, 0, -h, w + t, t, 0, r, g, b); // Barra inferior
                renderer.dibujar(x, y, w/2, -h/2, t, h + t, 0, r, g, b); // Barra vertical derecha (solo mitad inferior)
                renderer.dibujar(x, y, w/4, 0, w/2 + t, t, 0, r, g, b); // Barra central pequeña que entra a la G
                break;
            case 'A':
                renderer.dibujar(x, y, 0, h, w + t, t, 0, r, g, b); // Barra superior plana
                renderer.dibujar(x, y, -w/2, 0, t, h * 2 + t, 0, r, g, b); // Pata izquierda
                renderer.dibujar(x, y, w/2, 0, t, h * 2 + t, 0, r, g, b); // Pata derecha
                renderer.dibujar(x, y, 0, 0, w + t, t, 0, r, g, b); // Puente central
                break;
            case 'M':
                renderer.dibujar(x, y, -w/2, 0, t, h * 2 + t, 0, r, g, b); // Pata izquierda
                renderer.dibujar(x, y, w/2, 0, t, h * 2 + t, 0, r, g, b); // Pata derecha
                renderer.dibujar(x, y, 0, h, w + t, t, 0, r, g, b); // Techo que las une
                renderer.dibujar(x, y, 0, h/2, t, h, 0, r, g, b); // Palo corto en el medio bajando del techo
                break;
            case 'E':
                renderer.dibujar(x, y, -w/2, 0, t, h * 2 + t, 0, r, g, b); // Barra vertical izquierda (columna)
                renderer.dibujar(x, y, 0, h, w + t, t, 0, r, g, b); // Brazo superior
                renderer.dibujar(x, y, 0, 0, w + t, t, 0, r, g, b); // Brazo central
                renderer.dibujar(x, y, 0, -h, w + t, t, 0, r, g, b); // Brazo inferior
                break;
            case 'O':
                renderer.dibujar(x, y, -w/2, 0, t, h * 2 + t, 0, r, g, b); // Pared izquierda
                renderer.dibujar(x, y, w/2, 0, t, h * 2 + t, 0, r, g, b); // Pared derecha
                renderer.dibujar(x, y, 0, h, w + t, t, 0, r, g, b); // Techo
                renderer.dibujar(x, y, 0, -h, w + t, t, 0, r, g, b); // Suelo (cierra la caja)
                break;
            case 'V':
                renderer.dibujar(x, y, -w/2, h/2, t, h, 0, r, g, b); // Pared izquierda superior
                renderer.dibujar(x, y, w/2, h/2, t, h, 0, r, g, b); // Pared derecha superior
                renderer.dibujar(x, y, -w/4, -h/2, t, h, 0, r, g, b); // Cierre en embudo izquierdo inferior
                renderer.dibujar(x, y, w/4, -h/2, t, h, 0, r, g, b); // Cierre en embudo derecho inferior
                renderer.dibujar(x, y, 0, -h, w/2, t, 0, r, g, b); // Base pequeña chata (simula la punta de la V)
                break;
            case 'R':
                renderer.dibujar(x, y, -w/2, 0, t, h * 2 + t, 0, r, g, b); // Espalda izquierda
                renderer.dibujar(x, y, 0, h, w + t, t, 0, r, g, b); // Techo
                renderer.dibujar(x, y, w/2, h/2, t, h + t, 0, r, g, b); // Cierre derecho de la cabeza
                renderer.dibujar(x, y, 0, 0, w + t, t, 0, r, g, b); // Base de la cabeza (forma la 'P')
                // Pierna simulada en diagonal usando bloques escalonados
                renderer.dibujar(x, y, w/4, -h/4, t, h/2 + t, 0, r, g, b); // Mitad superior
                renderer.dibujar(x, y, w/2, -h*0.75f, t, h/2 + t, 0, r, g, b); // Mitad inferior
                break;
        }
    }
}
