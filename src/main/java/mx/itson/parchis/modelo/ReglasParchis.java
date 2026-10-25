package mx.itson.parchis.modelo;

/**
 * Las reglas del Parchís: qué movimientos se permiten y a dónde llega una ficha. Antes estas
 * condiciones estaban copiadas en tres lugares de {@code GestorPartida} (al tirar, al hacer clic y
 * al resaltar las fichas); ahora hay una sola versión.
 */
public final class ReglasParchis {

    /** Posición de una ficha que está en su base. */
    public static final int EN_BASE = -1;
    /** Casillas del recorrido común (0 a 67). */
    public static final int TOTAL_CASILLAS = 68;
    /** Última posición del recorrido común; de 67 a 73 es el pasillo de su color. */
    public static final int ULTIMA_CASILLA_COMUN = 66;
    /** Posición de una ficha que llegó a la meta. */
    public static final int META = 74;
    public static final int FICHAS_POR_JUGADOR = 4;

    private static final int DADO_PARA_SALIR = 5;
    private static final int DADO_QUE_REPITE = 6;
    private static final int MAXIMO_SEIS_SEGUIDOS = 3;

    private ReglasParchis() {
    }

    /**
     * Por qué una ficha no se puede mover con ese dado, o {@code null} si sí se puede.
     */
    public static String motivoParaNoMover(int posicion, int dado) {
        if (posicion == META) {
            return "Esa ficha ya llegó a la meta.";
        }
        if (posicion == EN_BASE && dado != DADO_PARA_SALIR) {
            return "Para sacar una ficha de la base necesitas un " + DADO_PARA_SALIR + ".";
        }
        if (posicion >= 0 && posicion + dado > META) {
            return "Esa ficha necesita exactamente un " + (META - posicion) + " para llegar a la meta.";
        }
        return null;
    }

    public static boolean puedeMover(int posicion, int dado) {
        return motivoParaNoMover(posicion, dado) == null;
    }

    /** A dónde llega una ficha que se mueve con ese dado. */
    public static int nuevaPosicion(int posicion, int dado) {
        return posicion == EN_BASE ? 0 : posicion + dado;
    }

    /** Si la ficha está en el recorrido que comparten los colores, donde puede capturar o ser capturada. */
    public static boolean enRecorridoComun(int posicion) {
        return posicion >= 0 && posicion <= ULTIMA_CASILLA_COMUN;
    }

    /** La casilla del tablero (0 a 67) que ocupa una ficha del recorrido común. */
    public static int casillaDelTablero(ColorJugador color, int posicion) {
        return (color.getCasillaSalida() + posicion) % TOTAL_CASILLAS;
    }

    public static boolean sumaSeisSeguidos(int dado) {
        return dado == DADO_QUE_REPITE;
    }

    /** Con 6 se vuelve a tirar, máximo tres veces seguidas. */
    public static boolean vuelveATirar(int dado, int seisSeguidos) {
        return dado == DADO_QUE_REPITE && seisSeguidos < MAXIMO_SEIS_SEGUIDOS;
    }
}
