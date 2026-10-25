package mx.itson.parchis.modelo;

/**
 * Los colores que juegan. Todo lo que depende del color (su nombre, su casilla de salida y su
 * tono) vive aquí, en un solo lugar, en vez de repetirse como texto por todo el programa.
 */
public enum ColorJugador {

    ROJO("rojo", 1, 0xC23B2E),
    AZUL("azul", 18, 0x2563A8);

    private final String nombre;
    private final int casillaSalida;
    private final int rgb;

    ColorJugador(String nombre, int casillaSalida, int rgb) {
        this.nombre = nombre;
        this.casillaSalida = casillaSalida;
        this.rgb = rgb;
    }

    /** La casilla del recorrido donde salen sus fichas de la base. */
    public int getCasillaSalida() {
        return casillaSalida;
    }

    /** El tono del color en formato 0xRRGGBB, para que la vista lo dibuje. */
    public int getRgb() {
        return rgb;
    }

    /** El nombre en minúsculas, tal como aparece en la bitácora y en los mensajes de red. */
    @Override
    public String toString() {
        return nombre;
    }
}
