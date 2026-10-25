package mx.itson.parchis;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import mx.itson.parchis.datos.RepositorioEstadisticasMySQL;
import mx.itson.parchis.modelo.ColorJugador;
import mx.itson.parchis.modelo.Jugador;
import mx.itson.parchis.modelo.Notificador;
import mx.itson.parchis.modelo.Partida;
import mx.itson.parchis.modelo.RepositorioEstadisticas;
import mx.itson.parchis.red.EspectadorRed;
import mx.itson.parchis.red.NotificadorSocket;
import mx.itson.parchis.vista.BitacoraSwing;
import mx.itson.parchis.vista.PanelLateral;
import mx.itson.parchis.vista.TableroPanel;

/**
 * Arranca el Parchís: crea cada pieza y las conecta. Es el único lugar que conoce las clases
 * concretas (el socket, MySQL, Swing); para cambiar una basta con cambiar una línea aquí.
 */
public final class GestorPartida {

    // Red: a dónde se avisa cada jugada
    private static final String HOST_RIVAL = "localhost";
    private static final int PUERTO_RIVAL = 5000;

    // Base de datos
    private static final String URL_BD = "jdbc:mysql://localhost:3306/parchis?createDatabaseIfNotExist=true&connectTimeout=2000";
    private static final String USUARIO_BD = "root";
    private static final String CLAVE_BD = "";

    private GestorPartida() {
    }

    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("espectador")) {
            new EspectadorRed(PUERTO_RIVAL).escuchar();
            return;
        }
        SwingUtilities.invokeLater(GestorPartida::abrirVentana);
    }

    private static void abrirVentana() {
        RepositorioEstadisticas estadisticas = new RepositorioEstadisticasMySQL(URL_BD, USUARIO_BD, CLAVE_BD);
        BitacoraSwing bitacora = new BitacoraSwing();
        Notificador notificador = new NotificadorSocket(HOST_RIVAL, PUERTO_RIVAL, bitacora);
        Partida partida = new Partida(
                new Jugador("Jugador 1", ColorJugador.ROJO),
                new Jugador("Jugador 2", ColorJugador.AZUL),
                bitacora, notificador, estadisticas);

        TableroPanel tablero = new TableroPanel(partida);
        PanelLateral lateral = new PanelLateral(partida, bitacora, estadisticas);
        partida.agregarObservador(tablero);
        partida.agregarObservador(lateral);

        JFrame ventana = new JFrame("Parchís");
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.add(tablero, BorderLayout.CENTER);
        ventana.add(lateral, BorderLayout.EAST);
        ventana.pack();
        ventana.setLocationRelativeTo(null);
        lateral.mostrarEstadisticas();
        partida.iniciar();
        ventana.setVisible(true);
    }
}
