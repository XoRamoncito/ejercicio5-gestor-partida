package mx.itson.parchis.modelo;

import java.util.List;

/**
 * Donde se guardan las estadísticas. La partida no sabe que hay MySQL detrás: se podría cambiar
 * por un archivo o por una lista en memoria sin tocarla.
 */
public interface RepositorioEstadisticas {

    /** Si se pudo conectar al arrancar. */
    boolean estaDisponible();

    List<Estadistica> leer() throws EstadisticasException;

    /** Suma una partida jugada a cada color y una ganada al ganador. */
    void registrarPartida(ColorJugador ganador) throws EstadisticasException;
}
