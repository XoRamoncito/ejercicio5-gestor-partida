package mx.itson.parchis.vista;

import java.awt.Font;
import javax.swing.JTextArea;
import mx.itson.parchis.modelo.Bitacora;

/** La bitácora en un área de texto de la ventana. */
public class BitacoraSwing implements Bitacora {

    private final JTextArea area = new JTextArea();

    public BitacoraSwing() {
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
    }

    JTextArea getArea() {
        return area;
    }

    @Override
    public void agregar(String texto) {
        area.append(texto + "\n");
        area.setCaretPosition(area.getDocument().getLength());
    }

    @Override
    public void limpiar() {
        area.setText("");
    }
}
