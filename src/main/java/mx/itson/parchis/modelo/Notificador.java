package mx.itson.parchis.modelo;

/**
 * A quién se le avisa cada jugada (la computadora del rival). La partida solo conoce esta
 * interfaz; el socket es un detalle de {@code mx.itson.parchis.red}.
 */
public interface Notificador {

    void enviar(String mensaje);
}
