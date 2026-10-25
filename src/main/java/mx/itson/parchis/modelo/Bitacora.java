package mx.itson.parchis.modelo;

/** Donde la partida va contando lo que pasa. La partida no sabe si es un JTextArea, la consola o un archivo. */
public interface Bitacora {

    void agregar(String texto);

    void limpiar();
}
