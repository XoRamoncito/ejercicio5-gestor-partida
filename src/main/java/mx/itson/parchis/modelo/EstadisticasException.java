package mx.itson.parchis.modelo;

/** No se pudieron leer o guardar las estadísticas. */
public class EstadisticasException extends Exception {

    public EstadisticasException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
