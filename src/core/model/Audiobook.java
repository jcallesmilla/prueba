package core.model;

import core.model.interfaces.IAudiobook;
import core.model.interfaces.IAuthor;
import core.model.interfaces.IPublisher;
import core.model.interfaces.INarrator;
import java.util.List;

/**
 * Audiolibro.
 */
public class Audiobook extends Book implements IAudiobook {

    private int duracion;
    private INarrator narrador;

    public Audiobook(String titulo, List<IAuthor> autores, String isbn, String genero, String formato, double valor,
            IPublisher editorial, int duracion, INarrator narrador) {
        super(titulo, autores, isbn, genero, formato, valor, editorial);
        this.duracion = duracion;
        this.narrador = narrador;
    }

    @Override
    public int getDuracion() {
        return duracion;
    }

    @Override
    public void setDuracion(int duracion) {
        this.duracion = duracion;
    }

    @Override
    public INarrator getNarrador() {
        return narrador;
    }

    @Override
    public void setNarrador(INarrator narrador) {
        this.narrador = narrador;
    }

    @Override
    public IAudiobook copiar() {
        INarrator narradorCopia = null;
        if (this.narrador != null) {
            narradorCopia = new Narrator(this.narrador.getId(), this.narrador.getNombres(),
                    this.narrador.getApellidos());
        }
        return new Audiobook(this.titulo, this.copiarAutores(), this.isbn, this.genero, this.formato, this.valor,
                this.copiarEditorial(), this.duracion, narradorCopia);
    }
}
