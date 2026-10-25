package mx.itson.parchis.modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * El estado de una partida y sus jugadas: de quién es el turno, qué salió en el dado y qué
 * pasa al mover una ficha. No dibuja, no abre sockets y no escribe SQL: recibe por el
 * constructor una {@link Bitacora}, un {@link Notificador} y un {@link RepositorioEstadisticas}
 * y solo habla con esas interfaces.
 */
public class Partida {

    private final Jugador rojo;
    private final Jugador azul;
    private final Bitacora bitacora;
    private final Notificador notificador;
    private final RepositorioEstadisticas estadisticas;
    private final List<ObservadorPartida> observadores = new ArrayList<>();
    private final List<String> jugadas = new ArrayList<>();
    private final Random azar = new Random();

    private Jugador enTurno;
    private int dado = 0;
    private boolean yaTiro = false;
    private int seisSeguidos = 0;
    private boolean terminada = false;

    public Partida(Jugador rojo, Jugador azul, Bitacora bitacora, Notificador notificador,
            RepositorioEstadisticas estadisticas) {
        this.rojo = rojo;
        this.azul = azul;
        this.bitacora = bitacora;
        this.notificador = notificador;
        this.estadisticas = estadisticas;
        this.enTurno = rojo;
    }

    public void agregarObservador(ObservadorPartida observador) {
        observadores.add(observador);
    }

    /** Anuncia quién empieza. Se llama una vez, cuando la ventana ya está lista. */
    public void iniciar() {
        bitacora.agregar("Empieza " + enTurno.getNombre() + " (" + enTurno.getColor() + ").");
        avisarCambio();
    }

    // ------------------------------------------------------------------ jugadas

    public void lanzarDado() throws JugadaInvalidaException {
        if (terminada) {
            throw new JugadaInvalidaException("La partida terminó. Empieza una nueva.");
        }
        if (yaTiro) {
            throw new JugadaInvalidaException("Ya tiraste: elige una ficha.");
        }
        procesarDado(azar.nextInt(6) + 1);
    }

    /** Aplica el valor del dado al jugador en turno. Es de paquete para poder probarlo con un dado fijo. */
    void procesarDado(int valor) {
        dado = valor;
        yaTiro = true;
        seisSeguidos = ReglasParchis.sumaSeisSeguidos(dado) ? seisSeguidos + 1 : 0;
        bitacora.agregar(enTurno.getNombre() + " sacó " + dado + ".");
        notificador.enviar("DADO;" + enTurno.getColor() + ";" + dado);

        if (!enTurno.puedeMoverAlguna(dado)) {
            bitacora.agregar("No puede mover ninguna ficha.");
            yaTiro = false;
            terminarTiro();
        }
        avisarCambio();
    }

    /** Mueve una ficha del jugador en turno con el dado que ya tiró. */
    public void moverFicha(int numero) throws JugadaInvalidaException {
        if (terminada || !yaTiro) {
            throw new JugadaInvalidaException("Primero lanza el dado.");
        }
        Jugador jugador = enTurno;
        int antes = jugador.getPosicion(numero);
        String motivo = ReglasParchis.motivoParaNoMover(antes, dado);
        if (motivo != null) {
            throw new JugadaInvalidaException(motivo);
        }

        int posicion = ReglasParchis.nuevaPosicion(antes, dado);
        jugador.setPosicion(numero, posicion);
        if (antes == ReglasParchis.EN_BASE) {
            bitacora.agregar(jugador.getNombre() + " sacó la ficha " + (numero + 1) + " de su base.");
        } else {
            bitacora.agregar(jugador.getNombre() + " avanzó " + dado + (dado == 1 ? " casilla" : " casillas")
                    + " con la ficha " + (numero + 1) + ".");
        }
        jugadas.add(jugador.getColor() + "," + numero + "," + posicion);

        capturarEn(jugador, posicion);
        notificador.enviar("MOVIMIENTO;" + jugador.getColor() + ";" + numero + ";" + posicion);

        if (posicion == ReglasParchis.META) {
            bitacora.agregar("La ficha " + (numero + 1) + " de " + jugador.getNombre() + " llegó a la meta.");
        }
        yaTiro = false;
        if (jugador.llegaronTodasALaMeta()) {
            terminar(jugador);
            return;
        }
        terminarTiro();
        avisarCambio();
    }

    /** Si la ficha cayó en una ficha rival del recorrido común, la rival regresa a su base. */
    private void capturarEn(Jugador jugador, int posicion) {
        if (!ReglasParchis.enRecorridoComun(posicion)) {
            return;
        }
        int casilla = ReglasParchis.casillaDelTablero(jugador.getColor(), posicion);
        Jugador rival = rivalDe(jugador);
        for (int j = 0; j < rival.cantidadDeFichas(); j++) {
            int posicionRival = rival.getPosicion(j);
            if (ReglasParchis.enRecorridoComun(posicionRival)
                    && ReglasParchis.casillaDelTablero(rival.getColor(), posicionRival) == casilla) {
                rival.regresarABase(j);
                bitacora.agregar("¡Capturó la ficha " + (j + 1) + " de " + rival.getNombre() + "! Regresa a su base.");
                notificador.enviar("CAPTURA;" + rival.getColor() + ";" + j);
            }
        }
    }

    /** Con 6 se vuelve a tirar; si no, pasa el turno. */
    private void terminarTiro() {
        if (ReglasParchis.vuelveATirar(dado, seisSeguidos)) {
            bitacora.agregar("Sacó 6: vuelve a tirar.");
        } else {
            cambiarTurno();
        }
    }

    private void cambiarTurno() {
        enTurno = rivalDe(enTurno);
        seisSeguidos = 0;
        bitacora.agregar("Turno de " + enTurno.getNombre() + " (" + enTurno.getColor() + ").");
    }

    private void terminar(Jugador ganador) {
        terminada = true;
        bitacora.agregar("¡" + ganador.getNombre() + " ganó la partida!");
        notificador.enviar("FIN;" + ganador.getColor());
        guardarEstadisticas(ganador);
        for (ObservadorPartida observador : observadores) {
            observador.partidaTerminada(ganador);
        }
    }

    private void guardarEstadisticas(Jugador ganador) {
        if (!estadisticas.estaDisponible()) {
            bitacora.agregar("Sin base de datos: no se guardaron las estadísticas.");
            return;
        }
        try {
            estadisticas.registrarPartida(ganador.getColor());
        } catch (EstadisticasException e) {
            bitacora.agregar("No se guardaron las estadísticas: " + e.getMessage());
        }
    }

    public void nuevaPartida() {
        rojo.regresarTodasABase();
        azul.regresarTodasABase();
        enTurno = rojo;
        dado = 0;
        yaTiro = false;
        seisSeguidos = 0;
        terminada = false;
        jugadas.clear();
        bitacora.limpiar();
        bitacora.agregar("Nueva partida. Empieza " + enTurno.getNombre() + ".");
        avisarCambio();
    }

    private void avisarCambio() {
        for (ObservadorPartida observador : observadores) {
            observador.estadoCambiado();
        }
    }

    // ------------------------------------------------------------------ consultas para la vista

    public Jugador getRojo() {
        return rojo;
    }

    public Jugador getAzul() {
        return azul;
    }

    public List<Jugador> getJugadores() {
        return List.of(rojo, azul);
    }

    public Jugador getJugadorEnTurno() {
        return enTurno;
    }

    public Jugador rivalDe(Jugador jugador) {
        return jugador == rojo ? azul : rojo;
    }

    public int getDado() {
        return dado;
    }

    public boolean isYaTiro() {
        return yaTiro;
    }

    public boolean isTerminada() {
        return terminada;
    }

    /** Si la vista debe resaltar esa ficha como una que se puede mover ahora. */
    public boolean esMovible(Jugador jugador, int numero) {
        return jugador == enTurno && yaTiro && !terminada
                && ReglasParchis.puedeMover(jugador.getPosicion(numero), dado);
    }
}
