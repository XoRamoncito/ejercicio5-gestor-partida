package mx.itson.parchis.modelo;

/** Lo que necesita saber quien muestra la partida (la vista) para redibujarse. */
public interface ObservadorPartida {

    void estadoCambiado();

    void partidaTerminada(Jugador ganador);
}
