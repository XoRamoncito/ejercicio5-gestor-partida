package mx.itson.parchis;

/** El jugador de las fichas azules. */
public class JugadorAzul extends Jugador {

    public JugadorAzul(String nombre) {
        super(nombre);
    }

    @Override
    public String getColor() {
        return "azul";
    }

    @Override
    public int getCasillaSalida() {
        return 18;
    }
}
