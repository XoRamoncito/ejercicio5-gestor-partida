package mx.itson.parchis.vista;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import mx.itson.parchis.modelo.ColorJugador;
import mx.itson.parchis.modelo.JugadaInvalidaException;
import mx.itson.parchis.modelo.Jugador;
import mx.itson.parchis.modelo.ObservadorPartida;
import mx.itson.parchis.modelo.Partida;
import mx.itson.parchis.modelo.ReglasParchis;

/** Dibuja el tablero y las fichas, y convierte un clic sobre una ficha en una jugada. */
public class TableroPanel extends JPanel implements ObservadorPartida {

    private final Partida partida;

    // Medidas del tablero en pantalla; se calculan al dibujar
    private int tamCelda;
    private int origenX;
    private int origenY;

    public TableroPanel(Partida partida) {
        this.partida = partida;
        setPreferredSize(new Dimension(620, 620));
        setBackground(Colores.FONDO);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                elegirFicha(e.getX(), e.getY());
            }
        });
    }

    @Override
    public void estadoCambiado() {
        repaint();
    }

    @Override
    public void partidaTerminada(Jugador ganador) {
        repaint();
    }

    private void elegirFicha(int x, int y) {
        if (partida.isTerminada() || !partida.isYaTiro()) {
            return;
        }
        Jugador jugador = partida.getJugadorEnTurno();
        int radio = tamCelda / 2;
        for (int i = 0; i < jugador.cantidadDeFichas(); i++) {
            int[] centro = centroEnPantalla(jugador.getColor(), i, jugador.getPosicion(i));
            int dx = x - centro[0];
            int dy = y - centro[1];
            if (dx * dx + dy * dy <= radio * radio) {
                try {
                    partida.moverFicha(i);
                } catch (JugadaInvalidaException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage());
                }
                return;
            }
        }
    }

    // ------------------------------------------------------------------ dibujo

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int celdas = GeometriaTablero.CELDAS;
        tamCelda = (Math.min(getWidth(), getHeight()) - 20) / celdas;
        origenX = (getWidth() - tamCelda * celdas) / 2;
        origenY = (getHeight() - tamCelda * celdas) / 2;

        // Bases: las de los colores que juegan en su color; las de abajo no se usan
        for (ColorJugador color : ColorJugador.values()) {
            int[] esquina = GeometriaTablero.base(color);
            dibujarBase(g2, esquina[0], esquina[1], Colores.de(color));
        }
        dibujarBase(g2, 11, 0, Colores.BASE_SIN_USAR);
        dibujarBase(g2, 11, 11, Colores.BASE_SIN_USAR);

        // Recorrido, con la salida de cada jugador en su color
        for (int i = 0; i < ReglasParchis.TOTAL_CASILLAS; i++) {
            int[] celda = GeometriaTablero.casilla(i);
            pintarCelda(g2, celda[0], celda[1], colorDeCasilla(i));
        }

        // Pasillos hacia la meta
        for (int i = 0; i < GeometriaTablero.LARGO_PASILLO; i++) {
            for (ColorJugador color : ColorJugador.values()) {
                int[] celda = GeometriaTablero.pasillo(color, i);
                pintarCelda(g2, celda[0], celda[1], Colores.claroDe(color));
            }
            // Los pasillos de los colores que no juegan
            pintarCelda(g2, 9, 17 - i, Colores.PASILLO_SIN_USAR);
            pintarCelda(g2, 17 - i, 9, Colores.PASILLO_SIN_USAR);
        }

        dibujarMeta(g2);
        for (Jugador jugador : partida.getJugadores()) {
            dibujarFichas(g2, jugador);
        }
    }

    private Color colorDeCasilla(int casilla) {
        for (ColorJugador color : ColorJugador.values()) {
            if (casilla == color.getCasillaSalida()) {
                return Colores.claroDe(color);
            }
        }
        return Color.WHITE;
    }

    private void dibujarBase(Graphics2D g2, int fila, int columna, Color color) {
        g2.setColor(color);
        g2.fillRect(origenX + columna * tamCelda, origenY + fila * tamCelda, 8 * tamCelda, 8 * tamCelda);
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(origenX + (columna + 1) * tamCelda, origenY + (fila + 1) * tamCelda,
                6 * tamCelda, 6 * tamCelda, tamCelda, tamCelda);
    }

    private void pintarCelda(Graphics2D g2, int fila, int columna, Color relleno) {
        int x = origenX + columna * tamCelda;
        int y = origenY + fila * tamCelda;
        g2.setColor(relleno);
        g2.fillRect(x, y, tamCelda, tamCelda);
        g2.setColor(Colores.BORDE_CELDA);
        g2.setStroke(new BasicStroke(1f));
        g2.drawRect(x, y, tamCelda, tamCelda);
    }

    /** El cuadro de 3 x 3 del centro. */
    private void dibujarMeta(Graphics2D g2) {
        g2.setColor(Colores.META);
        g2.fillRect(origenX + 8 * tamCelda, origenY + 8 * tamCelda, 3 * tamCelda, 3 * tamCelda);
        g2.setColor(Colores.TEXTO_META);
        g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, Math.max(8, tamCelda * 3 / 10)));
        FontMetrics medidas = g2.getFontMetrics();
        g2.drawString("META", origenX + 10 * tamCelda + tamCelda / 2 - medidas.stringWidth("META") / 2,
                origenY + 10 * tamCelda + tamCelda / 2 + medidas.getAscent() / 2 - 1);
    }

    private void dibujarFichas(Graphics2D g2, Jugador jugador) {
        int radio = tamCelda * 2 / 5;
        for (int i = 0; i < jugador.cantidadDeFichas(); i++) {
            int[] centro = centroEnPantalla(jugador.getColor(), i, jugador.getPosicion(i));

            // Resaltar las fichas que se pueden mover
            if (partida.esMovible(jugador, i)) {
                g2.setColor(Colores.FICHA_MOVIBLE);
                g2.setStroke(new BasicStroke(3f));
                g2.drawOval(centro[0] - radio - 4, centro[1] - radio - 4, (radio + 4) * 2, (radio + 4) * 2);
            }

            g2.setColor(Colores.de(jugador.getColor()));
            g2.fillOval(centro[0] - radio, centro[1] - radio, radio * 2, radio * 2);
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(2f));
            g2.drawOval(centro[0] - radio, centro[1] - radio, radio * 2, radio * 2);
            g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, Math.max(9, radio)));
            String numero = String.valueOf(i + 1);
            FontMetrics medidas = g2.getFontMetrics();
            g2.drawString(numero, centro[0] - medidas.stringWidth(numero) / 2,
                    centro[1] + medidas.getAscent() / 2 - 1);
        }
    }

    /** Centro en pixeles de una ficha. */
    private int[] centroEnPantalla(ColorJugador color, int numero, int posicion) {
        double[] filaColumna = GeometriaTablero.centroFicha(color, numero, posicion);
        return new int[] {
            (int) Math.round(origenX + filaColumna[1] * tamCelda),
            (int) Math.round(origenY + filaColumna[0] * tamCelda)
        };
    }
}
