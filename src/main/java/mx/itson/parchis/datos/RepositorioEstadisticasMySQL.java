package mx.itson.parchis.datos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import mx.itson.parchis.modelo.ColorJugador;
import mx.itson.parchis.modelo.Estadistica;
import mx.itson.parchis.modelo.EstadisticasException;
import mx.itson.parchis.modelo.RepositorioEstadisticas;

/** Las estadísticas en la tabla {@code estadisticas} de MySQL. Todo el SQL del juego está aquí. */
public class RepositorioEstadisticasMySQL implements RepositorioEstadisticas {

    private final String url;
    private final String usuario;
    private final String clave;
    private final boolean disponible;

    /** Crea la tabla si no existe; si no hay servidor, queda como no disponible. */
    public RepositorioEstadisticasMySQL(String url, String usuario, String clave) {
        this.url = url;
        this.usuario = usuario;
        this.clave = clave;
        this.disponible = crearTabla();
    }

    private Connection conectar() throws SQLException {
        return DriverManager.getConnection(url, usuario, clave);
    }

    private boolean crearTabla() {
        try (Connection conexion = conectar();
                Statement sentencia = conexion.createStatement()) {
            sentencia.executeUpdate("CREATE TABLE IF NOT EXISTS estadisticas ("
                    + "color VARCHAR(10) PRIMARY KEY, "
                    + "jugadas INT NOT NULL DEFAULT 0, "
                    + "ganadas INT NOT NULL DEFAULT 0)");
            return true;
        } catch (SQLException e) {
            System.out.println("Sin base de datos: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean estaDisponible() {
        return disponible;
    }

    @Override
    public List<Estadistica> leer() throws EstadisticasException {
        List<Estadistica> lista = new ArrayList<>();
        try (Connection conexion = conectar();
                Statement sentencia = conexion.createStatement();
                ResultSet resultado = sentencia.executeQuery(
                        "SELECT color, jugadas, ganadas FROM estadisticas ORDER BY color DESC")) {
            while (resultado.next()) {
                lista.add(new Estadistica(resultado.getString("color"),
                        resultado.getInt("jugadas"), resultado.getInt("ganadas")));
            }
        } catch (SQLException e) {
            throw new EstadisticasException(e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public void registrarPartida(ColorJugador ganador) throws EstadisticasException {
        String sql = "INSERT INTO estadisticas (color, jugadas, ganadas) VALUES (?, 1, ?) "
                + "ON DUPLICATE KEY UPDATE jugadas = jugadas + 1, ganadas = ganadas + ?";
        try (Connection conexion = conectar();
                PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            for (ColorJugador color : ColorJugador.values()) {
                int gano = color == ganador ? 1 : 0;
                sentencia.setString(1, color.toString());
                sentencia.setInt(2, gano);
                sentencia.setInt(3, gano);
                sentencia.executeUpdate();
            }
        } catch (SQLException e) {
            throw new EstadisticasException(e.getMessage(), e);
        }
    }
}
