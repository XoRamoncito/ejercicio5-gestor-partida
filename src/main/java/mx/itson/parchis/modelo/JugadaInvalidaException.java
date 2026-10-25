package mx.itson.parchis.modelo;

/** El jugador intentó algo que las reglas no permiten; el mensaje dice por qué. */
public class JugadaInvalidaException extends Exception {

    public JugadaInvalidaException(String mensaje) {
        super(mensaje);
    }
}
