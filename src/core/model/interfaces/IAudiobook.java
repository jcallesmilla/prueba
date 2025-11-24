package core.model.interfaces;

public interface IAudiobook extends IBook {
    int getDuracion();

    void setDuracion(int duracion);

    INarrator getNarrador();

    void setNarrador(INarrator narrador);

    IAudiobook copiar();
}
