package mx.itson.parchis;

/** El jugador de las fichas rojas. */
public class JugadorRojo extends Jugador {

    public JugadorRojo(String nombre) {
        super(nombre);
    }

    @Override
    public String getColor() {
        return "rojo";
    }

    @Override
    public int getCasillaSalida() {
        return 1;
    }
}
