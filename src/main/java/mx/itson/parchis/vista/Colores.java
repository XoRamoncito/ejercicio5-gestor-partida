package mx.itson.parchis.vista;

import java.awt.Color;
import mx.itson.parchis.modelo.ColorJugador;

/** Los colores de la pantalla, cada uno con un nombre, en un solo lugar. */
final class Colores {

    static final Color FONDO = new Color(0xF3F4EF);
    static final Color BASE_SIN_USAR = new Color(0xD9DDD3);
    static final Color PASILLO_SIN_USAR = new Color(0xE7EAE3);
    static final Color BORDE_CELDA = new Color(0xB9C0B5);
    static final Color META = new Color(0x14233A);
    static final Color TEXTO_META = new Color(0xA9B6C6);
    static final Color FICHA_MOVIBLE = new Color(0x2A7A50);

    private Colores() {
    }

    static Color de(ColorJugador color) {
        return new Color(color.getRgb());
    }

    /** El color del jugador, más claro, para sus casillas. */
    static Color claroDe(ColorJugador jugador) {
        Color color = de(jugador);
        return new Color(
                (color.getRed() + 2 * 255) / 3,
                (color.getGreen() + 2 * 255) / 3,
                (color.getBlue() + 2 * 255) / 3);
    }
}
