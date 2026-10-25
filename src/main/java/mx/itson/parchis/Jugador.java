package mx.itson.parchis;

/**
 * Un jugador de la partida: su nombre y la posición de sus cuatro fichas.
 */
public abstract class Jugador {

    private final String nombre;

    /**
     * Posición de cada ficha: -1 en la base, de 0 a 66 en el recorrido, de 67 a 73 en el
     * pasillo de su color y 74 en la meta.
     */
    private final int[] fichas = {-1, -1, -1, -1};

    protected Jugador(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public int[] getFichas() {
        return fichas;
    }

    /** El color del jugador, en minúsculas. */
    public abstract String getColor();

    /** La casilla del recorrido donde salen sus fichas de la base. */
    public abstract int getCasillaSalida();
}
