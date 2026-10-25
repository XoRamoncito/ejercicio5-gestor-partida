package mx.itson.parchis.red;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/** Modo espectador: escribe en la consola las jugadas que llegan por la red. */
public class EspectadorRed {

    private final int puerto;

    public EspectadorRed(int puerto) {
        this.puerto = puerto;
    }

    public void escuchar() {
        System.out.println("Esperando jugadas en el puerto " + puerto + "...");
        try (ServerSocket servidor = new ServerSocket(puerto)) {
            while (true) {
                try (Socket cliente = servidor.accept();
                        BufferedReader entrada = new BufferedReader(
                                new InputStreamReader(cliente.getInputStream(), StandardCharsets.UTF_8))) {
                    String linea;
                    while ((linea = entrada.readLine()) != null) {
                        System.out.println("Llegó: " + linea);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("No se pudo abrir el puerto " + puerto + ": " + e.getMessage());
        }
    }
}
