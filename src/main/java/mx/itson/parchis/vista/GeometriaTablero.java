package mx.itson.parchis.vista;

import mx.itson.parchis.modelo.ColorJugador;
import mx.itson.parchis.modelo.ReglasParchis;

/**
 * Dónde está cada cosa en la cuadrícula de 19 x 19 del tablero, en filas y columnas. No sabe de
 * pixeles: {@link TableroPanel} convierte estas medidas a pantalla.
 */
final class GeometriaTablero {

    static final int CELDAS = 19;
    static final int LARGO_PASILLO = 7;

    private static final double[][] LUGARES_EN_BASE = {{2.6, 2.6}, {2.6, 5.4}, {5.4, 2.6}, {5.4, 5.4}};

    private GeometriaTablero() {
    }

    /** Fila y columna de la casilla {@code i} del recorrido (0 a 67), en el sentido de las manecillas del reloj. */
    static int[] casilla(int i) {
        if (i <= 7) return new int[] {8, i};
        if (i <= 15) return new int[] {15 - i, 8};
        if (i == 16) return new int[] {0, 9};
        if (i <= 24) return new int[] {i - 17, 10};
        if (i <= 32) return new int[] {8, i - 14};
        if (i == 33) return new int[] {9, 18};
        if (i <= 41) return new int[] {10, 52 - i};
        if (i <= 49) return new int[] {i - 31, 10};
        if (i == 50) return new int[] {18, 9};
        if (i <= 58) return new int[] {69 - i, 8};
        if (i <= 66) return new int[] {10, 66 - i};
        return new int[] {9, 0};
    }

    /** Fila y columna de la casilla {@code i} (0 a 6) del pasillo de un color. */
    static int[] pasillo(ColorJugador color, int i) {
        return switch (color) {
            case ROJO -> new int[] {9, 1 + i};
            case AZUL -> new int[] {1 + i, 9};
        };
    }

    /** Fila y columna de la esquina del cuadro de 8 x 8 que es la base de un color. */
    static int[] base(ColorJugador color) {
        return switch (color) {
            case ROJO -> new int[] {0, 0};
            case AZUL -> new int[] {0, 11};
        };
    }

    private static int[][] lugaresEnMeta(ColorJugador color) {
        return switch (color) {
            case ROJO -> new int[][] {{9, 8}, {10, 8}, {10, 9}, {9, 9}};
            case AZUL -> new int[][] {{8, 9}, {8, 10}, {9, 10}, {8, 8}};
        };
    }

    /** Centro de una ficha, en filas y columnas (con decimales), según su color, su número y su posición. */
    static double[] centroFicha(ColorJugador color, int numero, int posicion) {
        if (posicion == ReglasParchis.EN_BASE) {
            // En la base: cuatro lugares dentro del cuadro de su color
            int[] esquina = base(color);
            return new double[] {esquina[0] + LUGARES_EN_BASE[numero][0], esquina[1] + LUGARES_EN_BASE[numero][1]};
        }
        if (posicion == ReglasParchis.META) {
            // En la meta: cada color ocupa su lado del cuadro central
            int[] lugar = lugaresEnMeta(color)[numero];
            return new double[] {lugar[0] + 0.5, lugar[1] + 0.5};
        }
        int[] celda = ReglasParchis.enRecorridoComun(posicion)
                ? casilla(ReglasParchis.casillaDelTablero(color, posicion))
                : pasillo(color, posicion - ReglasParchis.ULTIMA_CASILLA_COMUN - 1);
        // Un pequeño desplazamiento para que dos fichas en la misma casilla se distingan
        double desplazamiento = (numero - 1.5) * 0.12;
        return new double[] {celda[0] + 0.5 + desplazamiento, celda[1] + 0.5 + desplazamiento};
    }
}
