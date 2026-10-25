package mx.itson.parchis.modelo;

import java.util.Arrays;

/**
 * Un jugador de la partida: su nombre, su color y la posición de sus cuatro fichas.
 *
 * Antes había una subclase por color ({@code JugadorRojo}, {@code JugadorAzul}) solo para
 * devolver dos datos fijos. Ahora el jugador tiene un {@link ColorJugador}: agregar un color es
 * agregar una línea al enum, no una clase nueva.
 */
public class Jugador {

    private final String nombre;
    private final ColorJugador color;

    /**
     * Posición de cada ficha: -1 en la base, de 0 a 66 en el recorrido, de 67 a 73 en el
     * pasillo de su color y 74 en la meta.
     */
    private final int[] fichas = new int[ReglasParchis.FICHAS_POR_JUGADOR];

    public Jugador(String nombre, ColorJugador color) {
        this.nombre = nombre;
        this.color = color;
        regresarTodasABase();
    }

    public String getNombre() {
        return nombre;
    }

    public ColorJugador getColor() {
        return color;
    }

    public int cantidadDeFichas() {
        return fichas.length;
    }

    public int getPosicion(int ficha) {
        return fichas[ficha];
    }

    void setPosicion(int ficha, int posicion) {
        fichas[ficha] = posicion;
    }

    void regresarABase(int ficha) {
        fichas[ficha] = ReglasParchis.EN_BASE;
    }

    void regresarTodasABase() {
        Arrays.fill(fichas, ReglasParchis.EN_BASE);
    }

    public boolean puedeMoverAlguna(int dado) {
        for (int posicion : fichas) {
            if (ReglasParchis.puedeMover(posicion, dado)) {
                return true;
            }
        }
        return false;
    }

    public boolean llegaronTodasALaMeta() {
        for (int posicion : fichas) {
            if (posicion != ReglasParchis.META) {
                return false;
            }
        }
        return true;
    }
}
