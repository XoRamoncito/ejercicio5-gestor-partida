package mx.itson.parchis.red;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import mx.itson.parchis.modelo.Bitacora;
import mx.itson.parchis.modelo.Notificador;

/** Avisa cada jugada a la computadora del rival por un socket. Si no contesta, se sigue jugando aquí. */
public class NotificadorSocket implements Notificador {

    private static final int ESPERA_MS = 300;

    private final String host;
    private final int puerto;
    private final Bitacora bitacora;
    private boolean avisoSinRed = false;

    public NotificadorSocket(String host, int puerto, Bitacora bitacora) {
        this.host = host;
        this.puerto = puerto;
        this.bitacora = bitacora;
    }

    @Override
    public void enviar(String mensaje) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, puerto), ESPERA_MS);
            PrintWriter salida = new PrintWriter(
                    new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
            salida.println(mensaje);
        } catch (IOException e) {
            if (!avisoSinRed) {
                avisoSinRed = true;
                bitacora.agregar("Sin conexión con " + host + ":" + puerto
                        + ". Se sigue jugando en esta computadora.");
            }
        }
    }
}
