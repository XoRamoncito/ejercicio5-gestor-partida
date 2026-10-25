package mx.itson.parchis.vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import mx.itson.parchis.modelo.Estadistica;
import mx.itson.parchis.modelo.EstadisticasException;
import mx.itson.parchis.modelo.JugadaInvalidaException;
import mx.itson.parchis.modelo.Jugador;
import mx.itson.parchis.modelo.ObservadorPartida;
import mx.itson.parchis.modelo.Partida;
import mx.itson.parchis.modelo.RepositorioEstadisticas;

/** El panel de la derecha: turno, dado, botones, bitácora y estadísticas. */
public class PanelLateral extends JPanel implements ObservadorPartida {

    private final Partida partida;
    private final RepositorioEstadisticas estadisticas;

    private final JLabel etiquetaTurno = new JLabel(" ");
    private final JLabel etiquetaDado = new JLabel("Dado: -");
    private final JLabel etiquetaEstadisticas = new JLabel(" ");
    private final JButton botonDado = new JButton("Lanzar dado");
    private final JButton botonNueva = new JButton("Nueva partida");

    public PanelLateral(Partida partida, BitacoraSwing bitacora, RepositorioEstadisticas estadisticas) {
        super(new BorderLayout(0, 12));
        this.partida = partida;
        this.estadisticas = estadisticas;
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        setPreferredSize(new Dimension(300, 620));

        JPanel arriba = new JPanel(new GridLayout(0, 1, 0, 8));
        JLabel titulo = new JLabel("Parchís");
        titulo.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 26));
        etiquetaTurno.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 17));
        etiquetaDado.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 17));
        botonDado.addActionListener(e -> lanzarDado());
        botonNueva.addActionListener(e -> partida.nuevaPartida());
        arriba.add(titulo);
        arriba.add(etiquetaTurno);
        arriba.add(etiquetaDado);
        arriba.add(botonDado);
        arriba.add(botonNueva);
        add(arriba, BorderLayout.NORTH);

        add(new JScrollPane(bitacora.getArea()), BorderLayout.CENTER);

        etiquetaEstadisticas.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        add(etiquetaEstadisticas, BorderLayout.SOUTH);
    }

    private void lanzarDado() {
        try {
            partida.lanzarDado();
        } catch (JugadaInvalidaException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    @Override
    public void estadoCambiado() {
        actualizarEtiquetas();
    }

    @Override
    public void partidaTerminada(Jugador ganador) {
        mostrarEstadisticas();
        actualizarEtiquetas();
        JOptionPane.showMessageDialog(this, "¡Ganó " + ganador.getNombre() + " (" + ganador.getColor() + ")!");
    }

    private void actualizarEtiquetas() {
        Jugador jugador = partida.getJugadorEnTurno();
        if (partida.isTerminada()) {
            etiquetaTurno.setText("Partida terminada");
            etiquetaTurno.setForeground(Color.DARK_GRAY);
        } else {
            etiquetaTurno.setText("Turno: " + jugador.getNombre() + " (" + jugador.getColor() + ")");
            etiquetaTurno.setForeground(Colores.de(jugador.getColor()));
        }
        int dado = partida.getDado();
        etiquetaDado.setText("Dado: " + (dado == 0 ? "-" : String.valueOf(dado))
                + (partida.isYaTiro() ? " · elige una ficha" : ""));
        botonDado.setEnabled(!partida.isTerminada() && !partida.isYaTiro());
    }

    public void mostrarEstadisticas() {
        if (!estadisticas.estaDisponible()) {
            etiquetaEstadisticas.setText("<html>Estadísticas: sin base de datos</html>");
            return;
        }
        StringBuilder texto = new StringBuilder("<html><b>Estadísticas</b>");
        try {
            for (Estadistica fila : estadisticas.leer()) {
                texto.append("<br>").append(fila.color()).append(": ").append(fila.ganadas())
                        .append(" ganadas de ").append(fila.jugadas());
            }
        } catch (EstadisticasException e) {
            texto.append("<br>No se pudieron leer: ").append(e.getMessage());
        }
        etiquetaEstadisticas.setText(texto.append("</html>").toString());
    }
}
